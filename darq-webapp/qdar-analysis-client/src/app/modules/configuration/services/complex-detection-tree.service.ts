import { Injectable } from '@angular/core';
import {
  ExpressionType,
  IDetectionExpression,
  IExpression,
  IIMPLYExpression,
  INOTExpression
} from '../model/configuration.model';
import { IDetectionResource } from '../../shared/model/public.model';
import { ComplexDetectionService } from './complex-detection.service';
import { TreeNode } from 'primeng/api';

export interface ExpressionTreeNode extends TreeNode {
  label: string;
  children: ExpressionTreeNode[];
  parent: ExpressionTreeNode;
  data: {
    type?: ExpressionType;
    placeholder?: boolean;
    multiple?: boolean;
    detection?: IDetectionResource;
  };
}

@Injectable({
  providedIn: 'root'
})
export class ComplexDetectionTreeService {

  constructor(private complexDetectionService: ComplexDetectionService) { }

  createExpressionTreeNode(type: ExpressionType, parent: ExpressionTreeNode): ExpressionTreeNode {
    return this.getExpressionTree(this.complexDetectionService.createExpression(type), [], parent);
  }

  getPlaceholder(parent: ExpressionTreeNode): ExpressionTreeNode {
    return {
      label: 'placeholder',
      data: {
        placeholder: true,
      },
      parent,
      expanded: true,
      children: []
    };
  }

  getExpressionTree(
    expression: IExpression,
    detections: IDetectionResource[],
    parent: ExpressionTreeNode
  ): ExpressionTreeNode {
    if (expression) {
      switch (expression.type) {
        case ExpressionType.AND:
        case ExpressionType.OR:
        case ExpressionType.XOR:
          return this.getBinaryOperationExpressionTree(expression as (IExpression & { expressions: IExpression[] }), detections, parent);
        case ExpressionType.IMPLY:
          return this.getIMPLYExpressionTree(expression as IIMPLYExpression, detections, parent);
        case ExpressionType.NOT:
          return this.getNOTExpressionTree(expression as INOTExpression, detections, parent);
        case ExpressionType.DETECTION:
          return this.getDetectionExpressionTree(expression as IDetectionExpression, detections, parent);
      }
    }
    return this.getPlaceholder(parent);
  }

  getChildrenForBinaryOperator(
    expressions: IExpression[],
    detections: IDetectionResource[],
    parent: ExpressionTreeNode
  ): ExpressionTreeNode[] {
    const list = (expressions || []).map((e, i) => this.getExpressionTree(e, detections, parent));
    const size = list.length;
    if (size === 0) {
      list.push(this.getPlaceholder(parent), this.getPlaceholder(parent));
    }
    if (size === 1) {
      list.push(this.getPlaceholder(parent));
    }
    return list;
  }

  getBinaryOperationExpressionTree(
    expression: IExpression & { expressions: IExpression[] },
    detections: IDetectionResource[],
    parent: ExpressionTreeNode
  ): ExpressionTreeNode {
    const node: ExpressionTreeNode = {
      label: expression.type,
      data: {
        type: expression.type,
        multiple: true,
      },
      parent,
      expanded: true,
      children: [],
    };
    const children = this.getChildrenForBinaryOperator(expression.expressions, detections, node);
    node.children = children;
    return node;
  }

  getIMPLYExpressionTree(
    expression: IIMPLYExpression,
    detections: IDetectionResource[],
    parent: ExpressionTreeNode
  ): ExpressionTreeNode {
    const node = {
      label: expression.type,
      data: {
        type: expression.type,
      },
      parent,
      expanded: true,
      children: []
    };
    const left = expression.left ? this.getExpressionTree(expression.left, detections, node) : this.getPlaceholder(parent);
    const right = expression.right ? this.getExpressionTree(expression.right, detections, node) : this.getPlaceholder(parent);
    node.children = [
      left,
      right,
    ];
    return node;
  }

  getNOTExpressionTree(
    expression: INOTExpression,
    detections: IDetectionResource[],
    parent: ExpressionTreeNode
  ): ExpressionTreeNode {
    const node = {
      label: expression.type,
      data: {
        type: expression.type,
      },
      parent,
      expanded: true,
      children: []
    };
    const value = expression.expression ? this.getExpressionTree(expression.expression, detections, node) : this.getPlaceholder(parent);
    node.children = [
      value
    ];
    return node;
  }

  getDetectionExpressionTree(
    expression: IDetectionExpression,
    detections: IDetectionResource[],
    parent: ExpressionTreeNode
  ): ExpressionTreeNode {
    const detection = detections.find((d) => d.id === expression.code);
    return {
      label: expression.type,
      data: {
        type: expression.type,
        detection,
      },
      parent,
      expanded: true,
      children: []
    };
  }

  getExpressionFromTree(node: ExpressionTreeNode): IExpression {
    if (node.data.placeholder) {
      return undefined;
    }

    const expression = {
      type: node.data.type,
    };

    if (node.data.type === ExpressionType.NOT) {
      (expression as INOTExpression).expression = this.getExpressionFromTree(node.children[0]);
    } else if (node.data.type === ExpressionType.IMPLY) {
      (expression as IIMPLYExpression).left = this.getExpressionFromTree(node.children[0]);
      (expression as IIMPLYExpression).right = this.getExpressionFromTree(node.children[1]);
    } else if (node.data.type === ExpressionType.DETECTION) {
      (expression as IDetectionExpression).code = node.data.detection ? node.data.detection.id : null;
    } else {
      (expression as IExpression & { expressions: IExpression[] }).expressions = node.children.map(n => this.getExpressionFromTree(n));
    }
    return expression;
  }

}
