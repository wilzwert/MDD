import { TestBed } from '@angular/core/testing';

import { SessionService } from './session.service';
import { CacheService } from './cache.service';

describe('SessionService', () => {
  let service: SessionService;
  let mockCacheService: jest.Mocked<CacheService>;

  beforeEach(() => {
    mockCacheService = {
      clearCache: jest.fn()
    } as unknown as jest.Mocked<CacheService>;

    TestBed.configureTestingModule({
      providers: [
        {provide: CacheService, useValue: mockCacheService}
      ]
    });
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
