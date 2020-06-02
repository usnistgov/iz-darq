import { Component, OnInit, Input, AfterViewInit, ViewChild } from '@angular/core';
import { TREE_ACTIONS, TreeComponent, TreeNode } from 'angular-tree-component';
import { fromEvent } from 'rxjs';
import { debounce, tap, debounceTime } from 'rxjs/operators';

export interface ITocNode {
  id: string;
  path: string;
  header: string;
  warning: boolean;
  children: ITocNode[];
}

@Component({
  selector: 'app-report-toc',
  templateUrl: './report-toc.component.html',
  styleUrls: ['./report-toc.component.scss']
})
export class ReportTocComponent implements OnInit, AfterViewInit {

  @ViewChild(TreeComponent)
  treeComponent: TreeComponent;
  pnodes: ITocNode[];
  options;
  offsets: {
    id: string,
    top: number,
  }[] = [];
  container: HTMLElement;
  thresholdFilter: boolean = null;

  @Input()
  set nodes(nodes: ITocNode[]) {
    this.pnodes = nodes;
  }

  get nodes() {
    return this.pnodes;
  }

  constructor() {
    this.options = {
      allowDrag: true,
      actionMapping: {
        mouse: {
          click: (elm, node, event) => {
            const target = document.getElementById(node.data.id);
            const position = target.offsetTop;
            this.container.scrollTo({
              top: position - this.container.offsetTop,
              behavior: 'smooth'
            });
            TREE_ACTIONS.TOGGLE_ACTIVE(elm, node, event);
          },
        },

      }
    };
  }

  ngAfterViewInit() {
    this.treeComponent.treeModel.expandAll();
    const firstNode: TreeNode = this.treeComponent.treeModel.getFirstRoot();
    firstNode.setActiveAndVisible();
    this.container = document.getElementsByClassName('container-content').item(0) as HTMLElement;
    const collection = document.getElementsByClassName('scroll-to');
    const size = collection.length;
    let i = 0;
    this.offsets = [];
    while (i < size) {
      const elm = collection.item(i) as HTMLElement;
      this.offsets.push({
        id: elm.id,
        top: elm.offsetTop - this.container.offsetTop,
      });
      i++;
    }

    fromEvent(this.container, 'scroll').pipe(
      debounceTime(300),
      tap((elm) => {
        const target = elm.target as HTMLElement;
        const soff = target.scrollTop;
        if (this.container.scrollHeight - (soff + target.clientHeight) === 0 && soff !== 0) {
          this.focusOn(this.offsets[size - 1].id);
        } else {
          const match = this.findMatch(soff);
          if (match) {
            this.focusOn(match.id);
          }
        }
      })
    ).subscribe();
  }

  filterText(value) {
    this.treeComponent.treeModel.filterNodes((node) => {
      return node.data.path.includes(value) || node.data.header.includes(value);
    });
  }

  filterThreshold(value) {
    this.treeComponent.treeModel.filterNodes((node) => {
      return value == null || node.data.warning === value;
    });
  }

  focusOn(id: string) {
    const node = this.treeComponent.treeModel.getNodeById(id);
    if (node) {
      node.setActiveAndVisible();
    }
  }

  findMatch(top: number) {
    const size = this.offsets.length;
    let i = 0;
    while (i < (size - 1)) {
      if (top >= this.offsets[i].top && top < this.offsets[i + 1].top) {
        return this.offsets[i];
      }
      i++;
    }
    return this.offsets[0];
  }

  ngOnInit(): void {
  }

}
