import { Component, OnInit, Input, AfterViewInit, ViewChild, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { TREE_ACTIONS, TreeComponent, TreeNode } from 'angular-tree-component';
import { fromEvent, Subscription, Subject, interval, combineLatest } from 'rxjs';
import { tap, debounceTime } from 'rxjs/operators';

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
export class ReportTocComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild(TreeComponent)
  treeComponent: TreeComponent;
  pnodes: ITocNode[];
  options;
  offsets: Subject<{
    id: string,
    top: number,
  }[]>;
  container: HTMLElement;
  thresholdFilter: boolean = null;
  scroll: Subscription;
  refresh: Subscription;
  @Input()
  set nodes(nodes: ITocNode[]) {
    this.pnodes = nodes;
  }

  get nodes() {
    return this.pnodes;
  }

  constructor() {
    this.offsets = new Subject();
    this.options = {
      allowDrag: false,
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
    // Init Active node tree
    this.treeComponent.treeModel.expandAll();
    const firstNode: TreeNode = this.treeComponent.treeModel.getFirstRoot();
    firstNode.setActiveAndVisible();

    // Get DOM Elements
    this.container = document.getElementsByClassName('container-content').item(0) as HTMLElement;
    const collection = document.getElementsByClassName('scroll-to');
    const size = collection.length;

    this.refresh = interval(800).pipe(
      tap(() => {
        let i = 0;
        const offsets = [];
        while (i < size) {
          const elm = collection.item(i) as HTMLElement;
          offsets.push({
            id: elm.id,
            top: elm.offsetTop - this.container.offsetTop,
          });
          i++;
        }
        this.offsets.next(offsets);
      })
    ).subscribe();


    this.scroll = combineLatest([
      fromEvent(this.container, 'scroll'),
      this.offsets,
    ]).pipe(
      debounceTime(300),
      tap(([elm, offsets]) => {
        const target = elm.target as HTMLElement;
        const soff = target.scrollTop;
        if (this.container.scrollHeight - (soff + target.clientHeight) === 0 && soff !== 0) {
          this.focusOn(offsets[size - 1].id);
        } else {
          const match = this.findMatch(soff, offsets);
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

  findMatch(top: number, offsets) {
    const size = offsets.length;
    let i = 0;
    while (i < (size - 1)) {
      if (top >= offsets[i].top && top < offsets[i + 1].top) {
        return offsets[i];
      }
      i++;
    }
    return offsets[0];
  }

  ngOnInit(): void {
  }

  ngOnDestroy() {
    if (this.refresh) {
      this.refresh.unsubscribe();
    }

    if (this.scroll) {
      this.scroll.unsubscribe();
    }
  }

}
