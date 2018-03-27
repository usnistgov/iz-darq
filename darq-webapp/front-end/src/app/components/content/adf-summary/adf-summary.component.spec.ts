import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdfSummaryComponent } from './adf-summary.component';

describe('AdfSummaryComponent', () => {
  let component: AdfSummaryComponent;
  let fixture: ComponentFixture<AdfSummaryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdfSummaryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdfSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
