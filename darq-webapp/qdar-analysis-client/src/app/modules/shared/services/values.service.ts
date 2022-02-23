import { Injectable } from '@angular/core';
import { IFieldInputOptions, IFieldInputData } from '../components/field-input/field-input.component';
import { Comparator } from '../../report-template/model/report-template.model';
import { SelectItem } from 'primeng/api/selectitem';
import { AgeGroupService } from './age-group.service';
import { Field } from '../../report-template/model/analysis.values';

export interface ILabelMap {
  [key: string]: string;
}

export interface IValueLabelMap {
  [field: string]: ILabelMap;
  comparators: ILabelMap;
}

export class Labelizer {
  constructor(public values: IValueLabelMap, public options: IFieldInputOptions) { }
  for(field: Field, value: any): string {
    return this.values[field] ? this.values[field][value] ? this.values[field][value] : value : value;
  }
  comparator(cmp: Comparator) {
    return this.values.comparators[cmp];
  }
}

@Injectable({
  providedIn: 'root'
})
export class ValuesService {

  constructor(private ageGroupService: AgeGroupService) { }

  getQueryValuesLabel(options: IFieldInputOptions): Labelizer {
    const map: IValueLabelMap = {
      comparators: {
        [Comparator.LT]: 'Lower than',
        [Comparator.GT]: 'Higher than',
      },
    };

    const processOptions = (items: SelectItem[], field: Field) => {
      if (!map[field]) {
        map[field] = {};
      }

      items.forEach((option) => {
        map[field][option.value] = option.label;
      });
    };

    processOptions(options.vaccinationDetectionOptions, Field.DETECTION);
    processOptions(options.patientDetectionOptions, Field.DETECTION);
    processOptions(options.reportingGroupOptions, Field.PROVIDER);
    processOptions(options.cvxOptions, Field.VACCINE_CODE);
    processOptions(options.ageGroupOptions, Field.AGE_GROUP);

    return new Labelizer(map, options);
  }

  getFieldOptions(data: IFieldInputData, customLabels: Record<string, string>): IFieldInputOptions {
    const standardTransform = (elm) => {
      return {
        label: elm,
        value: elm,
      };
    };
    const detectionLabels = customLabels || {};
    const detectionTransform = (elm) => {
      return {
        label: elm.id + ' - ' + (detectionLabels[elm.id] || elm.description),
        value: elm.id,
      };
    };
    const ageGroups = data.ageGroups || [];

    ageGroups.sort((a, b) => {
      return this.ageGroupService.compare(a.min, b.min);
    });

    return {
      vaccinationDetectionOptions: data.detections.filter(d => d.target === 'VACCINATION').map(detectionTransform),
      patientDetectionOptions: data.detections.filter(d => d.target !== 'VACCINATION').map(detectionTransform),
      cvxOptions: data.cvxs.map((elm) => {
        return {
          label: elm.id + ' - ' + elm.name,
          value: elm.id,
        };
      }),
      reportingGroupOptions: Object.keys(data.reportingGroups || {}).map((hash) => ({ label: data.reportingGroups[hash], value: hash })),
      ageGroupOptions: [
        ...ageGroups.map((elm, i) => {
          return {
            label: this.ageGroupService.getAgeGroupLabel(elm),
            value: i + 'g',
          };
        }),
        {
          label: this.ageGroupService.getBracketLabel(this.ageGroupService.openBracket(ageGroups)) + ' -> + infinity',
          value: ageGroups.length + 'g',
        }
      ],
      vaccinationTableOptions: data.tables.vaccinationTables.map(standardTransform),
      patientTableOptions: data.tables.patientTables.map(standardTransform)
    };
  }

}
