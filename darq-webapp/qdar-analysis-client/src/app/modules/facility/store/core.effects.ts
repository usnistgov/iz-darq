import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { catchError, map, concatMap, flatMap } from 'rxjs/operators';
import { combineLatest } from 'rxjs';
import {
  CoreActionTypes,
  LoadFacilitiesSuccess,
  LoadFacilitiesFailure,
  LoadFacilitySuccess,
  LoadFacilityFailure,
  OpenFacilityEditor,
} from './core.actions';
import { Store } from '@ngrx/store';
import { FacilityService } from '../services/facility.service';
import { FACILITY_WIDGET } from '../components/facility-widget/facility-widget.component';
import {
  MessageService,
  DamWidgetEffect,
  LoadResourcesInRepository,
  LoadPayloadData,
  OpenEditor,
  DamActionTypes,
  GlobalSave,
  EditorSave,
} from 'ngx-dam-framework';
import { IFacilityDescriptor } from '../model/facility.model';
import { handleError } from '../../shared/services/helper.functions';
import { UserService } from '../../core/services/user.service';
import { IUser } from '../../core/model/user.model';

@Injectable()
export class CoreEffects extends DamWidgetEffect {

  @Effect()
  save$ = this.actions$.pipe(
    ofType(DamActionTypes.GlobalSave),
    map((action: GlobalSave) => {
      return new EditorSave({});
    })
  );

  @Effect()
  loadFacilities$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadFacilities),
    concatMap(() => {
      return this.facilityService.getList().pipe(
        flatMap((list) => {
          return [
            new LoadResourcesInRepository<IFacilityDescriptor>({
              collections: [{
                key: 'facilities',
                values: list,
              }]
            }),
            new LoadFacilitiesSuccess(list),
          ];
        }),
        catchError(handleError(this.messageService, LoadFacilitiesFailure)),
      );
    })
  );

  @Effect()
  openFacilityEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenFacilityEditor),
    concatMap((action: OpenFacilityEditor) => {
      return combineLatest([
        this.facilityService.getById(action.payload.id),
        this.userService.getListOfAllUsers(),
      ]).pipe(
        flatMap(([facility, users]) => {
          return [
            new LoadPayloadData(facility),
            new LoadResourcesInRepository<IUser>({
              collections: [{
                key: 'users',
                values: users,
              }]
            }),
            new LoadFacilitySuccess(facility),
            new OpenEditor({
              id: action.payload.id,
              editor: action.payload.editor,
              display: { name: facility.name },
              initial: facility,
            }),
          ];
        }),
        catchError(handleError(this.messageService, LoadFacilityFailure)),
      );
    }),
  );



  constructor(
    actions$: Actions,
    private store: Store<any>,
    private userService: UserService,
    private facilityService: FacilityService,
    private messageService: MessageService,
  ) {
    super(FACILITY_WIDGET, actions$);
  }

}
