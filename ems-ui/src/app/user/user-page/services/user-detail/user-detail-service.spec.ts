import {TestBed} from '@angular/core/testing';
import {UserModule} from '../../../user.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {UserDetailService} from './user-detail.service';

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
    expect(service.userSavedEvent).toBeTruthy();
  });
});

