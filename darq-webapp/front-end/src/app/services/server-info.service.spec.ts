import { TestBed, inject } from '@angular/core/testing';

import { ServerInfoService } from './server-info.service';

describe('ServerInfoService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ServerInfoService]
    });
  });

  it('should be created', inject([ServerInfoService], (service: ServerInfoService) => {
    expect(service).toBeTruthy();
  }));
});
