import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {UserModule} from '../../../user.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {UserDetailService} from './user-detail.service';
import {InputUserCreate} from '@cat/api';

describe('UserDetailService', () => {
  let httpTestingController: HttpTestingController;
  let service: UserDetailService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        UserModule,
        TestModule
      ]
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(UserDetailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a user', fakeAsync(() => {
    const user = {email: 'test@test.com'} as InputUserCreate;
    service.createUser(user);

    let success = false;
    service.saveSuccess().subscribe(result => success = result);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/user`
    }).flush(user);
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));
});

