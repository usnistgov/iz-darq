import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { catchError, concatMap, flatMap, take, map } from 'rxjs/operators';
import { of, combineLatest } from 'rxjs';
import {
  CoreActionTypes,
  CoreActions,
  LoadReportTemplates,
  LoadReportTemplatesFailure,
  LoadReportTemplatesSuccess,
} from './core.actions';
import { IReportTemplateDescriptor } from '../model/report-template.model';
import { LoadResourcesInRepository, MessageService } from 'ngx-dam-framework';
import { ReportTemplateService } from '../services/report-template.service';
import { ConfigurationService } from '../../configuration/services/configuration.service';
import { IConfigurationDescriptor } from '../../configuration/model/configuration.model';


@Injectable()
export class CoreEffects {

  @Effect()
  loadCores$ = this.actions$.pipe(
    ofType(CoreActionTypes.LoadReportTemplates),
    concatMap((action: LoadReportTemplates) => {
      return combineLatest([
        this.reportTemplateService.getReportTemplates(),
        this.configurationService.getConfigurations(),
      ]).pipe(
        flatMap(([templates, configurations]) => {
          return [
            new LoadResourcesInRepository<IReportTemplateDescriptor | IConfigurationDescriptor>({
              collections: [{
                key: 'templates',
                values: templates,
              },
              {
                key: 'configurations',
                values: configurations,
              }]
            }),
            new LoadReportTemplatesSuccess(templates),
          ];
        }),
        catchError((error) => {
          return of(
            this.messageService.actionFromError(error),
            new LoadReportTemplatesFailure(error)
          );
        })
      );
    })
  );

  constructor(
    private actions$: Actions<CoreActions>,
    private reportTemplateService: ReportTemplateService,
    private configurationService: ConfigurationService,
    private messageService: MessageService,
  ) { }

}
