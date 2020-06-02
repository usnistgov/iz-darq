import { Component, OnInit, Input } from '@angular/core';
import { IFacilityDescriptor } from '../../model/facility.model';
import { BehaviorSubject, Observable, combineLatest, of } from 'rxjs';
import { map, flatMap } from 'rxjs/operators';
import { DamWidgetComponent, RxjsStoreHelperService, MessageType, InsertResourcesInCollection } from 'ngx-dam-framework';
import { FacilityService } from '../../services/facility.service';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { NameDialogComponent } from '../../../shared/components/name-dialog/name-dialog.component';
import { EntityType } from 'src/app/modules/shared/model/entity.model';
import { GoToEntity } from '../../../shared/store/core.actions';

@Component({
  selector: 'app-facility-list',
  templateUrl: './facility-list.component.html',
  styleUrls: ['./facility-list.component.scss']
})
export class FacilityListComponent implements OnInit {

  @Input()
  set facilities(c: IFacilityDescriptor[]) {
    this.facilitiesSubject.next(c);
  }

  get facilities() {
    return this.facilitiesSubject.getValue();
  }

  filtered$: Observable<IFacilityDescriptor[]>;
  facilitiesSubject: BehaviorSubject<IFacilityDescriptor[]>;
  filterTextSubject: BehaviorSubject<string>;

  constructor(
    public widget: DamWidgetComponent,
    private facilityService: FacilityService,
    private helper: RxjsStoreHelperService,
    private dialog: MatDialog,
    private store: Store<any>,
  ) {
    this.facilitiesSubject = new BehaviorSubject([]);
    this.filterTextSubject = new BehaviorSubject('');

    this.filtered$ = combineLatest([
      this.facilitiesSubject,
      this.filterTextSubject,
    ]).pipe(
      map(([facilities, text]) => {
        return facilities.filter((conf) => {
          return conf.name.includes(text);
        });
      }),
    );
  }

  get filterText() {
    return this.filterTextSubject.getValue();
  }

  set filterText(text: string) {
    this.filterTextSubject.next(text);
  }

  createFacility() {
    this.dialog.open(NameDialogComponent, {
      data: {
        title: 'Create Facility'
      },
    }).afterClosed().pipe(
      flatMap((name) => {
        if (name) {
          return this.helper.getMessageAndHandle<IFacilityDescriptor>(
            this.store,
            () => {
              return this.facilityService.create({
                id: undefined,
                name,
                type: EntityType.FACILITY,
                size: 0,
              });
            },
            (message) => {
              return message.status === MessageType.SUCCESS ? [new InsertResourcesInCollection({
                key: 'facilities',
                values: [
                  message.data,
                ],
              }),
              new GoToEntity({
                type: EntityType.FACILITY,
                id: message.data.id,
              })] : [];
            },
          );
        }
        return of();
      }),
    ).subscribe();
  }


  ngOnInit(): void {
  }

}
