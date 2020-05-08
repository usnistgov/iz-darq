import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalysisPayloadDialogComponent } from './analysis-payload-dialog.component';

describe('AnalysisPayloadDialogComponent', () => {
  let component: AnalysisPayloadDialogComponent;
  let fixture: ComponentFixture<AnalysisPayloadDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnalysisPayloadDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalysisPayloadDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
