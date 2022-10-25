import { QueryVariableType, ExternalQueryVariableScope, IQueryVariableRefInstance, IDynamicQueryVariableRef } from './../../model/query-variable.model';
import { Component, Input, OnInit } from '@angular/core';

export interface IQueryVariableDisplay {
  id: string;
  name: string;
  description: string;
  type: QueryVariableType;
  scope?: ExternalQueryVariableScope;
  snapshot?: IQueryVariableRefInstance;
  dynamic: IDynamicQueryVariableRef;
}

@Component({
  selector: 'app-variable-ref-display',
  templateUrl: './variable-ref-display.component.html',
  styleUrls: ['./variable-ref-display.component.scss']
})
export class VariableRefDisplayComponent implements OnInit {

  @Input()
  variable: IQueryVariableDisplay;

  constructor() { }

  ngOnInit(): void {
  }

}
