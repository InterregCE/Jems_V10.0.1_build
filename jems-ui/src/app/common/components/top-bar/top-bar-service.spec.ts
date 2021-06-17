import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TopBarService} from '@common/components/top-bar/top-bar.service';
import {TestModule} from '../../test-module';
import {MenuItemConfiguration} from '@common/components/menu/model/menu-item.configuration';
import {Permission} from '../../../security/permissions/permission';
import {SecurityService} from '../../../security/security.service';
import {RouterTestingModule} from '@angular/router/testing';
import {UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

describe('TopBarService', () => {
  let service: TopBarService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1', component: TopBarService}])
      ],
      providers: [
        {
          provide: TopBarService,
          useClass: TopBarService
        },
      ]
    });
    service = TestBed.inject(TopBarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create menus depending on rights', fakeAsync(() => {
    const securityService: SecurityService = TestBed.inject(SecurityService);

    let menuItems: MenuItemConfiguration[] = [];
    service.menuItems$
      .subscribe((items: MenuItemConfiguration[]) => menuItems = items);

    (securityService as any).myCurrentUser.next({
      name: 'user',
      role: {
        name: Permission.ADMINISTRATOR, permissions: [
          PermissionsEnum.ProjectRetrieve,
          PermissionsEnum.AuditRetrieve,
          ...Permission.PROGRAMME_SETUP_MODULE_PERMISSIONS,
          ...Permission.SYSTEM_MODULE_PERMISSIONS,
          PermissionsEnum.CallRetrieve,
        ],
      }
    });
    tick();
    expect(menuItems.length).toBe(5);
    expect(menuItems[0].name).toBe('topbar.main.project');
    expect(menuItems[1].name).toBe('topbar.main.call');
    expect(menuItems[2].name).toBe('topbar.main.programme');
    expect(menuItems[3].name).toBe('topbar.main.system');
    expect(menuItems[4].name).toBe('user (administrator)');


    (securityService as any).myCurrentUser.next({name: 'user', role: {name: Permission.APPLICANT_USER, permissions: []}});
    tick();
    expect(menuItems.length).toBe(2);
    expect(menuItems[0].name).toBe('topbar.main.dashboard');
    expect(menuItems[1].name).toBe('user (applicant user)');
  }));
});
