import { Component, OnInit, Input, AfterViewInit, ViewChild, OnDestroy, OnChanges, SimpleChanges, EventEmitter, Output } from '@angular/core';
import { TREE_ACTIONS, TreeComponent, TreeNode } from 'angular-tree-component';
import { fromEvent, Subscription, Subject, interval, combineLatest, BehaviorSubject } from 'rxjs';
import { tap, debounceTime, filter } from 'rxjs/operators';

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
  scrollTriggeredByClick = false;
  pnodes: ITocNode[];
  options;
  offsets: BehaviorSubject<{
    id: string,
    top: number,
  }[]>;
  container: HTMLElement;
  mouseWheel: Subscription;
  scroll: Subscription;
  refresh: Subscription;
  thresholdFilterValue: boolean;

  @Output()
  thresholdFilter: EventEmitter<boolean>;

  @Input()
  set nodes(nodes: ITocNode[]) {
    this.pnodes = nodes;
  }

  get nodes() {
    return this.pnodes;
  }

  constructor() {
    this.thresholdFilter = new EventEmitter();
    this.offsets = new BehaviorSubject([]);
    this.options = {
      allowDrag: false,
      actionMapping: {
        mouse: {
          click: (elm, node, event) => {
            const target = document.getElementById(node.data.id);
            const position = target.offsetTop;
            this.scrollTriggeredByClick = true;
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

  // tslint:disable-next-line: cognitive-complexity
  ngAfterViewInit() {
    // Init Active node tree
    this.treeComponent.treeModel.expandAll();
    const firstNode: TreeNode = this.treeComponent.treeModel.getFirstRoot();
    firstNode.setActiveAndVisible();

    // Get DOM Elements
    this.container = document.getElementsByClassName('container-content').item(0) as HTMLElement;
    this.refresh = interval(800).pipe(
      tap(() => {
        const collection = document.getElementsByClassName('scroll-to');
        const size = collection.length;
        let i = 0;
        let offsets = this.offsets.getValue();
        while (i < size) {
          const elm = collection.item(i) as HTMLElement;
          const top = elm.offsetTop - this.container.offsetTop;
          if (offsets.length > i && (offsets[i].id !== elm.id || offsets[i].top !== top)) {
            offsets = [...offsets];
            offsets[i] = {
              top,
              id: elm.id,
            };
          } else if (offsets.length <= i) {
            offsets = [...offsets, {
              top,
              id: elm.id,
            }];
          }
          i++;
        }
        const previous = this.offsets.getValue();
        if (i < previous.length) {
          offsets = offsets.slice(0, i);
        }

        if (previous !== offsets) {
          this.offsets.next(offsets);
        }
      })
    ).subscribe();


    this.scroll = combineLatest([
      fromEvent(this.container, 'scroll'),
      this.offsets,
    ]).pipe(
      filter(() => !this.scrollTriggeredByClick),
      debounceTime(300),
      tap(([elm, offsets]) => {
        const size = offsets.length;
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

    this.mouseWheel = fromEvent(this.container, 'mousewheel').pipe(
      tap(() => {
        this.scrollTriggeredByClick = false;
      })
    ).subscribe();

  }

  filterText(value: string) {
    this.treeComponent.treeModel.filterNodes((node) => {
      return node.data.path.includes(value) || node.data.header.includes(value);
    });
  }

  filterThreshold(value: boolean) {
    this.thresholdFilter.emit(value);
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

    if (this.mouseWheel) {
      this.scroll.unsubscribe();
    }
  }

}
