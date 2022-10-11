import { MessageService } from 'ngx-dam-framework';
import { Store } from '@ngrx/store';
import { IQueryPayload } from './../../../report-template/model/report-template.model';
import { IQuerySaveRequest, QueryService } from './../../../shared/services/query.service';
import { MatDialog } from '@angular/material/dialog';
import { IConfigurationPayload } from './../../../configuration/model/configuration.model';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Labelizer } from '../../../shared/services/values.service';
import { IReportSectionResult } from '../../model/report.model';
import { Field } from '../../../report-template/model/analysis.values';
import { of } from 'rxjs';
import { QuerySaveDialogComponent, IQuerySaveDetails } from 'src/app/modules/shared/components/query-save-dialog/query-save-dialog.component';
import { flatMap, map } from 'rxjs/operators';

@Component({
  selector: 'app-report-section',
  templateUrl: './report-section.component.html',
  styleUrls: ['./report-section.component.scss']
})
export class ReportSectionComponent implements OnInit {

  fields = Field;

  @Input()
  set value(v: IReportSectionResult) {
    this.pval = this.cloneAndChildren(v);
  }

  get value() {
    return this.pval;
  }

  @Input()
  filterActive: boolean;

  @Input()
  filtered: IReportSectionResult;

  @Output()
  valueChange: EventEmitter<IReportSectionResult>;

  @Input()
  labelizer: Labelizer;

  @Input()
  configuration: IConfigurationPayload;

  filteredHasValue: boolean;
  editMode: boolean;
  pval: IReportSectionResult;

  @Input()
  viewOnly: boolean;

  constructor(
    private dialog: MatDialog,
    private queryService: QueryService,
    private store: Store<any>,
    private messageService: MessageService,
  ) {
    this.valueChange = new EventEmitter();
  }


  saveQuery(queryPayload: IQueryPayload) {
    this.dialog.open(QuerySaveDialogComponent).afterClosed().pipe(
      flatMap((result: IQuerySaveDetails) => {
        if (result) {
          const query: IQuerySaveRequest = {
            id: null,
            name: result.name,
            query: queryPayload,
            configuration: this.configuration,
          };
          return this.queryService.saveQuery(query).pipe(
            map((m) => {
              this.store.dispatch(this.messageService.messageToAction(m));
            })
          );
        }
        return of();
      })
    ).subscribe();
  }

  commentChange() {
    this.valueChange.emit(this.value);
  }

  childChange(child: IReportSectionResult, i: number) {
    const clone = this.cloneAndChildren(this.value);
    clone.children.splice(i, 1, this.cloneAndChildren(child));
    this.valueChange.emit(clone);
  }

  cloneAndChildren(section: IReportSectionResult) {
    return {
      ...section,
      children: [
        ...section.children.map((child) => {
          return {
            ...child,
          };
        })
      ],
    };
  }

  comment() {
    this.editMode = !this.editMode;
  }

  ngOnInit(): void {
  }

}
