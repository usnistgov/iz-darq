import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PayloadFilterComponent } from './payload-filter.component';

describe('PayloadFilterComponent', () => {
  let component: PayloadFilterComponent;
  let fixture: ComponentFixture<PayloadFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PayloadFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PayloadFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
