import { TestBed, inject } from '@angular/core/testing';

import { ConfiguationService } from './configuation.service';

describe('ConfiguationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ConfiguationService]
    });
  });

  it('should be created', inject([ConfiguationService], (service: ConfiguationService) => {
    expect(service).toBeTruthy();
  }));
});
