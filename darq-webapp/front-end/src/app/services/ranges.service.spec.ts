import { TestBed, inject } from '@angular/core/testing';

import { RangesService } from './ranges.service';

describe('RangesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RangesService]
    });
  });

  it('should be created', inject([RangesService], (service: RangesService) => {
    expect(service).toBeTruthy();
  }));
});
