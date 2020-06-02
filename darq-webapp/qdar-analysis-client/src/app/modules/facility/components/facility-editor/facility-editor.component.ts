import { Component, OnInit, OnDestroy } from '@angular/core';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { IEditorMetadata, DamAbstractEditorComponent, EditorSave, IWorkspaceCurrent, InsertResourcesInCollection, MessageService } from 'ngx-dam-framework';
import { Observable, Subscription, of, throwError } from 'rxjs';
import { IFacility } from '../../model/facility.model';
import { map, take, flatMap, concatMap, catchError } from 'rxjs/operators';
import { selectFacility } from '../../store/core.selectors';
import { MatDialog } from '@angular/material/dialog';
import { UserListComponent } from '../user-list/user-list.component';
import { IUserResource } from 'src/app/modules/core/model/user.model';
import { selectUsers } from '../../../core/store/core.selectors';
import { FacilityService } from '../../services/facility.service';

export const FACILITY_EDITOR_METADATA: IEditorMetadata = {
  id: 'FACILITY_EDITOR',
  title: 'Facility'
};

@Component({
  selector: 'app-facility-editor',
  templateUrl: './facility-editor.component.html',
  styleUrls: ['./facility-editor.component.scss']
})
export class FacilityEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  facility: IFacility;
  users$: Observable<IUserResource[]>;

  sub: Subscription;
  constructor(
    store: Store<any>,
    actions$: Actions,
    private dialog: MatDialog,
    private facilityService: FacilityService,
    private messageService: MessageService,
  ) {
    super(
      FACILITY_EDITOR_METADATA,
      actions$,
      store,
    );
    this.users$ = this.store.select(selectUsers);
    this.sub = this.currentSynchronized$.pipe(
      map((value) => {
        this.facility = {
          ...value,
        };
      })
    ).subscribe();
  }

  addUsers() {
    this.users$.pipe(
      take(1),
      flatMap((users) => {
        return this.dialog.open(UserListComponent, {
          data: {
            users,
            selection: [...this.facility.members],
          }
        }).afterClosed().pipe(
          map((selection) => {
            if (selection) {
              this.userChange([
                ...this.facility.members,
                ...selection
              ]);
            }
          })
        );
      })
    ).subscribe();
  }

  removeUser(i) {
    const clone = [...this.facility.members];
    clone.splice(i, 1);
    this.userChange(clone);
  }

  nameChange(value: string) {
    this.facility = {
      ...this.facility,
      name: value,
    };

    this.editorChange(this.facility, this.facility.name && this.facility.name !== '');
  }

  userChange(list: string[]) {
    this.facility = {
      ...this.facility,
      members: [...list],
    };
    this.editorChange(this.facility, this.facility.name && this.facility.name !== '');
  }

  ngOnDestroy(): void {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return this.current$.pipe(
      take(1),
      concatMap((current: IWorkspaceCurrent) => {
        return this.facilityService.save(current.data).pipe(
          flatMap((message) => {
            return [
              new InsertResourcesInCollection({
                key: 'facilities',
                values: [this.facilityService.getDescriptor(message.data)],
              }),
              this.messageService.messageToAction(message),
            ];
          }),
          catchError((error) => {
            return throwError(this.messageService.actionFromError(error));
          })
        );
      }),
    );
  }

  editorDisplayNode(): Observable<any> {
    return this.store.select(selectFacility).pipe(
      map(facility => {
        return {
          name: facility.name,
        };
      }),
    );
  }

  onDeactivate(): void {
  }


  ngOnInit(): void {
  }

}
