import { Component, OnInit, OnDestroy } from '@angular/core';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { IEditorMetadata, DamAbstractEditorComponent, EditorSave, IWorkspaceCurrent, InsertResourcesInCollection, MessageService } from '@usnistgov/ngx-dam-framework-legacy';
import { Observable, Subscription, throwError, combineLatest } from 'rxjs';
import { IFacilityContent } from '../../model/facility.model';
import { map, take, flatMap, concatMap, catchError } from 'rxjs/operators';
import { selectFacility } from '../../store/core.selectors';
import { MatDialog } from '@angular/material/dialog';
import { UserListComponent } from '../user-list/user-list.component';
import { FacilityService } from '../../services/facility.service';
import { IUser } from '../../../core/model/user.model';
import { selectUsers, selectUserById } from '../../../shared/store/core.selectors';

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

  facility: IFacilityContent;
  users$: Observable<IUser[]>;

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
      flatMap((value) => {
        this.facility = {
          ...value,
          members: [],
        };
        return combineLatest(
          value.members ? [...value.members.map((id) => this.store.select(selectUserById, { id }).pipe(take(1)))] : []
        ).pipe(
          map((members: IUser[]) => {
            this.facility = {
              ...this.facility,
              members,
            };
          })
        );
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
              combineLatest([
                ...selection.map((id) => this.store.select(selectUserById, { id }).pipe(take(1)))
              ]).pipe(
                map((members) => {
                  this.userChange([
                    ...this.facility.members,
                    ...(members as any),
                  ]);
                })
              ).subscribe();
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

  userChange(list: IUser[]) {
    this.facility = {
      ...this.facility,
      members: [...list],
    };
    this.editorChange({
      ...this.facility,
      members: this.facility.members.map((v) => v.id),
    }, this.facility.name && this.facility.name !== '');
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
