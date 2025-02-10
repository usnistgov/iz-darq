import { Comparator, IVariableQuery } from './../../../../report-template/model/report-template.model';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';
import { MatDialog } from '@angular/material/dialog';
import { IQueryVariableDisplay } from '../../variable-ref-display/variable-ref-display.component';
import { MessageType, UserMessage } from '@usnistgov/ngx-dam-framework-legacy';
import { VariableSelectDialogComponent } from '../../variable-select-dialog/variable-select-dialog.component';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-variable-query',
  templateUrl: './variable-query.component.html',
  styleUrls: ['./variable-query.component.scss']
})
export class VariableQueryComponent extends QueryDialogTabComponent<IVariableQuery> implements OnInit, OnChanges {

  @Input()
  variables: IQueryVariableDisplay[];

  comparatorOptions = [{
    label: 'Greater Than',
    value: Comparator.GT,
  },
  {
    label: 'Lower Than',
    value: Comparator.LT,
  }];


  constructor(
    private dialog: MatDialog,
  ) {
    super();
  }

  validate(value: IVariableQuery): { status: boolean; issues: UserMessage<any>[]; } {
    const issues = [];
    if (!value.denominatorVariable) {
      issues.push('Denominator variable is missing');
    }
    if (!value.numeratorVariable) {
      issues.push('Numerator variable is missing');
    }
    if (value.threshold && value.threshold.active &&
      (!value.threshold.goal || !value.threshold.goal.comparator || value.threshold.goal.value === undefined)) {
      issues.push('Fill "Threshold" field');
    }

    return {
      status: issues.length === 0,
      issues: issues.map((issue) => new UserMessage(MessageType.FAILED, issue)),
    };
  }

  openVariableDialog(denominator: boolean) {
    return this.dialog.open(VariableSelectDialogComponent, {
      data: {
        variables: this.variables,
      }
    }).afterClosed().pipe(
      map((variable) => {
        if (denominator) {
          this.value.denominatorVariable = variable;
        } else {
          this.value.numeratorVariable = variable;
        }
        this.emitChange(this.value);
      })
    ).subscribe();
  }

  removeVariable(denominator: boolean) {
    if (denominator) {
      this.value.denominatorVariable = null;
    } else {
      this.value.numeratorVariable = null;
    }
    this.emitChange(this.value);
  }

  triggerValid() {
    this.emitValid(this.value);
  }

  triggerChange() {
    this.emitChange(this.value);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.emitValid(this.value);
  }


  ngOnInit(): void {
  }

}
