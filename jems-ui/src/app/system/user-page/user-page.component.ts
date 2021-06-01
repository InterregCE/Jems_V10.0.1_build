import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {SystemPageSidenavService} from '../services/system-page-sidenav.service';
import {RolePageService} from '../role-page/role-page.service';
import {UserPageStore} from './user-page-store.service';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss'],
  providers: [UserPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPageComponent implements OnInit {

  PermissionsEnum = PermissionsEnum;
  success = this.router.getCurrentNavigation()?.extras?.state?.success;
  Alert = Alert;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/app/system/user/detail',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'user.table.column.name.id',
        elementProperty: 'id',
        sortProperty: 'id'
      },
      {
        displayedColumn: 'user.table.column.name.name',
        elementProperty: 'name',
        sortProperty: 'name'
      },
      {
        displayedColumn: 'user.table.column.name.surname',
        elementProperty: 'surname',
        sortProperty: 'surname'
      },
      {
        displayedColumn: 'user.table.column.name.email',
        elementProperty: 'email',
        sortProperty: 'email'
      },
      {
        displayedColumn: 'user.table.column.name.role',
        elementProperty: 'userRole.name',
        sortProperty: 'userRole.name'
      }
    ]
  });

  constructor(public userPageStore: UserPageStore,
              private rolePageService: RolePageService,
              private router: Router,
              private changeDetectorRef: ChangeDetectorRef,
              private systemPageSidenavService: SystemPageSidenavService) {
  }

  ngOnInit(): void {
    if (this.success) {
      setTimeout(() => {
        this.success = null;
        this.changeDetectorRef.markForCheck();
      },         3000);
    }
  }
}
