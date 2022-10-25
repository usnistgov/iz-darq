import { map } from 'rxjs/operators';
import { VariableSelectDialogComponent } from './../../variable-select-dialog/variable-select-dialog.component';
import { IDataViewQuery } from './../../../../report-template/model/report-template.model';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';
import { MessageType, UserMessage } from 'ngx-dam-framework';
import { MatDialog } from '@angular/material/dialog';
import { QueryVariableService } from '../../../services/query-variable.service';
import { QueryService } from '../../../services/query.service';
import { IQueryVariableDisplay } from '../../variable-ref-display/variable-ref-display.component';
import { QueryVariableRefType } from '../../../model/query-variable.model';

@Component({
  selector: 'app-query-denominator-override',
  templateUrl: './query-denominator-override.component.html',
  styleUrls: ['./query-denominator-override.component.scss']
})
export class QueryDenominatorOverrideComponent extends QueryDialogTabComponent<IDataViewQuery> implements OnInit, OnChanges {

  @Input()
  variables: IQueryVariableDisplay[];

  constructor(
    private dialog: MatDialog,
  ) {
    super();
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

  validate(value: IDataViewQuery): { status: boolean; issues: UserMessage<any>[]; } {
    if (value.denominatorVariable) {
      const idx = this.variables.findIndex((v) => v.id === value.denominatorVariable.id);
      if (idx === -1 && value.denominatorVariable.queryValueType === QueryVariableRefType.DYNAMIC) {
        return {
          status: true,
          issues: [
            new UserMessage(MessageType.FAILED, 'Dynamic Denominator Variable not found'),
          ],
        };
      }
    }

    return {
      status: true,
      issues: [],
    };
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.emitValid(this.value);
  }

  ngOnInit(): void {
  }

}
