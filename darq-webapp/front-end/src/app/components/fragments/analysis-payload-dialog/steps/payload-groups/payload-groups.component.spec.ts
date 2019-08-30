import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PayloadGroupsComponent } from './payload-groups.component';

describe('PayloadGroupsComponent', () => {
  let component: PayloadGroupsComponent;
  let fixture: ComponentFixture<PayloadGroupsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PayloadGroupsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PayloadGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
