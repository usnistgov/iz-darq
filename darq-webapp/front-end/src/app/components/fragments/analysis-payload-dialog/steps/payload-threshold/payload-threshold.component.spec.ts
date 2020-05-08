import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PayloadThresholdComponent } from './payload-threshold.component';

describe('PayloadThresholdComponent', () => {
  let component: PayloadThresholdComponent;
  let fixture: ComponentFixture<PayloadThresholdComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PayloadThresholdComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PayloadThresholdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
