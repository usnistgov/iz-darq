import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CompletionResultComponent } from './completion-result.component';

describe('CompletionResultComponent', () => {
  let component: CompletionResultComponent;
  let fixture: ComponentFixture<CompletionResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CompletionResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompletionResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
