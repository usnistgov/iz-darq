import { Component, OnInit, Input } from '@angular/core';
import { Labelizer } from '../../services/values.service';
import { IDataViewQuery } from '../../../report-template/model/report-template.model';

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

  constructor() { }

  ngOnInit(): void {
  }

}
