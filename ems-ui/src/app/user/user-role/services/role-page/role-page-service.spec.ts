import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import {RolePageService} from './role-page.service';
import {UserModule} from '../../../user.module';
import {TestModule} from '../../../../common/test-module';
import {OutputUserRole} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {Permission} from '../../../../security/permissions/permission';

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

  it('should list empty user roles for regular user', fakeAsync(() => {
    let results: OutputUserRole[] = [];
    service.userRoles().subscribe(result => results = result);

    tick();

    expect(results).toEqual([]);
  }));

  it('should list user roles for admin', fakeAsync(() => {
    const permissionService = TestBed.inject(PermissionService);
    permissionService.setPermissions([Permission.ADMINISTRATOR]);
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
