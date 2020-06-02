import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Store, Action } from '@ngrx/store';
import { IReportSection } from '../../model/report-template.model';
import { ReportTemplateService } from '../../services/report-template.service';
import { selectReportTemplate } from '../../store/core.selectors';
import { take, flatMap, tap, map } from 'rxjs/operators';
import { ITreeOptions, TreeModel } from 'angular-tree-component';
import { TreeNode } from 'primeng/api/treenode';


@Component({
  selector: 'app-rt-side-bar',
  templateUrl: './rt-side-bar.component.html',
  styleUrls: ['./rt-side-bar.component.scss']
})
export class RtSideBarComponent implements OnInit, OnDestroy {

  @Input()
  sections: IReportSection[];
  @Input()
  set viewOnly(vo: boolean) {
    this.options = {
      allowDrag: !vo,
      actionMapping: {
        mouse: {
          drop: (tree: TreeModel, node: TreeNode, $event: any, { from, to }) => {
            if (tree.canMoveNode(from, to, from.getIndexInParent())) {
              this.store.select(selectReportTemplate).pipe(
                take(1),
                flatMap((rt) => {
                  return this.reportTemplateService.moveSection(
                    rt,
                    from.parent.data.virtual ? undefined : from.parent.data.path,
                    from.data.id,
                    to.index,
                    to.parent.data.virtual ? undefined : to.parent.data.path,
                  );
                }),
                tap((a: Action) => this.store.dispatch(a)),
              ).subscribe();
            }
          },
          click: () => { },
        }
      }
    };
  }
  options: ITreeOptions;

  constructor(
    private store: Store<any>,
    private reportTemplateService: ReportTemplateService,
  ) {

  }

  createNewSection(parent?: string) {
    this.store.select(selectReportTemplate).pipe(
      take(1),
      flatMap((rt) => {
        return this.reportTemplateService.createAndAddSection(rt, -1, parent);
      }),
      tap((a: Action) => this.store.dispatch(a)),
    ).subscribe();
  }

  deleteSection(id: string, parentNode?: any) {
    const parent = parentNode.data.virtual ? undefined : parentNode.data.path;
    this.store.select(selectReportTemplate).pipe(
      take(1),
      flatMap((rt) => {
        return this.reportTemplateService.deleteSection(rt, id, parent);
      }),
      tap((a: Action) => this.store.dispatch(a)),
    ).subscribe();
  }

  ngOnDestroy() {
  }

  ngOnInit(): void {
  }

}
