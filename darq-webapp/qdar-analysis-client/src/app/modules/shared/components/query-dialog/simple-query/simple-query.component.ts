import { IQueryVariableDisplay } from './../../variable-ref-display/variable-ref-display.component';
import { QueryVariableRefType } from './../../../model/query-variable.model';
import { map } from 'rxjs/operators';
import { VariableSelectDialogComponent } from './../../variable-select-dialog/variable-select-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { Component, OnInit, SimpleChanges, OnChanges } from '@angular/core';
import { AnalysisType, Field } from 'src/app/modules/report-template/model/analysis.values';
import { Comparator, IThreshold } from 'src/app/modules/report-template/model/report-template.model';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';
import { ISimpleViewQuery } from '../../../../report-template/model/report-template.model';
import { MessageType, UserMessage } from 'ngx-dam-framework';
import { Input } from '@angular/core';
import { IFieldInputOptions } from '../../field-input/field-input.component';
import { QueryService } from '../../../services/query.service';

export interface ISimpleQuery {
  filter: {
    value: string;
  };
  denominator?: {
    active: boolean;
    field: Field;
  };
  threshold?: {
    active: boolean;
    goal: IThreshold;
  };
}

@Component({
  selector: 'app-simple-query',
  templateUrl: './simple-query.component.html',
  styleUrls: ['./simple-query.component.scss']
})
export class SimpleQueryComponent extends QueryDialogTabComponent<ISimpleViewQuery> implements OnInit, OnChanges {
  AnalysisType = AnalysisType;
  comparatorOptions = [{
    label: 'Greater Than',
    value: Comparator.GT,
  },
  {
    label: 'Lower Than',
    value: Comparator.LT,
  }];

  breakdownBy: Record<AnalysisType, { value: Field, label: string }[]> = {
    [AnalysisType.VACCINCATIONS]: this.labelize(
      [Field.PROVIDER, Field.AGE_GROUP, Field.EVENT, Field.GENDER, Field.VACCINATION_YEAR, Field.VACCINE_CODE]
    ),
    [AnalysisType.PATIENTS_MATCHING]: [],
    [AnalysisType.VACCINCATIONS_DETECTIONS]: this.labelize([Field.PROVIDER, Field.AGE_GROUP]),
    [AnalysisType.VACCINCATIONS_VOCABULARY]: this.labelize([Field.PROVIDER, Field.AGE_GROUP]),
    [AnalysisType.PATIENTS_DETECTIONS]: this.labelize([Field.AGE_GROUP]),
    [AnalysisType.PATIENTS_VOCABULARY]: this.labelize([Field.AGE_GROUP]),
    [AnalysisType.PATIENTS_PROVIDER_DETECTIONS]: this.labelize([Field.PROVIDER, Field.AGE_GROUP, Field.DETECTION]),
    [AnalysisType.PATIENTS_PROVIDER_VOCABULARY]: this.labelize([Field.PROVIDER, Field.AGE_GROUP, Field.TABLE, Field.CODE]),
  };

  @Input()
  options: IFieldInputOptions;
  @Input()
  variables: IQueryVariableDisplay[];

  @Input()
  set analysis(type: AnalysisType) {
    this.type = type;
    this.setQueryFlags(type);
  }
  queryFlags: {
    detections: boolean;
    codes: boolean;
    vaccineEvents: boolean;
    provider: boolean;
    matchSignature: boolean;
  };

  constructor(
    private queryService: QueryService,
    private dialog: MatDialog,
  ) {
    super();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.emitValid(this.value);
  }

  triggerValid() {
    this.emitValid(this.value);
  }

  triggerChange() {
    this.emitChange(this.value);
  }

  openDenominatorVariableDialog() {
    return this.dialog.open(VariableSelectDialogComponent, {
      data: {
        variables: this.variables,
      }
    }).afterClosed().pipe(
      map((variable) => {
        this.value.denominatorVariable = variable;
        this.emitChange(this.value);
      })
    ).subscribe();
  }

  removeDenominatorVariable() {
    this.value.denominatorVariable = null;
    this.emitChange(this.value);
  }

  labelize(fields: Field[]): { value: Field, label: string }[] {
    return fields.map((field) => ({
      value: field,
      label: this.fieldDisplayName[field],
    }));
  }

  validate(value: ISimpleViewQuery): { status: boolean; issues: UserMessage<any>[]; } {
    const issues = this.queryService.validateSimpleQuery(value);

    if (value.denominatorVariable) {
      const idx = this.variables.findIndex((v) => v.id === value.denominatorVariable.id);
      if (idx === -1 && value.denominatorVariable.queryValueType === QueryVariableRefType.DYNAMIC) {
        issues.push('Dynamic Denominator Variable not found');
      }
    }

    return {
      status: issues.length === 0,
      issues: issues.map((issue) => new UserMessage(MessageType.FAILED, issue)),
    };
  }

  setQueryFlags(type: AnalysisType) {
    this.queryFlags = this.queryService.getQueryFlags(type);
  }

  ngOnInit(): void {
  }

}
