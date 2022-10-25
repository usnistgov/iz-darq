import { IQueryVariableRef, IQueryVariableRefInstance, QueryVariableRefType, IDynamicQueryVariableRef } from './../../model/query-variable.model';
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-variable-ref',
  templateUrl: './variable-ref.component.html',
  styleUrls: ['./variable-ref.component.scss']
})
export class VariableRefComponent implements OnInit {

  @Input()
  set variable(v: IQueryVariableRef) {
    this.type = v.queryValueType;
    if (v && v.queryValueType === QueryVariableRefType.STATIC) {
      this.instance = v as IQueryVariableRefInstance;
    } else if (v && v.queryValueType === QueryVariableRefType.DYNAMIC) {
      this.dynamic = v as IDynamicQueryVariableRef;
    }
  }

  type: QueryVariableRefType;
  instance: IQueryVariableRefInstance;
  dynamic: IDynamicQueryVariableRef;

  constructor() { }

  ngOnInit(): void {
  }

}
