import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {OutputUser} from '@cat/api';
import {UserPageService} from './user-page.service';
import {UserModule} from '../../../user.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';

describe('UserPageService', () => {
  let service: UserPageService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserModule, TestModule],
    });
    service = TestBed.inject(UserPageService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should list users', fakeAsync(() => {
    let results: OutputUser[] = [];
    service.userList().subscribe(result => results = result);

    const users = [
      {email: '1@1'} as OutputUser,
      {email: '2@2'} as OutputUser
    ];
    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/user?page=0&size=100&sort=id,desc`
    }).flush({content: users});
    httpTestingController.verify();

    tick();
    expect(results).toEqual(users);
  }));
});

