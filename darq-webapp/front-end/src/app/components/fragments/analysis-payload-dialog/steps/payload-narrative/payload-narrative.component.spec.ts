import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PayloadNarativeComponent } from './payload-narative.component';

describe('PayloadNarativeComponent', () => {
  let component: PayloadNarativeComponent;
  let fixture: ComponentFixture<PayloadNarativeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PayloadNarativeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PayloadNarativeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
