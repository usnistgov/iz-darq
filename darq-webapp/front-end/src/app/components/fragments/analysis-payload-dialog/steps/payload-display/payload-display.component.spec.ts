import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PayloadDisplayComponent } from './payload-display.component';

describe('PayloadDisplayComponent', () => {
  let component: PayloadDisplayComponent;
  let fixture: ComponentFixture<PayloadDisplayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PayloadDisplayComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PayloadDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
