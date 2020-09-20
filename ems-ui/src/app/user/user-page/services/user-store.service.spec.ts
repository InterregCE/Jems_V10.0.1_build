import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import {UserStore} from './user-store.service';
import {HttpTestingController} from '@angular/common/http/testing';
import {InputUserUpdate} from '@cat/api';
import {UserModule} from '../../user.module';
import {TestModule} from '../../../common/test-module';

describe('UserStoreService', () => {
  let service: UserStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserModule, TestModule]
    });
    service = TestBed.inject(UserStore);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update a user', fakeAsync(() => {
    const user = {email: 'test@test.com'} as InputUserUpdate;
    service.getUser().subscribe();
    service.init(1);

    service.saveUser$.next(user);
    let success = false;
    service.userSaveSuccess$.subscribe((result: boolean) => success = result);

    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/user/1`
    }).flush(user);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/user`
    }).flush(user);
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));
});
