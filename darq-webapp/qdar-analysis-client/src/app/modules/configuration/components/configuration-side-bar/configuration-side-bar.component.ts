import {Component, Input, OnInit} from '@angular/core';
import {IConfigurationDescriptor, IDigestConfiguration} from '../../model/configuration.model';
import {ConfigurationService} from '../../services/configuration.service';
import {
  ConfirmDialogComponent,
  DamWidgetComponent,
  DeleteResourcesFromRepository,
  InsertResourcesInCollection,
  MessageType,
  RxjsStoreHelperService,
} from '@usnistgov/ngx-dam-framework-legacy';
import {MatDialog} from '@angular/material/dialog';
import {concatMap, flatMap, map} from 'rxjs/operators';
import {Store} from '@ngrx/store';
import {BehaviorSubject, combineLatest, from, Observable, of} from 'rxjs';
import {GoToEntity} from '../../../shared/store/core.actions';
import {EntityType} from 'src/app/modules/shared/model/entity.model';
import {filterDescriptorByType, FilterType} from '../../../shared/model/filter.model';
import {selectCurrentUserId} from '../../../core/store/core.selectors';

@Component({
  selector: 'app-configuration-side-bar',
  templateUrl: './configuration-side-bar.component.html',
  styleUrls: ['./configuration-side-bar.component.scss']
})
export class ConfigurationSideBarComponent implements OnInit {

  @Input()
  set configurations(c: IConfigurationDescriptor[]) {
    this.configurationsSubject.next([...c.map((v) => ({ ...v }))]);
  }

  get configurations() {
    return this.configurationsSubject.getValue();
  }

  filtered$: Observable<IConfigurationDescriptor[]>;
  configurationsSubject: BehaviorSubject<IConfigurationDescriptor[]>;
  listTypeSubject: BehaviorSubject<FilterType>;
  filterTextSubject: BehaviorSubject<string>;
  userId$: Observable<string>;
  filterType = FilterType;

  constructor(
    public widget: DamWidgetComponent,
    private configurationService: ConfigurationService,
    private helper: RxjsStoreHelperService,
    private dialog: MatDialog,
    private store: Store<any>,
  ) {
    this.configurationsSubject = new BehaviorSubject([]);
    this.listTypeSubject = new BehaviorSubject(FilterType.ALL);
    this.filterTextSubject = new BehaviorSubject('');
    this.userId$ = this.store.select(selectCurrentUserId);
    this.filtered$ = combineLatest([
      this.configurationsSubject,
      this.listTypeSubject,
      this.filterTextSubject,
      this.userId$,
    ]).pipe(
      map(([configuration, type, text, userId]) => {
        return filterDescriptorByType(configuration, type, userId).filter((conf) => {
          return conf.name.includes(text);
        });
      }),
    );
  }

  set listType(type: FilterType) {
    this.listTypeSubject.next(type);
  }

  get listType() {
    return this.listTypeSubject.getValue();
  }

  get filterText() {
    return this.filterTextSubject.getValue();
  }

  set filterText(text: string) {
    this.filterTextSubject.next(text);
  }

  // tslint:disable-next-line: cognitive-complexity
  deleteConfiguration(configuration: IConfigurationDescriptor, active: boolean) {
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        action: 'Delete Configuration',
        question: 'Are you sure you want to delete configuration ' + configuration.name + ' ? <br>' +
          (configuration.published || configuration.locked ? '<ul>' : '') +
          (configuration.published ? '<li> this configuration is published and might be used by other users </li>' : '') +
          (configuration.locked ? '<li> this configuration is locked </li>' : '') +
          (configuration.published || configuration.locked ? '<ul>' : ''),
      }
    }).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle<IDigestConfiguration>(
            this.store,
            () => {
              return this.configurationService.delete(configuration.id);
            },
            (message) => {
              return from(message.status === MessageType.SUCCESS ? [new DeleteResourcesFromRepository({
                collections: [{
                  key: 'configurations',
                  values: [configuration.id],
                }],
              }),
              ...(active ? [
                new GoToEntity({
                  type: EntityType.CONFIGURATION,
                }),
              ] : [])
              ] : []);
            }
          );
        }
        return of();
      }),
    ).subscribe();
  }

  readonly addAndOpenHandler = (message) => {
    return message.status === MessageType.SUCCESS ?
      this.configurationService.getDescriptorById(message.data.id).pipe(
        flatMap((descriptor) => {
          return from([
            new InsertResourcesInCollection({
              key: 'configurations',
              values: [
                descriptor,
              ],
            }),
            new GoToEntity({
              type: EntityType.CONFIGURATION,
              id: message.data.id,
            }),
          ]);
        })
      ) : from([]);
  }

  cloneConfiguration(configuration: IConfigurationDescriptor) {
    return this.helper.getMessageAndHandle<IDigestConfiguration>(
      this.store,
      () => {
        return this.configurationService.clone(configuration.id);
      },
      this.addAndOpenHandler,
    ).subscribe();
  }

  createConfiguration() {
    return this.helper.getMessageAndHandle<IDigestConfiguration>(
      this.store,
      () => {
        return this.configurationService.save(this.configurationService.getEmptyConfiguration());
      },
      this.addAndOpenHandler,
    ).subscribe();
  }


  ngOnInit(): void {
  }

}
