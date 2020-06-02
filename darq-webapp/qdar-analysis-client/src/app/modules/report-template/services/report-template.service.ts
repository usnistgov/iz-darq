import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message, LoadPayloadData, InsertResourcesInCollection, SetValue, DeleteResourcesFromCollection, LoadResourcesInRepository } from 'ngx-dam-framework';
import { EntityType } from '../../shared/model/entity.model';
import { IReportTemplateDescriptor, IReportTemplate, IReportSection, IReportTemplateCreate, IDataViewQuery, Comparator } from '../model/report-template.model';
import { IDescriptor } from '../../shared/model/descriptor.model';
import { Action } from '@ngrx/store';
import { IReportSectionDisplay } from '../model/state.model';
import { Guid } from 'guid-typescript';
import { AnalysisType, Field } from '../model/analysis.values';

@Injectable({
  providedIn: 'root'
})
export class ReportTemplateService {

  readonly URL_PREFIX = 'api/template/';

  constructor(private http: HttpClient) { }

  getEmptyTemplate(): IReportTemplate {
    return {
      id: undefined,
      name: '',
      description: '',
      owner: undefined,
      lastUpdated: undefined,
      published: false,
      type: EntityType.TEMPLATE,
      viewOnly: undefined,
      configuration: undefined,
      sections: [],
    };
  }

  getEmptyDataViewQuery(): IDataViewQuery {
    return {
      type: AnalysisType.VACCINCATIONS_DETECTIONS,
      caption: '',
      paginate: true,
      rows: 10,
      selectors: [],
      groupBy: [
        Field.DETECTION,
      ],
      filter: {
        denominator: {
          active: false,
          value: 0,
          comparator: Comparator.GT,
        },
        percentage: {
          active: false,
          value: 0,
          comparator: Comparator.GT,
        },
        threshold: {
          active: false,
          pass: true,
        },
        groups: {
          keep: false,
          active: false,
          values: [],
        }
      },
      threshold: {
        global: {
          active: false,
          goal: {
            value: 1,
            comparator: Comparator.LT,
          }
        },
        custom: {
          active: false,
          thresholds: [],
        }
      },
    };
  }

  mergeMetadata(current: IReportTemplate, meta: Partial<IReportTemplate>): IReportTemplate {
    return {
      ...current,
      ...meta,
    };
  }

  mergeSection(
    current: IReportTemplate,
    path: string,
    section: Partial<IReportSection>
  ): {
    reportTemplate: IReportTemplate,
    section: IReportSection,
  } {
    const { parent } = this.getParentAndTarget(path);
    const { root, target } = this.getMutableSectionByPath(current.sections, parent);
    const targetSectionList: IReportSection[] = target ? target.children : root;
    const targetIndex: number = targetSectionList.findIndex((elm) => elm.path === path);
    const mergedSection = {
      ...targetSectionList[targetIndex],
      ...section,
    };
    targetSectionList.splice(targetIndex, 1, mergedSection);
    this.sortList(targetSectionList, target, true);
    return {
      reportTemplate: {
        ...current,
        sections: root,
      },
      section: mergedSection,
    };
  }

  getParentAndTarget(path: string): { parent: string, targetPath: string } {
    if (path) {
      const split = path.split('.');
      if (split.length === 1) {
        return {
          parent: undefined,
          targetPath: path,
        };
      } else {
        return {
          parent: split.slice(0, split.length - 1).join('.'),
          targetPath: split[split.length - 1],
        };
      }
    }

    return {
      parent: undefined,
      targetPath: undefined,
    };
  }

  cloneSectionAndChildren(section: IReportSection): IReportSection {
    return {
      ...section,
      children: this.cloneContent(section.children),
    };
  }

  getSectionDisplay(section: IReportSection): IReportSectionDisplay {
    return {
      id: section.id,
      path: section.path,
      name: section.header,
      type: EntityType.SECTION,
      position: section.position,
    };
  }

  cloneContent(sections: IReportSection[]): IReportSection[] {
    return [...sections.map(elm => {
      return {
        ...elm
      };
    })];
  }

  // tslint:disable-next-line: cognitive-complexity
  getMutableSectionByPath(
    sections: IReportSection[],
    path: string,
    preserve: boolean = false,
  ): { root: IReportSection[], target: IReportSection } {
    const list = !preserve ? this.cloneContent(sections) : sections;
    if (path) {
      const getTarget = (sectionsList: IReportSection[]): IReportSection => {
        let i = 0;
        for (const section of sectionsList) {
          if (section.path === path) {
            if (preserve) {
              return section;
            }

            const clone = this.cloneSectionAndChildren(section);
            sectionsList.splice(i, 1, clone);
            return clone;
          } else if (path.startsWith(section.path)) {
            if (preserve) {
              return getTarget(section.children);
            }

            const clone = this.cloneSectionAndChildren(section);
            sectionsList.splice(i, 1, clone);
            return getTarget(clone.children);
          }
          i++;
        }
      };

      return {
        root: list,
        target: getTarget(list),
      };

    } else {
      return {
        root: list,
        target: undefined,
      };
    }
  }

  moveSection(rt: IReportTemplate, sourceId: string, sectionId: string, index: number, targetId: string) {

    // Delete
    const { root: clone, target: source } = this.getMutableSectionByPath(rt.sections, sourceId);
    const sourceSectionList: IReportSection[] = sourceId ? source.children : clone;
    const sourceIndex: number = sourceSectionList.findIndex((elm) => elm.id === sectionId);
    const [node] = sourceSectionList.splice(sourceIndex, 1);

    // Add
    const { root, target } = this.getMutableSectionByPath(clone, targetId);
    const targetSectionList: IReportSection[] = targetId ? target.children : root;
    const i = index >= 0 ? index : targetSectionList.length;
    targetSectionList.splice(i, 0, node);

    this.sortList(root, undefined, true);

    const payload = {
      ...rt,
      sections: root,
    };
    return [
      new LoadPayloadData(payload),
      new SetValue({ tableOfContentChanged: true }),
      new LoadResourcesInRepository({
        collections: [{
          key: 'sections',
          values: this.flattenSections(payload.sections),
        }]
      })
    ];
  }

  deleteSection(rt: IReportTemplate, path: string, parent?: string) {
    const { root, target } = this.getMutableSectionByPath(rt.sections, parent);
    const targetSectionList: IReportSection[] = parent ? target.children : root;
    const removeIndex = targetSectionList.findIndex((elm) => elm.path === path);
    const [node] = targetSectionList.splice(removeIndex, 1);
    this.sortList(targetSectionList, target, true);
    const payload = {
      ...rt,
      sections: root,
    };
    return [
      new LoadPayloadData(payload),
      new SetValue({ tableOfContentChanged: true }),
      new DeleteResourcesFromCollection({
        key: 'sections',
        values: [node.id],
      }),
    ];
  }

  createAndAddSection(rt: IReportTemplate, index?: number, path?: string): Action[] {
    const { root, target } = this.getMutableSectionByPath(rt.sections, path);
    const targetSectionList: IReportSection[] = path ? target.children : root;
    const section = this.createChildSection(targetSectionList);
    const i = index && index >= 0 ? index : targetSectionList.length;
    targetSectionList.splice(i, 0, section);
    this.sortList(targetSectionList, target, true);
    const payload = {
      ...rt,
      sections: root,
    };

    return [
      new LoadPayloadData(payload),
      new SetValue({ tableOfContentChanged: true }),
      new InsertResourcesInCollection({
        key: 'sections',
        values: [{
          type: EntityType.SECTION,
          ...section,
        }],
      }),
    ];
  }


  flattenSections(list: IReportSection[]): IReportSection[] {
    const sections: IReportSection[] = [];

    const loop = (l: IReportSection[]) => {
      if (!l || l.length === 0) {
        return;
      }

      l.forEach((elm) => {
        sections.push(elm);
        loop(elm.children);
      });
    };

    loop(list);
    return sections;
  }

  sortList(list: IReportSection[], parent?: IReportSection, update: boolean = false) {
    if (list) {
      list.forEach((section, i) => {
        if (update) {
          section.position = i + 1;
          section.path = parent ? parent.path + '.' + section.position : section.position + '';
        }
        section.children = this.cloneContent(section.children);
        this.sortList(section.children, section, update);
      });
      list.sort((a, b) => a.position - b.position);
    }
  }


  createChildSection(list: IReportSection[]): IReportSection {
    return {
      id: Guid.create().toString(),
      type: EntityType.SECTION,
      path: '',
      header: 'Section',
      text: '',
      position: undefined,
      children: [],
      data: [],
    };
  }

  getReportTemplates(): Observable<IReportTemplateDescriptor[]> {
    return this.http.get<IReportTemplateDescriptor[]>(this.URL_PREFIX);
  }

  getById(id: string): Observable<IReportTemplate> {
    return this.http.get<IReportTemplate>(this.URL_PREFIX + id);
  }

  save(template: IReportTemplate): Observable<Message<IReportTemplate>> {
    return this.http.post<Message<IReportTemplate>>(this.URL_PREFIX, template);
  }

  create(req: IReportTemplateCreate): Observable<Message<IReportTemplate>> {
    return this.http.post<Message<IReportTemplate>>(this.URL_PREFIX + '/create', req);
  }

  delete(id: string): Observable<Message<IReportTemplateDescriptor>> {
    return this.http.delete<Message<IReportTemplateDescriptor>>(this.URL_PREFIX + id);
  }

  publish = (id: string): Observable<Message<IReportTemplate>> => {
    return this.http.post<Message<IReportTemplate>>(this.URL_PREFIX + id + '/publish', {});
  }

  clone(id: string): Observable<Message<IReportTemplate>> {
    return this.http.post<Message<IReportTemplate>>(this.URL_PREFIX + id + '/clone', {});
  }

  getDescriptorById(id: string): Observable<IReportTemplateDescriptor> {
    return this.http.get<IReportTemplateDescriptor>(this.URL_PREFIX + id + '/descriptor');
  }

  getDescriptor(configuration: IReportTemplate, owned: boolean): IDescriptor {
    return {
      id: configuration.id,
      type: EntityType.TEMPLATE,
      name: configuration.name,
      owner: configuration.owner,
      lastUpdated: configuration.lastUpdated,
      viewOnly: configuration.viewOnly,
      locked: false,
      published: configuration.published,
      owned,
    };
  }

}
