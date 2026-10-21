import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {AdfComparisonService} from './adf-comparison.service';
import {IADFMetadata} from '../model/adf.model';

describe('AdfComparisonService', () => {
  let service: AdfComparisonService;

  const meta = (counts: any, extract: any = {}, ageGroups: any[] = [], outOfRange = 0): IADFMetadata => ({
    summary: { counts, extract, countByAgeGroup: ageGroups, outOfRange },
  } as any);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(AdfComparisonService);
  });

  it('computes a positive delta as new - base', () => {
    const result = service.differential('L', 100, 150);
    expect(result.delta).toBe(50);
    expect(result.missingInBase).toBe(false);
    expect(result.missingInNew).toBe(false);
  });

  it('computes a negative delta', () => {
    expect(service.differential('L', 150, 100).delta).toBe(-50);
  });

  it('treats zero as a real value rather than missing', () => {
    const result = service.differential('L', 0, 5);
    expect(result.missingInBase).toBe(false);
    expect(result.delta).toBe(5);
  });

  it('marks a value missing on one side as incomparable', () => {
    const result = service.differential('L', undefined, 5);
    expect(result.missingInBase).toBe(true);
    expect(result.delta).toBeUndefined();
  });

  it('compares general counts in the summary page order', () => {
    const base = meta({ totalReadPatientRecords: 10, totalReadVaccinations: 4 });
    const next = meta({ totalReadPatientRecords: 25, totalReadVaccinations: 1 });
    const counts = service.compareCounts(base, next);

    expect(counts[0].label).toBe('Total Read Patient Records');
    expect(counts[0].delta).toBe(15);
    expect(counts.find((c) => c.label === 'Total Read Vaccination Records').delta).toBe(-3);
  });

  it('matches age groups by range and keeps groups present on only one side', () => {
    const range1 = { min: { year: 0 }, max: { year: 1 } };
    const range2 = { min: { year: 1 }, max: { year: 2 } };
    const base = meta({}, {}, [{ range: range1, nb: 10 }]);
    const next = meta({}, {}, [{ range: range1, nb: 15 }, { range: range2, nb: 7 }]);

    const groups = service.compareAgeGroups(base, next);
    expect(groups.length).toBe(2);
    expect(groups[0].delta).toBe(5);
    expect(groups[1].missingInBase).toBe(true);
    expect(groups[1].new).toBe(7);
  });

  it('compares extract percentages per column and flags added elements', () => {
    const base = meta({}, { PID5: { valued: 90, empty: 10 } });
    const next = meta({}, { PID5: { valued: 95, empty: 5 }, PID7: { valued: 50 } });

    const extract = service.compareExtract(base, next);
    const pid5 = extract.find((e) => e.label === 'PID5');
    const pid7 = extract.find((e) => e.label === 'PID7');

    expect(pid5.columns.valued.delta).toBe(5);
    expect(pid5.columns.empty.delta).toBe(-5);
    expect(pid7.missingInBase).toBe(true);
  });
});

describe('AdfComparisonService comparability', () => {
  let service: AdfComparisonService;

  const file = (id: string, cliVersion?: string, mqeVersion?: string): any => ({ id, cliVersion, mqeVersion });

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(AdfComparisonService);
  });

  it('matches files on the same cli and mqe version', () => {
    expect(service.areComparable(file('a', '3.1.0', '1.0'), file('b', '3.1.1', '1.0'))).toBe(true);
  });

  it('rejects files whose mqe version differs', () => {
    expect(service.areComparable(file('a', '3.1.0', '1.0'), file('b', '3.1.0', '2.0'))).toBe(false);
  });

  it('rejects files with no cli version recorded', () => {
    expect(service.areComparable(file('a', undefined, '1.0'), file('b', undefined, '1.0'))).toBe(false);
  });

  it('returns nothing when every file lacks a cli version', () => {
    const base = file('a');
    const all = [base, file('b'), file('c')];
    expect(service.comparableTo(base, all).length).toBe(0);
  });

  it('still offers files with no version as candidates, flagged incompatible', () => {
    const base = file('a');
    const candidates = service.candidatesFor(base, [base, file('b'), file('c')]);
    expect(candidates.length).toBe(2);
    expect(candidates.every((c) => !c.compatible)).toBe(true);
  });

  it('excludes the base file from the candidates', () => {
    const base = file('a', '3.1.0', '1.0');
    const candidates = service.candidatesFor(base, [base, file('b', '3.1.0', '1.0')]);
    expect(candidates.map((c) => c.file.id)).toEqual(['b']);
  });

  it('lists compatible candidates before incompatible ones', () => {
    const base = file('a', '3.1.0', '1.0');
    const candidates = service.candidatesFor(base, [
      base,
      file('mismatch', '2.0.0', '1.0'),
      file('match', '3.1.1', '1.0'),
    ]);
    expect(candidates[0].file.id).toBe('match');
    expect(candidates[0].compatible).toBe(true);
    expect(candidates[1].compatible).toBe(false);
  });
});

describe('AdfComparisonService extract filtering', () => {
  let service: AdfComparisonService;

  const meta = (extract: any): any => ({
    summary: { counts: {}, extract, countByAgeGroup: [], outOfRange: 0 },
  });

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(AdfComparisonService);
  });

  it('treats an element with identical percentages as unchanged', () => {
    const items = service.compareExtract(
      meta({ PID5: { valued: 90, empty: 10 } }),
      meta({ PID5: { valued: 90, empty: 10 } }),
    );
    expect(service.extractHasChange(items[0])).toBe(false);
  });

  it('treats any differing column as a change', () => {
    const items = service.compareExtract(
      meta({ PID5: { valued: 90, empty: 10 } }),
      meta({ PID5: { valued: 90, empty: 11 } }),
    );
    expect(service.extractHasChange(items[0])).toBe(true);
  });

  it('treats an element present on only one side as a change', () => {
    const items = service.compareExtract(
      meta({ PID5: { valued: 90 } }),
      meta({ PID5: { valued: 90 }, PID7: { valued: 50 } }),
    );
    expect(service.extractHasChange(items.find((i) => i.label === 'PID7'))).toBe(true);
  });

  it('filters out only the identical elements', () => {
    const items = service.compareExtract(
      meta({ same: { valued: 90, empty: 10 }, diff: { valued: 80, empty: 20 } }),
      meta({ same: { valued: 90, empty: 10 }, diff: { valued: 85, empty: 15 } }),
    );
    const changed = service.changedExtractOnly(items);
    expect(changed.map((i) => i.label)).toEqual(['diff']);
  });

  it('returns an empty list when nothing changed', () => {
    const items = service.compareExtract(
      meta({ a: { valued: 1 }, b: { valued: 2 } }),
      meta({ a: { valued: 1 }, b: { valued: 2 } }),
    );
    expect(service.changedExtractOnly(items).length).toBe(0);
  });
});
