import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {provideMockStore} from '@ngrx/store/testing';

import {AdfComparisonSummaryDialogComponent} from './adf-comparison-summary-dialog.component';
import {AdfComparisonSummaryComponent} from '../adf-comparison-summary/adf-comparison-summary.component';

describe('AdfComparisonSummaryDialogComponent', () => {
  let fixture: ComponentFixture<AdfComparisonSummaryDialogComponent>;

  const meta = (name: string, totalRead: number): any => ({
    id: name,
    name,
    summary: {
      counts: { totalReadPatientRecords: totalRead },
      extract: {},
      countByAgeGroup: [],
      outOfRange: 0,
    },
  });

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [AdfComparisonSummaryDialogComponent, AdfComparisonSummaryComponent],
      providers: [
        provideMockStore({ initialState: {} }),
        { provide: MatDialogRef, useValue: { close: () => {} } },
        { provide: MAT_DIALOG_DATA, useValue: { baseMeta: meta('base', 100), newMeta: meta('new', 150) } },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdfComparisonSummaryDialogComponent);
  });

  it('passes both metadata payloads through to the summary', () => {
    fixture.detectChanges();
    const summary = fixture.debugElement.nativeElement.querySelector('app-adf-comparison-summary');
    expect(summary).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('General Counts');
  });
});
