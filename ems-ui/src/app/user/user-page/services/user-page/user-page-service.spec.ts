import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {InputUser} from '@cat/api';
import {UserPageService} from './user-page.service';
import {UserModule} from '../../../user.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';

describe('UserPageService', () => {
  let httpTestingController: HttpTestingController;

  beforeEach(() => TestBed.configureTestingModule({
    imports: [UserModule, TestModule],
  }));

  it('should be created', () => {
    const service: UserPageService = TestBed.get(UserPageService);
    expect(service).toBeTruthy();
  });

  it('should save a user', fakeAsync(() => {
    httpTestingController = TestBed.get(HttpTestingController);
    const service: UserPageService = TestBed.get(UserPageService);
    let success = false;
    const user = {
      name: 'test',
      surname: 'test',
      email: 'test@test.com',
      accountRoleId: 1
    } as InputUser;

    service.saveUser(user);
    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/user`
    }).flush(user);
    httpTestingController.verify();
    service.saveSuccess().subscribe(result => {
      success = result;
    })
    tick();
    expect(success).toBeTruthy();
  }));
});

