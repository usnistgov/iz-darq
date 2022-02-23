import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, combineLatest, Subscription, ReplaySubject } from 'rxjs';
import { IADFMetadata, IExtractPercent } from '../../model/adf.model';
import { Store } from '@ngrx/store';
import { selectOpenFileMetadata, selectUserFacilities } from '../../store/core.selectors';
import { AgeGroupService } from '../../../shared/services/age-group.service';
import { map, switchMap, tap } from 'rxjs/operators';
import { selectDetectionById } from '../../../shared/store/core.selectors';
import { IDetectionResource } from '../../../shared/model/public.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-adf-summary',
  templateUrl: './adf-summary.component.html',
  styleUrls: ['./adf-summary.component.scss']
})
export class AdfSummaryComponent implements OnInit, OnDestroy {

  adfMeta$: Observable<IADFMetadata>;
  extractItems$: Observable<{
    [P in keyof IExtractPercent]?: IExtractPercent[P];
  } & { label: string }[]>;
  supported$: ReplaySubject<IDetectionResource[]>;
  unrecognized$: ReplaySubject<string[]>;
  inactive$: ReplaySubject<IDetectionResource[]>;
  dSub: Subscription;
  dashboardRoute$: Observable<string[]>;
  facilityMap$: Observable<Record<string, string>>;
  humanReadableDuration: string;
  MS_HOURS = 3600000;
  MS_MINUTES = 60000;
  MS_SECONDS = 1000;

  constructor(
    private store: Store<any>,
    public agService: AgeGroupService,
    private activatedRoute: ActivatedRoute) {
    this.adfMeta$ = this.store.select(selectOpenFileMetadata).pipe(
      tap((meta) => {
        if (meta && meta.totalAnalysisTime && meta.totalAnalysisTime > 0) {
          this.humanReadableDuration = this.humanReadable(meta.totalAnalysisTime);
        } else {
          this.humanReadableDuration = undefined;
        }
      })
    );

    this.extractItems$ = this.store.select(selectOpenFileMetadata).pipe(
      map((meta) => {
        return Object.keys(meta.summary.extract).map((key) => {
          return {
            label: key,
            ...meta.summary.extract[key],
          };
        });
      }),
    );
    this.dashboardRoute$ = this.activatedRoute.queryParams.pipe(
      map((queryParams) => {
        return queryParams.facilityId ? ['/', 'adf', 'dashboard', queryParams.facilityId] : ['/', 'adf', 'dashboard'];
      })
    );
    this.supported$ = new ReplaySubject();
    this.unrecognized$ = new ReplaySubject();
    this.inactive$ = new ReplaySubject();
    this.facilityMap$ = this.store.select(selectUserFacilities).pipe(
      map((facilities) => {
        const facilityMap = {};
        facilities.forEach((f) => {
          facilityMap[f.id] = f.name;
        });
        return facilityMap;
      }),
    );
    this.dSub = this.store.select(selectOpenFileMetadata).pipe(
      switchMap((meta) => {
        return combineLatest(meta.configuration.detections.map((detection) => {
          return this.store.select(selectDetectionById, { id: detection }).pipe(
            map((value) => {
              return { id: detection, value };
            })
          );
        })).pipe(
          map((list) => {

            const unrecognized = list.filter((elm) => !elm.value).map((elm) => elm.id);
            const supported = list
              .filter((elm) => !!elm.value && (!meta.inactiveDetections || !meta.inactiveDetections.includes(elm.id)))
              .map((elm) => elm.value);
            const inactive = list
              .filter((elm) => !!elm.value && meta.inactiveDetections && meta.inactiveDetections.includes(elm.id))
              .map((elm) => elm.value);

            this.unrecognized$.next(unrecognized);
            this.supported$.next(supported);
            this.inactive$.next(inactive);
          })
        );
      }),
    ).subscribe();
  }

  ngOnDestroy() {
    if (this.dSub) {
      this.dSub.unsubscribe();
    }
  }

  humanReadable(ms: number) {
    const hours = Math.floor(ms / this.MS_HOURS);
    const minutes = Math.floor((ms - (hours * this.MS_HOURS)) / this.MS_MINUTES);
    const seconds = Math.floor((ms - (hours * this.MS_HOURS) - (minutes * this.MS_MINUTES)) / this.MS_SECONDS);
    const milliseconds = (ms - (hours * this.MS_HOURS) - (minutes * this.MS_MINUTES) - (seconds * this.MS_SECONDS));

    const parts = [
      ...hours > 0 ? [`${hours} ${this.plural(hours, 'hour')}`] : [],
      ...minutes > 0 ? [`${minutes} ${this.plural(minutes, 'minute')}`] : [],
      ...seconds > 0 ? [`${seconds} ${this.plural(seconds, 'second')}`] : [],
      ...milliseconds > 0 ? [`${milliseconds} ${this.plural(milliseconds, 'millisecond')}`] : [],
    ];

    return parts.join(' ');
  }

  plural(i: number, text: string): string {
    return i > 1 ? `${text}s` : text;
  }

  ngOnInit(): void {
  }

}
