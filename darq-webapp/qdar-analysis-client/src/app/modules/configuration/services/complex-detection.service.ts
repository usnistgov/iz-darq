import { Injectable } from '@angular/core';
import {
  ExpressionType,
  IANDExpression,
  IDetectionExpression,
  IExpression,
  IIMPLYExpression,
  INOTExpression,
  IORExpression,
  IXORExpression
} from '../model/configuration.model';
import { IDetectionResource } from '../../shared/model/public.model';

@Injectable({
  providedIn: 'root'
})
export class ComplexDetectionService {


  createExpression(type: ExpressionType): IExpression {
    switch (type) {
      case ExpressionType.AND:
        return { type, expressions: [] } as IANDExpression;
      case ExpressionType.OR:
        return { type, expressions: [] } as IORExpression;
      case ExpressionType.XOR:
        return { type, expressions: [] } as IXORExpression;
      case ExpressionType.NOT:
        return { type } as INOTExpression;
      case ExpressionType.IMPLY:
        return { type } as IIMPLYExpression;
    }
  }

  getExpressionString(
    expression: IExpression,
    detections: IDetectionResource[],
    showDescription: boolean = false
  ): string {
    if (expression) {
      switch (expression.type) {
        case ExpressionType.AND:
          return this.getANDExpressionString(expression as IANDExpression, detections, showDescription);
        case ExpressionType.OR:
          return this.getORExpressionString(expression as IORExpression, detections, showDescription);
        case ExpressionType.XOR:
          return this.getXORExpressionString(expression as IXORExpression, detections, showDescription);
        case ExpressionType.IMPLY:
          return this.getIMPLYExpressionString(expression as IIMPLYExpression, detections, showDescription);
        case ExpressionType.NOT:
          return this.getNOTExpressionString(expression as INOTExpression, detections, showDescription);
        case ExpressionType.DETECTION:
          return this.getDetectionExpressionString(expression as IDetectionExpression, detections, showDescription);
      }
    }
    return '_';
  }

  getExpressionListStringForBinaryOperator(
    expressions: IExpression[],
    detections: IDetectionResource[],
    showDescription: boolean = false
  ): string[] {
    const list = (expressions || []).map((e) => this.getExpressionString(e, detections, showDescription));
    const size = list.length;
    if (size === 0) {
      list.push('_', '_');
    }
    if (size === 1) {
      list.push('_');
    }
    return list;
  }

  getANDExpressionString(expression: IANDExpression, detections: IDetectionResource[], showDescription: boolean = false): string {
    const expressions = this.getExpressionListStringForBinaryOperator(expression.expressions, detections, showDescription);
    return `(${expressions.join(' AND ')})`;
  }

  getORExpressionString(expression: IORExpression, detections: IDetectionResource[], showDescription: boolean = false): string {
    const expressions = this.getExpressionListStringForBinaryOperator(expression.expressions, detections, showDescription);
    return `(${expressions.join(' OR ')})`;
  }

  getXORExpressionString(expression: IXORExpression, detections: IDetectionResource[], showDescription: boolean = false): string {
    const expressions = this.getExpressionListStringForBinaryOperator(expression.expressions, detections, showDescription);
    return `(${expressions.join(' XOR ')})`;
  }

  getIMPLYExpressionString(expression: IIMPLYExpression, detections: IDetectionResource[], showDescription: boolean = false): string {
    const left = expression.left ? this.getExpressionString(expression.left, detections, showDescription) : '_';
    const right = expression.right ? this.getExpressionString(expression.right, detections, showDescription) : '_';
    return `(IF ${left} THEN ${right})`;
  }

  getNOTExpressionString(expression: INOTExpression, detections: IDetectionResource[], showDescription: boolean = false): string {
    const value = expression.expression ? this.getExpressionString(expression.expression, detections, showDescription) : '_';
    return `(NOT ${value})`;
  }

  getDetectionExpressionString(
    expression: IDetectionExpression,
    detections: IDetectionResource[],
    showDescription: boolean = false
  ): string {
    const detection = detections.find((d) => d.id === expression.code);
    const description = ` - ${detection.description}`;
    return detection ? `${detection.id}${showDescription ? description : ''}` : '_';
  }



}
