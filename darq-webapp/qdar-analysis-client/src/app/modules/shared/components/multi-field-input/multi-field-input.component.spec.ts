import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiFieldInputComponent } from './multi-field-input.component';

describe('MultiFieldInputComponent', () => {
  let component: MultiFieldInputComponent;
  let fixture: ComponentFixture<MultiFieldInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MultiFieldInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiFieldInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
