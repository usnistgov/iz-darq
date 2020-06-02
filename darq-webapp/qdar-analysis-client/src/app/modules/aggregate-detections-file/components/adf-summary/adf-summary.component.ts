import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, combineLatest, Subject, Subscription, BehaviorSubject, ReplaySubject } from 'rxjs';
import { IADFMetadata, IExtractPercent } from '../../model/adf.model';
import { Store } from '@ngrx/store';
import { selectOpenFileMetadata } from '../../store/core.selectors';
import { AgeGroupService } from '../../../shared/services/age-group.service';
import { tap, map, switchMap } from 'rxjs/operators';
import { selectDetectionById } from '../../../shared/store/core.selectors';
import { IDetectionResource } from '../../../shared/model/public.model';

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

  constructor(
    private store: Store<any>,
    public agService: AgeGroupService) {
    this.adfMeta$ = this.store.select(selectOpenFileMetadata);
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

    this.supported$ = new ReplaySubject();
    this.unrecognized$ = new ReplaySubject();
    this.inactive$ = new ReplaySubject();

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

  ngOnInit(): void {
  }

}
