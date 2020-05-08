import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExtractConfigurationComponent } from './extract-configuration.component';

describe('ExtractConfigurationComponent', () => {
  let component: ExtractConfigurationComponent;
  let fixture: ComponentFixture<ExtractConfigurationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExtractConfigurationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExtractConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
