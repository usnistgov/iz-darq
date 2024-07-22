import { Component, OnInit, forwardRef, AfterViewInit } from '@angular/core';
import {
  DamWidgetComponent,
  IDamDataModel,
  selectRouteParams,
  TurnOffLoader,
  selectIsAdmin,
  RxjsStoreHelperService,
} from 'ngx-dam-framework';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { Observable, combineLatest, EMPTY, of } from 'rxjs';
import { IUserFacilityDescriptor } from '../../../facility/model/facility.model';
import { selectCurrentFacility, selectUserFacilitiesSorted } from '../../store/core.selectors';
import { map, take, flatMap } from 'rxjs/operators';
import { AdfMergeDialogComponent } from '../adf-merge-dialog/adf-merge-dialog.component';
import { FileService } from '../../services/file.service';

export const ADF_WIDGET = 'ADF_WIDGET';

@Component({
  selector: 'app-adf-widget',
  templateUrl: './adf-widget.component.html',
  styleUrls: ['./adf-widget.component.scss'],
  providers: [
    { provide: DamWidgetComponent, useExisting: forwardRef(() => AdfWidgetComponent) },
  ],
})
export class AdfWidgetComponent extends DamWidgetComponent implements OnInit, AfterViewInit {

  facilities$: Observable<IUserFacilityDescriptor[]>;
  currentEditorFacility$: Observable<IUserFacilityDescriptor>;
  facilityIdFromURL$: Observable<string>;
  isAdmin$: Observable<boolean>;

  constructor(
    store: Store<IDamDataModel>,
    dialog: MatDialog,
    private fileService: FileService,
    private helper: RxjsStoreHelperService,
  ) {
    super(ADF_WIDGET, store, dialog);
    this.facilities$ = store.select(selectUserFacilitiesSorted);
    this.currentEditorFacility$ = this.store.select(selectCurrentFacility);
    this.facilityIdFromURL$ = this.store.select(selectRouteParams).pipe(
      map((p) => p['facility']),
    );
    this.isAdmin$ = this.store.select(selectIsAdmin);
  }

  merge() {
    combineLatest([
      this.fileService.getAll(),
      this.currentEditorFacility$,
      this.facilities$,
    ]).pipe(
      take(1),
      flatMap(([files, current, facilities]) => {
        return this.dialog.open(AdfMergeDialogComponent, {
          data: {
            files,
            facilities,
          }
        }).afterClosed().pipe(
          flatMap((mergeRequest) => {
            if (mergeRequest) {
              return this.helper.getMessageAndHandle(
                this.store,
                () => {
                  return this.fileService.mergeFiles(mergeRequest);
                },
                (message) => {
                  if (message.status === 'SUCCESS' && mergeRequest.facilityId === current.id) {
                    window.location.reload();
                  }
                  return EMPTY;
                }
              );
            }
            return EMPTY;
          })
        );
      }),
    ).subscribe();
  }

  ngAfterViewInit(): void {
    this.store.dispatch(new TurnOffLoader());
  }

  ngOnInit(): void {
  }

}

