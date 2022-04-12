import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TopBarService} from '@common/components/top-bar/top-bar.service';
import {TestModule} from '../../test-module';
import {MenuItemConfiguration} from '@common/components/top-bar/menu-item.configuration';
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

});
