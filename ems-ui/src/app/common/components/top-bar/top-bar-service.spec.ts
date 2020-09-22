import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TopBarService} from '@common/components/top-bar/top-bar.service';
import {TestModule} from '../../test-module';
import {PermissionService} from '../../../security/permissions/permission.service';
import {MenuItemConfiguration} from '@common/components/menu/model/menu-item.configuration';
import {Permission} from '../../../security/permissions/permission';
import {SecurityService} from '../../../security/security.service';
import {HttpTestingController} from '@angular/common/http/testing';

describe('TopBarService', () => {
  let httpTestingController: HttpTestingController;
  let service: TopBarService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [
        {
          provide: TopBarService,
          useClass: TopBarService
        },
      ]
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(TopBarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create menus depending on rights', fakeAsync(() => {
    const permissionService: PermissionService = TestBed.inject(PermissionService);
    const securityService: SecurityService = TestBed.inject(SecurityService);
    securityService.reloadCurrentUser().subscribe();
    service.newAuditUrl('auditUrl');

    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/auth/current`
    }).flush({id: 1, name: 'user', role: 'role'});

    let menuItems: MenuItemConfiguration[] = [];
    service.menuItems()
      .subscribe((items: MenuItemConfiguration[]) => menuItems = items);

    permissionService.setPermissions([Permission.ADMINISTRATOR]);
    tick();
    expect(menuItems.length).toBe(6);
    expect(menuItems[0].name).toBe('topbar.main.project')
    expect(menuItems[1].name).toBe('topbar.main.call')
    expect(menuItems[2].name).toBe('topbar.main.programme')
    expect(menuItems[3].name).toBe('topbar.main.user.management')
    expect(menuItems[4].name).toBe('topbar.main.audit')
    expect(menuItems[5].name).toBe('user (role)')

    permissionService.setPermissions([Permission.PROGRAMME_USER]);
    tick();
    expect(menuItems.length).toBe(5);
    expect(menuItems[0].name).toBe('topbar.main.project')
    expect(menuItems[1].name).toBe('topbar.main.call')
    expect(menuItems[2].name).toBe('topbar.main.programme')
    expect(menuItems[3].name).toBe('topbar.main.audit')
    expect(menuItems[4].name).toBe('user (role)')

    permissionService.setPermissions([Permission.APPLICANT_USER]);
    tick();
    expect(menuItems.length).toBe(3);
  }));
});
