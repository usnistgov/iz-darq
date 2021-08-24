import { Component, OnInit, Input } from '@angular/core';
import { Labelizer } from '../../services/values.service';
import { IDataViewQuery, QueryType, QueryPayloadType } from '../../../report-template/model/report-template.model';
import { Field, fieldDisplayName } from '../../../report-template/model/analysis.values';

@Component({
  selector: 'app-query-display',
  templateUrl: './query-display.component.html',
  styleUrls: ['./query-display.component.scss']
})
export class QueryDisplayComponent implements OnInit {
  QueryPayloadType = QueryPayloadType;
  @Input()
  labelizer: Labelizer;
  @Input()
  query: QueryType;
  fieldDisplayName = fieldDisplayName;

  getAllFields(value: QueryType): Field[] {
    switch (value.payloadType) {
      case QueryPayloadType.SIMPLE:
        return [
          ...(value.denominator?.active && value.denominator.field ? [value.denominator.field] : []),
          ...(value.nominator ? [value.nominator] : []),
        ];
      case QueryPayloadType.ADVANCED:
        return [
          ...value.groupBy,
          ...value.occurrences,
        ];
    }
    return [];
  }

  constructor() { }

  ngOnInit(): void {
  }

}
