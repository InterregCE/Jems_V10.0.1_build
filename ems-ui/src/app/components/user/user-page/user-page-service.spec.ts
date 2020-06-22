import {TestBed} from '@angular/core/testing';

import {UserPageService} from './user-page.service';

describe('UserPageService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UserPageService = TestBed.get(UserPageService);
    expect(service).toBeTruthy();
  });
});
