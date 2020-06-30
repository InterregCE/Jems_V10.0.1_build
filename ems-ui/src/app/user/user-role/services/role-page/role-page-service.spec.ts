import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import { RolePageService } from './role-page.service';
import {UserModule} from '../../../user.module';
import {TestModule} from '../../../../common/test-module';
import {OutputUserRole} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';

describe('RolePageService', () => {
  let service: RolePageService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        UserModule,
        TestModule
      ]
    });
    service = TestBed.inject(RolePageService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should list user roles', fakeAsync(() => {
    let results: OutputUserRole[] = [];
    service.userRoles().subscribe(result => results = result);

    const roles = [
      {name: 'role1'} as OutputUserRole,
      {name: '2@role1'} as OutputUserRole
    ];
    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/role`
    }).flush({content: roles});
    httpTestingController.verify();

    tick();
    expect(results).toEqual(roles);
  }));
});
