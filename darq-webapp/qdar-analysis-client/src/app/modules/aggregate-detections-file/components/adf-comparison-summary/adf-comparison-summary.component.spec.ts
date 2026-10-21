import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {provideMockStore} from '@ngrx/store/testing';

import {AdfComparisonSummaryComponent} from './adf-comparison-summary.component';

describe('AdfComparisonSummaryComponent', () => {
  let component: AdfComparisonSummaryComponent;
  let fixture: ComponentFixture<AdfComparisonSummaryComponent>;

  const meta = (name: string, totalRead: number): any => ({
    id: name,
    name,
    summary: {
      counts: { totalReadPatientRecords: totalRead },
      extract: { PID5: { valued: 90, empty: 10 } },
      countByAgeGroup: [],
      outOfRange: 0,
    },
  });

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [AdfComparisonSummaryComponent],
      providers: [provideMockStore({ initialState: {} })],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdfComparisonSummaryComponent);
    component = fixture.componentInstance;
  });

  it('builds the comparison once both metadata inputs are set', () => {
    component.baseMeta = meta('base', 100);
    component.newMeta = meta('new', 150);
    expect(component.comparison).toBeDefined();
    expect(component.comparison.counts[0].delta).toBe(50);
  });

  it('renders the comparison content into the DOM', () => {
    component.baseMeta = meta('base', 100);
    component.newMeta = meta('new', 150);
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('General Counts');
    expect(text).toContain('Total Read Patient Records');
  });

  it('explains itself instead of rendering blank when a summary is missing', () => {
    component.baseMeta = meta('base', 100);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('General Counts');
    expect(fixture.nativeElement.textContent).toContain('Waiting for both ADF summaries');
  });

  it('explains itself when both ADFs carry no summary data', () => {
    component.baseMeta = { id: 'a', name: 'a' } as any;
    component.newMeta = { id: 'b', name: 'b' } as any;
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('no summary data');
  });
});
