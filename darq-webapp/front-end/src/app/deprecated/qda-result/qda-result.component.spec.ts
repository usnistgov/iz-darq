import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QdaResultComponent } from './qda-result.component';

describe('QdaResultComponent', () => {
  let component: QdaResultComponent;
  let fixture: ComponentFixture<QdaResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QdaResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QdaResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
