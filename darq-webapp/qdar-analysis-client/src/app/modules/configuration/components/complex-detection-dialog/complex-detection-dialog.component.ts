import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { IDetectionResource } from 'src/app/modules/shared/model/public.model';
import { ComplexDetectionTarget, ExpressionType, IExpression } from '../../model/configuration.model';
import * as _ from 'lodash';
import { ComplexDetectionService } from '../../services/complex-detection.service';
import { ComplexDetectionExpressionDialogComponent } from '../complex-detection-expression-dialog/complex-detection-expression-dialog.component';
import { tap } from 'rxjs/operators';
import { FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';

@Component({
  selector: 'app-complex-detection-dialog',
  templateUrl: './complex-detection-dialog.component.html',
  styleUrls: ['./complex-detection-dialog.component.scss']
})
export class ComplexDetectionDialogComponent implements OnInit {

  detections: IDetectionResource[];
  targets = [
    ComplexDetectionTarget.RECORD,
    ComplexDetectionTarget.VACCINATION
  ];
  operators = [
    ExpressionType.AND,
    ExpressionType.OR,
    ExpressionType.XOR,
    ExpressionType.IMPLY,
    ExpressionType.NOT,
  ];
  expressionString: string;
  showDescriptionString: boolean;
  existingCodes: string[];
  formGroup: FormGroup;
  expression: IExpression;

  existingCodeValidator: ValidatorFn = (control) => {
    const match = this.detections.find((detection) => detection.id.toLowerCase() === control.value.toLowerCase());
    if (match) {
      return {
        existing: match.id,
      };
    } else {
      const existing = this.existingCodes.find((code) => code.toLowerCase() === control.value.toLowerCase());
      if (existing) {
        return {
          existing
        };
      }
      return {};
    }
  }

  constructor(
    public dialogRef: MatDialogRef<ComplexDetectionDialogComponent>,
    private complexDetectionService: ComplexDetectionService,
    private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.detections = data.detections;
    if (data.detection && data.detection.expression) {
      this.expression = _.cloneDeep(data.detection.expression);
    }
    this.existingCodes = data.existingCodes || [];
    this.formGroup = new FormGroup({
      code: new FormControl(data.detection?.code || '', [
        Validators.required,
        Validators.pattern('^[A-Z0-9]+$'),
        Validators.maxLength(10),
        this.existingCodeValidator]
      ),
      description: new FormControl(data.detection?.description || '', [Validators.required]),
      target: new FormControl(data.detection?.target || ComplexDetectionTarget.RECORD, [Validators.required])
    });
    this.updateString();
  }

  updateString() {
    this.expressionString = this.complexDetectionService.getExpressionString(
      this.expression,
      this.detections,
      this.showDescriptionString,
    );
  }

  openExpressionDialog() {
    this.dialog.open(ComplexDetectionExpressionDialogComponent, {
      height: '95vh',
      width: '95vh',
      minHeight: '95vh',
      minWidth: '95vw',
      data: {
        detections: this.getDetections(this.formGroup.getRawValue().target),
        expression: this.expression
      }
    }).afterClosed().pipe(
      tap((expression) => {
        if (expression) {
          this.expression = expression;
          this.updateString();
        }
      })
    ).subscribe();
  }

  getDetections(target: ComplexDetectionTarget) {
    if (target === ComplexDetectionTarget.RECORD) {
      return this.detections;
    } else if (target === ComplexDetectionTarget.VACCINATION) {
      return this.detections.filter(d => d.target === 'VACCINATION');
    } else {
      return [];
    }
  }

  close() {
    this.dialogRef.close({
      ...this.formGroup.getRawValue(),
      expression: this.expression,
    });
  }

  cancel() {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

}
