import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TopBarService} from '@common/components/top-bar/top-bar.service';
import {TestModule} from '../../test-module';
import {PermissionService} from '../../../security/permissions/permission.service';
import {MenuItemConfiguration} from '@common/components/menu/model/menu-item.configuration';
import {Permission} from '../../../security/permissions/permission';

describe('TopBarService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [TestModule],
    providers: [
      {
        provide: TopBarService,
        useClass: TopBarService
      },
    ]
  }));

  it('should be created', () => {
    const service: TopBarService = TestBed.inject(TopBarService);
    expect(service).toBeTruthy();
  });

  it('should create menus depending on rights', fakeAsync(() => {
    const service: TopBarService = TestBed.inject(TopBarService);
    const permissionService: PermissionService = TestBed.inject(PermissionService);

    let menuItems: MenuItemConfiguration[] = [];
    service.menuItems()
      .subscribe((items: MenuItemConfiguration[]) => menuItems = items);

    permissionService.setPermissions([Permission.PROGRAMME_USER]);
    tick();
    expect(menuItems.length).toBe(1);
    expect(menuItems[0].name).toBe('Project Applications');

    permissionService.setPermissions([Permission.ADMINISTRATOR]);
    tick();
    expect(menuItems.length).toBe(3);
  }));
});
