import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportResultSectionComponent } from './report-result-section.component';

describe('ReportResultSectionComponent', () => {
  let component: ReportResultSectionComponent;
  let fixture: ComponentFixture<ReportResultSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportResultSectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportResultSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
