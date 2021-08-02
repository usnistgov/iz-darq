import { Component, OnInit, OnDestroy } from '@angular/core';
import { DamAbstractEditorComponent, MessageService, IEditorMetadata, EditorSave, LoadPayloadData, SetValue } from 'ngx-dam-framework';
import { Store, Action } from '@ngrx/store';
import { Actions } from '@ngrx/effects';
import { ValuesService, Labelizer } from '../../../shared/services/values.service';
import { Observable, combineLatest, Subscription, throwError, BehaviorSubject } from 'rxjs';
import { IReport, IReportSectionResult } from '../../model/report.model';
import { selectAllDetections, selectAllCvx, selectPatientTables, selectVaccinationTables } from '../../../shared/store/core.selectors';
import { map, tap, concatMap, take, flatMap, catchError, skipUntil, withLatestFrom } from 'rxjs/operators';
import { selectReportPayload, selectReportGeneralFilter } from '../../store/core.selectors';
import { ReportService } from '../../services/report.service';
import { IReportFilter } from '../../../report-template/model/report-template.model';
import { PermissionService } from '../../../core/services/permission.service';
import { ResourceType } from '../../../core/model/resouce-type.enum';
import { Action as _Action } from '../../../core/model/action.enum';
import { ITocNode } from '../report-toc/report-toc.component';

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
  nodesSub: Subscription;
  viewOnly$: Observable<boolean>;
  generalFilter$: Observable<IReportFilter>;
  report$: BehaviorSubject<IReport>;

  constructor(
    store: Store<any>,
    actions$: Actions,
    private permission: PermissionService,
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
    this.viewOnly$ = combineLatest([
      this.permission.abilities$,
      this.store.select(selectReportPayload),
    ]).pipe(
      map(([ability, report]) => {
        return ability.onResourceCant(_Action.COMMENT, ResourceType.REPORT, report);
      })
    );
    this.labelizer$ = combineLatest([
      this.store.select(selectReportPayload),
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

    this.nodesSub = this.filtered$.pipe(
      withLatestFrom(this.filterIsActive$),
      map(([report, filterIsActive]) => {
        this.store.dispatch(new SetValue({ reportTocNodes: this.makeTocNode(filterIsActive, report.sections) }));
      })
    ).subscribe();
  }

  emptyFiltered(filterActive: boolean, report: IReport) {
    return filterActive && !report.sections.map((section) => section.hasValue).includes(true);
  }

  makeTocNode(filterIsActive: boolean, sections: IReportSectionResult[]): ITocNode[] {
    if (!sections) {
      return [];
    }

    return sections
      .filter((section) => !filterIsActive || section.hasValue)
      .map((s) => {
        return {
          id: s.id,
          header: s.header,
          path: s.path,
          warning: s.thresholdViolation,
          children: this.makeTocNode(filterIsActive, s.children),
        };
      });
  }

  get filterIsActive$() {
    return this.generalFilter$.pipe(
      map((filter) => {
        return this.reportService.filterIsActive(filter);
      })
    );
  }

  ngOnDestroy(): void {
    if (this.wSub) {
      this.wSub.unsubscribe();
    }
    if (this.nodesSub) {
      this.nodesSub.unsubscribe();
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
    return this.store.select(selectReportPayload).pipe(
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
