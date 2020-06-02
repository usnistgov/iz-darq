import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IRange, IBracket } from '../model/age-group.model';

@Injectable({
  providedIn: 'root'
})
export class AgeGroupService {


  constructor(private http: HttpClient) { }

  getAgeGroupLabel(range: IRange): string {
    return this.getBracketLabel(range.min) + ' -> ' + this.getBracketLabel(range.max);
  }

  getBracketLabel(bracket: IBracket) {
    return [
      ...bracket.year ? [bracket.year,
      this.plural(bracket.year, 'year'),
      ] : [],
      ...bracket.month ? [bracket.month,
      this.plural(bracket.year, 'month'),
      ] : [],
      ...bracket.day ? [bracket.day,
      this.plural(bracket.year, 'day'),
      ] : [],
      (bracket.month + bracket.day + bracket.year) === 0 ? 'Birth' : '',
    ].join(' ');
  }

  removeBracket(ranges: IRange[], i: number): IRange[] {
    const removable: IRange = ranges[i];
    const next: IRange = i < ranges.length - 1 ? ranges[i + 1] : undefined;
    return [
      ...[...ranges].slice(0, i),
      ...next ? [{
        min: removable.min,
        max: next.max,
      }] : [],
      ...[...ranges].slice(i + 2, ranges.length),
    ];
  }

  bracketWeight(bracket: IBracket): number {
    return bracket.year * 365 + bracket.month * 30 + bracket.day;
  }

  rangeWeight(range: IRange): number {
    return this.bracketWeight(range.max) - this.bracketWeight(range.min);
  }

  insertBracket(ranges: IRange[], bracket: IBracket): IRange[] {
    let exists = false;
    let withinRange = -1;
    let i = 0;

    for (const range of ranges) {
      const compareMaxWeight = this.compare(range.max, bracket);
      const compareMinWeight = this.compare(range.min, bracket);

      if (compareMaxWeight === 0 || compareMinWeight === 0) {
        exists = true;
        break;
      }

      if (compareMinWeight < 0 && compareMaxWeight > 0) {
        withinRange = i;
        break;
      }

      i++;
    }

    if (!exists) {
      if (withinRange !== -1) {
        const split = ranges[withinRange];
        return [
          ...[...ranges].slice(0, withinRange),
          {
            min: split.min,
            max: bracket,
          },
          {
            min: bracket,
            max: split.max,
          },
          ...[...ranges].slice(withinRange + 1, ranges.length),
        ];
      } else {
        const openBracket = this.openBracket(ranges);
        const compareOpenAndBracket = this.compare(openBracket, bracket);
        return compareOpenAndBracket < 0 ? [
          ...ranges,
          {
            min: openBracket,
            max: bracket,
          }
        ] : [
            ...ranges,
          ];
      }
    } else {
      return [
        ...ranges,
      ];
    }
  }

  openBracket(ranges: IRange[]): IBracket {
    if (ranges.length > 0) {
      return ranges[ranges.length - 1].max;
    } else {
      return {
        year: 0,
        month: 0,
        day: 0,
      };
    }
  }

  compare(a: IBracket, b: IBracket): number {
    const aWeight = this.bracketWeight(a);
    const bWeight = this.bracketWeight(b);
    return aWeight - bWeight;
  }

  plural(n: number, word: string): string {
    return n > 1 || n === 0 ? word + 's' : word;
  }
}
