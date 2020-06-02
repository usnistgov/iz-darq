import { Component, OnInit, OnDestroy } from '@angular/core';
import { DamAbstractEditorComponent, MessageService, IEditorMetadata, EditorSave, LoadPayloadData } from 'ngx-dam-framework';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { MatDialog } from '@angular/material/dialog';
import { ValuesService, Labelizer } from '../../../shared/services/values.service';
import { ReportTemplateService } from '../../../report-template/services/report-template.service';
import { Observable, combineLatest, Subscription, throwError, BehaviorSubject } from 'rxjs';
import { IReport, IReportSectionResult } from '../../model/report.model';
import { selectAllDetections, selectAllCvx, selectPatientTables, selectVaccinationTables } from '../../../shared/store/core.selectors';
import { map, tap, concatMap, take, flatMap, catchError, filter, skipUntil } from 'rxjs/operators';
import { selectReport, selectReportIsViewOnly, selectReportGeneralFilter } from '../../store/core.selectors';
import { ReportService } from '../../services/report.service';
import { IReportFilter } from '../../../report-template/model/report-template.model';

export const REPORT_EDITOR_METADATA: IEditorMetadata = {
  id: 'REPORT_EDITOR',
  title: 'Analysis Report'
};


@Component({
  selector: 'app-report-editor',
  templateUrl: './report-editor.component.html',
  styleUrls: ['./report-editor.component.scss']
})
export class ReportEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  report: IReport;
  filtered$: Observable<IReport>;
  labelizer$: Observable<Labelizer>;
  wSub: Subscription;
  viewOnly$: Observable<boolean>;
  generalFilter$: Observable<IReportFilter>;
  report$: BehaviorSubject<IReport>;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private valueService: ValuesService,
    private reportService: ReportService,
    private messageService: MessageService,
  ) {
    super(
      REPORT_EDITOR_METADATA,
      actions$,
      store,
    );
    this.report$ = new BehaviorSubject(undefined);
    this.viewOnly$ = this.store.select(selectReportIsViewOnly);
    this.labelizer$ = combineLatest([
      this.store.select(selectReport),
      this.store.select(selectAllDetections),
      this.store.select(selectAllCvx),
      this.store.select(selectPatientTables),
      this.store.select(selectVaccinationTables),
    ]).pipe(
      map(([report, detections, cvxCodes, patientTables, vaccinationTables]) => {
        const options = this.valueService.getFieldOptions({
          detections,
          ageGroups: report.configuration.ageGroups,
          cvxs: cvxCodes,
          tables: {
            vaccinationTables,
            patientTables,
          }
        });
        return this.valueService.getQueryValuesLabel(options);
      }),
    );
    this.generalFilter$ = this.store.select(selectReportGeneralFilter);

    this.wSub = this.currentSynchronized$.pipe(
      tap((report) => {
        this.report = this.cloneAndSections(report);
        this.report$.next(this.report);
      }),
    ).subscribe();

    this.filtered$ = this.generalFilter$.pipe(
      skipUntil(this.report$),
      map((general) => {
        if (!general) {
          return this.report;
        } else {
          return this.reportService.postProcessFilter(this.report, general);
        }
      })
    );
  }

  ngOnDestroy(): void {
    if (this.wSub) {
      this.wSub.unsubscribe();
    }
  }

  childChange(child: IReportSectionResult, i: number) {
    const clone = this.cloneAndSections(this.report);
    clone.sections.splice(i, 1, this.cloneAndChildren(child));
    this.editorChange({ ...clone }, true);
  }

  cloneAndSections(section: IReport): IReport {
    return {
      ...section,
      sections: [...section.sections.map((child) => {
        return {
          ...child,
        };
      })],
    };
  }

  cloneAndChildren(section: IReportSectionResult) {
    return {
      ...section,
      children: [
        ...section.children.map(this.cloneObj)
      ],
    };
  }

  cloneObj(a) {
    return {
      ...a,
    };
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return this.current$.pipe(
      take(1),
      concatMap((current) => {
        console.log(current.data);
        return this.reportService.save(current.data).pipe(
          flatMap((message) => {
            return [
              new LoadPayloadData(message.data),
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
    return this.store.select(selectReport).pipe(
      map((report) => {
        return { name: report.name };
      }),
    );
  }

  onDeactivate(): void {
  }

  ngOnInit(): void {
  }

}
