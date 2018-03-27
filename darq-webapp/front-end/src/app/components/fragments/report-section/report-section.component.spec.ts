import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportSectionComponent } from './report-section.component';

describe('ReportSectionComponent', () => {
  let component: ReportSectionComponent;
  let fixture: ComponentFixture<ReportSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportSectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
