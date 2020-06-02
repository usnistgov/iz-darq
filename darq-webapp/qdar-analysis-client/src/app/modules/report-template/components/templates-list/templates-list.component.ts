import { Component, OnInit } from '@angular/core';
import { Observable, of, Subject, combineLatest, BehaviorSubject } from 'rxjs';
import { IReportTemplateDescriptor, IReportTemplate } from '../../model/report-template.model';
import { Store } from '@ngrx/store';
import { selectReportTemplates } from '../../store/core.selectors';
import {
  selectIsAdmin,
  RxjsStoreHelperService,
  ConfirmDialogComponent,
  MessageType,
  DeleteResourcesFromRepository,
  InsertResourcesInCollection,
} from 'ngx-dam-framework';
import { MatDialog } from '@angular/material/dialog';
import { concatMap, map, take, flatMap } from 'rxjs/operators';
import { ReportTemplateService } from '../../services/report-template.service';
import { FilterType } from 'src/app/modules/shared/model/filter.model';
import { filterDescriptorByType } from '../../../shared/model/filter.model';
import { GoToEntity } from '../../../shared/store/core.actions';
import { EntityType } from '../../../shared/model/entity.model';
import { CreateRtDialogComponent } from '../create-rt-dialog/create-rt-dialog.component';
import { IConfigurationDescriptor } from '../../../configuration/model/configuration.model';
import { selectConfigurations } from '../../../configuration/store/core.selectors';

@Component({
  selector: 'app-templates-list',
  templateUrl: './templates-list.component.html',
  styleUrls: ['./templates-list.component.scss']
})
export class TemplatesListComponent implements OnInit {

  templates$: Observable<IReportTemplateDescriptor[]>;
  configurations$: Observable<IConfigurationDescriptor[]>;
  isAdmin$: Observable<boolean>;
  listTypeSubject: BehaviorSubject<FilterType>;
  filterType = FilterType;

  constructor(
    private store: Store<any>,
    private helper: RxjsStoreHelperService,
    private templateService: ReportTemplateService,
    private dialog: MatDialog) {
    this.listTypeSubject = new BehaviorSubject(FilterType.ALL);
    this.configurations$ = this.store.select(selectConfigurations);
    this.templates$ = combineLatest([
      this.listTypeSubject,
      this.store.select(selectReportTemplates),
    ]).pipe(
      map(([filter, list]) => {
        return filterDescriptorByType(list, filter);
      })
    );
    this.isAdmin$ = this.store.select(selectIsAdmin);
  }

  set listType(type: FilterType) {
    this.listTypeSubject.next(type);
  }

  get listType() {
    return this.listTypeSubject.getValue();
  }

  remove(template: IReportTemplateDescriptor) {
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        action: 'Delete Report Template',
        question: 'Are you sure you want to delete template ' + template.name + ' ? <br>' +
          (template.published ? '<ul><li> this configuration is published and might be used by other users </li></ul>' : ''),
      },
    }).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle<IReportTemplateDescriptor>(
            this.store,
            () => {
              return this.templateService.delete(template.id);
            },
            (message) => {
              return message.status === MessageType.SUCCESS ? [new DeleteResourcesFromRepository({
                collections: [{
                  key: 'templates',
                  values: [template.id],
                }],
              })] : [];
            }
          );
        }
        return of();
      }),
    ).subscribe();
  }

  readonly addAndOpenHandler = (message) => {
    return message.status === MessageType.SUCCESS ? [new InsertResourcesInCollection({
      key: 'templates',
      values: [
        this.templateService.getDescriptor(message.data, true),
      ],
    }),
    new GoToEntity({
      type: EntityType.TEMPLATE,
      id: message.data.id,
    }),
    ] : [];
  }

  clone(template: IReportTemplateDescriptor) {
    return this.helper.getMessageAndHandle<IReportTemplate>(
      this.store,
      () => {
        return this.templateService.clone(template.id);
      },
      this.addAndOpenHandler,
    ).subscribe();
  }

  create() {
    this.configurations$.pipe(
      take(1),
      flatMap((configurations) => {
        return this.dialog.open(CreateRtDialogComponent, {
          data: {
            configurations,
          }
        }).afterClosed().pipe(
          flatMap((data) => {
            if (data) {
              return this.helper.getMessageAndHandle<IReportTemplate>(
                this.store,
                () => {
                  return this.templateService.create(data);
                },
                this.addAndOpenHandler,
              );
            }
            return of();
          }),
        );
      })
    ).subscribe();
  }

  publish(template: IReportTemplateDescriptor) {
    return this.dialog.open(ConfirmDialogComponent, {
      data: {
        action: 'Publish Template',
        question: 'Are you sure you want to publish template ' + template.name + ',<br> this will make it globally available for all users ?',
      },
    }).afterClosed().pipe(
      concatMap((answer) => {
        if (answer) {
          return this.helper.getMessageAndHandle<IReportTemplate>(
            this.store,
            () => {
              return this.templateService.publish(template.id);
            },
            this.addAndOpenHandler,
          );
        }
        return of();
      }),
    ).subscribe();
  }

  ngOnInit(): void {
  }

}
