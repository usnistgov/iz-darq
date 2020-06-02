import { Component, OnInit, Input } from '@angular/core';
import { IConfigurationDescriptor, IDigestConfiguration } from '../../model/configuration.model';
import { ConfigurationService } from '../../services/configuration.service';
import {
  ConfirmDialogComponent,
  MessageType,
  DeleteResourcesFromRepository,
  InsertResourcesInCollection,
  RxjsStoreHelperService,
} from 'ngx-dam-framework';
import { MatDialog } from '@angular/material/dialog';
import { concatMap, map } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { of, BehaviorSubject, Observable, combineLatest } from 'rxjs';
import { GoToEntity } from '../../../shared/store/core.actions';
import { EntityType } from 'src/app/modules/shared/model/entity.model';
import { DamWidgetComponent } from 'ngx-dam-framework';
import { FilterType, filterDescriptorByType } from '../../../shared/model/filter.model';

@Component({
  selector: 'app-configuration-side-bar',
  templateUrl: './configuration-side-bar.component.html',
  styleUrls: ['./configuration-side-bar.component.scss']
})
export class ConfigurationSideBarComponent implements OnInit {

  @Input()
  set configurations(c: IConfigurationDescriptor[]) {
    this.configurationsSubject.next(c);
  }

  get configurations() {
    return this.configurationsSubject.getValue();
  }

  filtered$: Observable<IConfigurationDescriptor[]>;
  configurationsSubject: BehaviorSubject<IConfigurationDescriptor[]>;
  listTypeSubject: BehaviorSubject<FilterType>;
  filterTextSubject: BehaviorSubject<string>;

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

    this.filtered$ = combineLatest([
      this.configurationsSubject,
      this.listTypeSubject,
      this.filterTextSubject,
    ]).pipe(
      map(([configuration, type, text]) => {
        return filterDescriptorByType(configuration, type).filter((conf) => {
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
              return message.status === MessageType.SUCCESS ? [new DeleteResourcesFromRepository({
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
              ] : [];
            }
          );
        }
        return of();
      }),
    ).subscribe();
  }

  readonly addAndOpenHandler = (message) => {
    return message.status === MessageType.SUCCESS ? [new InsertResourcesInCollection({
      key: 'configurations',
      values: [
        this.configurationService.getDescriptor(message.data, true),
      ],
    }),
    new GoToEntity({
      type: EntityType.CONFIGURATION,
      id: message.data.id,
    })] : [];
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
