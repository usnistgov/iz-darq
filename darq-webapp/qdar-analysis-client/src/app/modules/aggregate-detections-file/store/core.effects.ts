import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { catchError, concatMap, flatMap } from 'rxjs/operators';
import { of, combineLatest } from 'rxjs';
import {
  CoreActionTypes,
  CoreActions,
  LoadADFile,
  LoadADFileSuccess,
  LoadADFileFailure,
} from './core.actions';
import { FileService } from '../services/file.service';
import { MessageService, LoadResourcesInRepository, SetValue, IDamResource } from '@usnistgov/ngx-dam-framework-legacy';
import { IADFDescriptor } from '../model/adf.model';
import { SupportDataService } from '../../shared/services/support-data.service';
import { IDetectionResource, ICvxResource } from '../../shared/model/public.model';
import { IAnalysisJob } from '../../report/model/report.model';
import {
  LoadUserFacilities,
  LoadUserFacilitiesSuccess,
  LoadUserFacilitiesFailure,
} from './core.actions';


type Resources = IDetectionResource | ICvxResource | IADFDescriptor | IAnalysisJob;

@Injectable()
export class CoreEffects {

  @Effect()
  loadUserFacilities$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadUserFacilities),
    concatMap((action: LoadUserFacilities) => {
      return this.fileService.getFacilitiesForUser().pipe(
        flatMap((facilityList) => {
          return [
            new LoadResourcesInRepository({
              collections: [
                {
                  key: 'user-facilities',
                  values: [
                    ...facilityList,
                  ],
                },
              ]
            }),
            new LoadUserFacilitiesSuccess(facilityList),
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new LoadUserFacilitiesFailure(error)
          );
        })
      );
    })
  );

  @Effect()
  loadFile$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadADFile),
    concatMap((action: LoadADFile) => {
      return combineLatest([
        this.fileService.getFileMetadata(action.id),
        this.supportService.getDetections(),
        this.fileService.getFacilitiesForUser(),
      ]).pipe(
        flatMap(([file, detections, facilities]) => {
          return [
            new LoadResourcesInRepository<IDamResource>({
              collections: [{
                key: 'detections',
                values: detections,
              }, {
                key: 'user-facilities',
                values: [
                  ...facilities,
                ],
              }]
            }),
            new SetValue({
              file,
            }),
            new LoadADFileSuccess(file),
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new LoadADFileFailure(error)
          );
        })
      );
    })
  );


  constructor(
    private fileService: FileService,
    private messageService: MessageService,
    private supportService: SupportDataService,
    private actions$: Actions<CoreActions>
  ) {

  }

}
