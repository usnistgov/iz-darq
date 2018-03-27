import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralResultComponent } from './general-result.component';

describe('GeneralResultComponent', () => {
  let component: GeneralResultComponent;
  let fixture: ComponentFixture<GeneralResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GeneralResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
