import { TestBed, inject } from '@angular/core/testing';

import { AdfService } from './adf.service';

describe('AdfService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AdfService]
    });
  });

  it('should be created', inject([AdfService], (service: AdfService) => {
    expect(service).toBeTruthy();
  }));
});
