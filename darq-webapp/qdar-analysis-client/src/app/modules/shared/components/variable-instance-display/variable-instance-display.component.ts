import { IQueryVariableRefInstance } from './../../model/query-variable.model';
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-variable-instance-display',
  templateUrl: './variable-instance-display.component.html',
  styleUrls: ['./variable-instance-display.component.scss']
})
export class VariableInstanceDisplayComponent implements OnInit {

  @Input()
  variable: IQueryVariableRefInstance;

  constructor() { }

  ngOnInit(): void {
  }

}
