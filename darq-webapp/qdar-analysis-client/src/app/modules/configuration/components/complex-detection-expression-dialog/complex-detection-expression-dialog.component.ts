import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { IDetectionResource } from 'src/app/modules/shared/model/public.model';
import { ExpressionType, IExpression } from '../../model/configuration.model';
import * as _ from 'lodash';
import { ComplexDetectionService } from '../../services/complex-detection.service';
import { ComplexDetectionTreeService, ExpressionTreeNode } from '../../services/complex-detection-tree.service';
import { BehaviorSubject, Observable } from 'rxjs';

@Component({
  selector: 'app-complex-detection-expression-dialog',
  templateUrl: './complex-detection-expression-dialog.component.html',
  styleUrls: ['./complex-detection-expression-dialog.component.scss']
})
export class ComplexDetectionExpressionDialogComponent implements OnInit {

  detections: IDetectionResource[];
  expression: IExpression = null;
  operators = [
    ExpressionType.AND,
    ExpressionType.OR,
    ExpressionType.XOR,
    ExpressionType.IMPLY,
    ExpressionType.NOT,
  ];
  expressionString: string;
  tree: ExpressionTreeNode[];
  dragging: boolean;
  dragged: ExpressionTreeNode;
  showDescriptionTree: boolean;
  showDescriptionString: boolean;
  hasPlaceholder: boolean;
  filteredDetections$: BehaviorSubject<IDetectionResource[]>;

  constructor(
    public dialogRef: MatDialogRef<ComplexDetectionExpressionDialogComponent>,
    private complexDetectionService: ComplexDetectionService,
    private complexDetectionTreeService: ComplexDetectionTreeService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.detections = data.detections;
    this.filteredDetections$ = new BehaviorSubject(this.detections);
    if (data.expression) {
      this.expression = _.cloneDeep(data.expression);
    }
    this.tree = [this.complexDetectionTreeService.getExpressionTree(
      this.expression,
      this.detections,
      null
    )];
    this.change();
  }

  filterChange(term: string) {
    const termLower = term.toLowerCase();
    this.filteredDetections$.next(
      this.detections.filter((detection) => {
        return detection.id.toLowerCase().includes(termLower) || detection.description.toLowerCase().includes(termLower);
      })
    );
  }

  change() {
    this.expression = this.complexDetectionTreeService.getExpressionFromTree(this.tree[0]);
    this.updateString();
    this.hasPlaceholder = this.getNodeHasPlaceholder(this.tree[0]);
    this.tree = [...this.tree];
  }

  getNodeHasPlaceholder(node: ExpressionTreeNode) {
    return node.data.placeholder || node.children.some((n) => this.getNodeHasPlaceholder(n));
  }

  updateString() {
    this.expressionString = this.complexDetectionService.getExpressionString(
      this.expression,
      this.detections,
      this.showDescriptionString
    );
  }

  getDetectionByCode(code: string) {
    return this.detections.find(d => d.id === code);
  }

  close() {
    this.dialogRef.close(this.expression);
  }

  cancel() {
    this.dialogRef.close();
  }

  drop(node: ExpressionTreeNode) {
    this.dragged.parent = node.parent;
    this.replace(node, this.dragged);
    this.change();
  }

  dragOperatorStart(type) {
    this.dragging = true;
    this.dragged = this.complexDetectionTreeService.createExpressionTreeNode(type, undefined);
  }

  dragDetectionStart(detection) {
    this.dragging = true;
    this.dragged = {
      label: 'Detection',
      data: {
        type: ExpressionType.DETECTION,
        detection
      },
      parent: undefined,
      children: [],
    };
  }

  clear(node) {
    this.replace(node, this.complexDetectionTreeService.getPlaceholder(node.parent));
    this.change();
  }

  remove(node: ExpressionTreeNode) {
    if (node.parent) {
      const index = node.parent.children.findIndex(n => n === node);
      node.parent.children.splice(index, 1);
    }
    this.change();
  }

  add(node) {
    node.children.push(this.complexDetectionTreeService.getPlaceholder(node));
    this.change();
  }

  dragEnd() {
    this.dragging = false;
    this.dragged = null;
  }

  replace(source: ExpressionTreeNode, target: ExpressionTreeNode) {
    Object.keys(source).forEach((k) => delete source[k]);
    Object.assign(source, target);
  }



  ngOnInit(): void {
  }

}
