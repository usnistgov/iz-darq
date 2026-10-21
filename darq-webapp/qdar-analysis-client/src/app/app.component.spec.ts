import {async, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {AppComponent} from './app.component';
import {ServerInfoService} from './modules/core/services/app-info.service';

describe('AppComponent', () => {
  const serverInfoStub = {
    getServerInfo: () => of({}),
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: ServerInfoService, useValue: serverInfoStub },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should expose the server info on init', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit();
    expect(app.info).toBeDefined();
  });
});
