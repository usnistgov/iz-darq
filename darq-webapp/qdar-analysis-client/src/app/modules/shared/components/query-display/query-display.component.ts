import { Component, OnInit, Input } from '@angular/core';
import { Labelizer } from '../../services/values.service';
import { IDataViewQuery } from '../../../report-template/model/report-template.model';
import { fieldDisplayName } from '../../../report-template/model/analysis.values';

@Component({
  selector: 'app-query-display',
  templateUrl: './query-display.component.html',
  styleUrls: ['./query-display.component.scss']
})
export class QueryDisplayComponent implements OnInit {

  @Input()
  labelizer: Labelizer;
  @Input()
  query: IDataViewQuery;
  fieldDisplayName = fieldDisplayName;

  constructor() { }

  ngOnInit(): void {
  }

}
