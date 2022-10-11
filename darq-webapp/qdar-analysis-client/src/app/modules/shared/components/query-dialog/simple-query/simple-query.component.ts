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
  set analysis(type: AnalysisType) {
    this.type = type;
    this.setQueryFlags(type);
  }
  queryFlags: {
    detections: boolean;
    codes: boolean;
    vaccineEvents: boolean;
    provider: boolean;
  };

  constructor(private queryService: QueryService) {
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

  labelize(fields: Field[]): { value: Field, label: string }[] {
    return fields.map((field) => ({
      value: field,
      label: this.fieldDisplayName[field],
    }));
  }

  validate(value: ISimpleViewQuery): { status: boolean; issues: UserMessage<any>[]; } {
    const issues = this.queryService.validateSimpleQuery(value);
    return {
      status: issues.length === 0,
      issues: issues.map((issue) => new UserMessage(MessageType.FAILED, issue)),
    };
  }

  setQueryFlags(type: AnalysisType) {
    this.queryFlags = {
      detections: [AnalysisType.PATIENTS_DETECTIONS, AnalysisType.VACCINCATIONS_DETECTIONS, AnalysisType.PATIENTS_PROVIDER_DETECTIONS]
        .includes(type),
      codes: [AnalysisType.PATIENTS_VOCABULARY, AnalysisType.VACCINCATIONS_VOCABULARY, AnalysisType.PATIENTS_PROVIDER_VOCABULARY]
        .includes(type),
      provider: [AnalysisType.PATIENTS_PROVIDER_VOCABULARY, AnalysisType.PATIENTS_PROVIDER_DETECTIONS]
        .includes(type),
      vaccineEvents: [AnalysisType.VACCINCATIONS]
        .includes(type)
    };
  }

  ngOnInit(): void {
  }

}
