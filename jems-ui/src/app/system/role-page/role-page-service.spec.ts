import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import {RolePageService} from './role-page.service';
import {UserRoleDTO, UserRoleSummaryDTO} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {SystemModule} from '../system.module';
import {TestModule} from '../../common/test-module';
import {PermissionService} from '../../security/permissions/permission.service';

describe('RolePageService', () => {
  let service: RolePageService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        SystemModule,
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
    let results: UserRoleSummaryDTO[] = [];
    service.userRoles().subscribe(result => results = result);

    tick();

    expect(results).toEqual([]);
  }));

  it('should list user roles for admin', fakeAsync(() => {
    const permissionService = TestBed.inject(PermissionService);
    const role: UserRoleDTO = {id: 0, name: 'administrator', permissions: []} as UserRoleDTO;
    permissionService.setPermissions([role]);
    let results: UserRoleSummaryDTO[] = [];
    service.userRoles().subscribe(result => results = result);

    httpTestingController.match({method: 'GET', url: `//api/auth/current`});
    const roles = [
      {name: 'role1'} as UserRoleSummaryDTO,
      {name: '2@role1'} as UserRoleSummaryDTO
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
