import {TestBed} from '@angular/core/testing';

import {UserPageService} from './user-page.service';
import {UserModule} from '../../../user.module';

describe('UserPageService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [UserModule],
  }));

  it('should be created', () => {
    const service: UserPageService = TestBed.get(UserPageService);
    expect(service).toBeTruthy();
  });
});
