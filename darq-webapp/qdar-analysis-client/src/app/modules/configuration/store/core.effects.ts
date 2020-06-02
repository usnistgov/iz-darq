
import { Actions, Effect, ofType } from '@ngrx/effects';
import { concatMap, flatMap, take, switchMap, map, catchError } from 'rxjs/operators';
import {
  CoreActionTypes,
  CoreActions,
  RouteLoadConfigurationPage,
  RouteLoadConfigurationPageSuccess,
  OpenConfigurationEditor,
  RouteLoadConfigurationPageFailure
} from './core.actions';
import { ConfigurationService } from '../services/configuration.service';
import { SupportDataService } from '../../shared/services/support-data.service';
import { combineLatest, of } from 'rxjs';
import {
  LoadResourcesInRepository,
  DamWidgetEffect,
  OpenEditor,
  DamActionTypes,
  EditorSave,
  GlobalSave,
  OpenEditorFailure,
  MessageService
} from 'ngx-dam-framework';
import { IDetectionResource } from '../../shared/model/public.model';
import { IConfigurationDescriptor } from '../model/configuration.model';
import { CONFIG_WIDGET } from '../components/configuration-widget/configuration-widget.component';
import { Store } from '@ngrx/store';
import { selectConfigurationById } from './core.selectors';
import { CONFIGURATION_EDITOR_MD } from '../components/configuration-editor/configuration-editor.component';
import { Injectable } from '@angular/core';

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
  loadCores$ = this.actions$.pipe(
    ofType(CoreActionTypes.RouteLoadConfigurationPage),
    concatMap((action: RouteLoadConfigurationPage) => {
      return combineLatest([
        this.supportingDataService.getDetections(),
        this.configurationService.getConfigurations(),
      ]).pipe(
        flatMap(([detections, configurations]) => {
          return [
            new LoadResourcesInRepository<IDetectionResource | IConfigurationDescriptor>({
              collections: [{
                key: 'detections',
                values: detections,
              },
              {
                key: 'configurations',
                values: configurations,
              }]
            }),
            new RouteLoadConfigurationPageSuccess(configurations),
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new RouteLoadConfigurationPageFailure(error)
          );
        })
      );
    })
  );

  @Effect()
  openConfigEditor$ = this.actions$.pipe(
    ofType(CoreActionTypes.OpenConfigurationEditor),
    switchMap((action: OpenConfigurationEditor) => {
      return combineLatest([
        this.store.select(selectConfigurationById, { id: action.payload.id }),
        this.configurationService.getById(action.payload.id),
      ]).pipe(
        take(1),
        flatMap(([display, configuration]) => {
          return [
            new OpenEditor({
              id: action.payload.id,
              display,
              editor: CONFIGURATION_EDITOR_MD,
              initial: configuration,
            }),
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new OpenEditorFailure({
              id: action.payload.id,
            }));
        })
      );
    })
  );


  constructor(
    private configurationService: ConfigurationService,
    private supportingDataService: SupportDataService,
    private messageService: MessageService,
    private store: Store<any>,
    actions$: Actions<CoreActions>,
  ) {
    super(CONFIG_WIDGET, actions$);
  }

}
