import {Component, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {selectIsAdmin} from '@usnistgov/ngx-dam-framework-legacy';
import {AgeGroupService} from '../../../shared/services/age-group.service';
import {selectUserFacilities} from '../../store/core.selectors';
import {IADFMetadata} from '../../model/adf.model';
import {EXTRACT_COLUMNS, IAdfComparison, IExtractDifferential} from '../../model/adf-comparison.model';
import {AdfComparisonService} from '../../services/adf-comparison.service';

@Component({
  selector: 'app-adf-comparison-summary',
  templateUrl: './adf-comparison-summary.component.html',
  styleUrls: ['./adf-comparison-summary.component.scss']
})
export class AdfComparisonSummaryComponent implements OnInit {

  extractColumns = EXTRACT_COLUMNS;
  comparison: IAdfComparison;
  extractItems: IExtractDifferential[] = [];
  isAdmin$: Observable<boolean>;
  dashboardRoute$: Observable<string[]>;
  facilityMap$: Observable<Record<string, string>>;

  /**
   * Hides the dashboard back-link when the summary is shown inside a dialog
   * rather than as a standalone page.
   */
  @Input()
  embedded = false;

  @Input()
  set baseMeta(meta: IADFMetadata) {
    this.pbaseMeta = meta;
    this.build();
  }

  get baseMeta() {
    return this.pbaseMeta;
  }

  @Input()
  set newMeta(meta: IADFMetadata) {
    this.pnewMeta = meta;
    this.build();
  }

  get newMeta() {
    return this.pnewMeta;
  }

  private pbaseMeta: IADFMetadata;
  private pnewMeta: IADFMetadata;

  constructor(
    private store: Store<any>,
    public agService: AgeGroupService,
    private comparisonService: AdfComparisonService,
    private activatedRoute: ActivatedRoute,
  ) {
    this.isAdmin$ = this.store.select(selectIsAdmin);
    this.dashboardRoute$ = this.activatedRoute.queryParams.pipe(
      map((queryParams) => {
        return queryParams.facilityId ? ['/', 'adf', 'dashboard', queryParams.facilityId] : ['/', 'adf', 'dashboard'];
      })
    );
    this.facilityMap$ = this.store.select(selectUserFacilities).pipe(
      map((facilities) => {
        const facilityMap = {};
        facilities.forEach((f) => {
          facilityMap[f.id] = f.name;
        });
        return facilityMap;
      }),
    );
  }

  /**
   * Differentials are computed on the client from the two metadata payloads,
   * so no comparison endpoint is involved.
   */
  build() {
    // Both summaries are required: an ADF without one carries no statistics to
    // compare, and would otherwise render as a page of empty tables.
    if (!this.pbaseMeta || !this.pnewMeta || !this.pbaseMeta.summary || !this.pnewMeta.summary) {
      this.comparison = undefined;
      this.extractItems = [];
      return;
    }
    this.comparison = this.comparisonService.compare(this.pbaseMeta, this.pnewMeta);
    this.extractItems = this.comparison.extract;
  }

  ngOnInit(): void {
  }

}
