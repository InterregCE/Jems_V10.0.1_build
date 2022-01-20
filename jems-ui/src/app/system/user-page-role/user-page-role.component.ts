import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {UserPageRoleStore} from './user-page-role-store.service';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Router} from '@angular/router';
import {SystemPageSidenavService} from '../services/system-page-sidenav.service';
import {UserRoleDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-user-page-role',
  templateUrl: './user-page-role.component.html',
  styleUrls: ['./user-page-role.component.scss'],
  providers: [UserPageRoleStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPageRoleComponent implements OnInit {

  PermissionsEnum = PermissionsEnum;
  Alert = Alert;
  success = this.router.getCurrentNavigation()?.extras?.state?.success;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/app/system/role/detail',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'userRole.table.column.name.id',
        elementProperty: 'id',
        sortProperty: 'id'
      },
      {
        displayedColumn: 'userRole.table.column.name.name',
        elementProperty: 'name',
        sortProperty: 'name'
      },
    ]
  });

  constructor(
    public roleStore: UserPageRoleStore,
    private router: Router,
    private systemPageSidenavService: SystemPageSidenavService,
  ) {
  }

  ngOnInit(): void {
    if (this.success) {
      setTimeout(
        () => {
          this.success = null;
        },
        3000);
    }
  }
}
