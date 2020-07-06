import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {PageOutputUser} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserListComponent {

  @Input() userPage: PageOutputUser;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/user/',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'user.table.column.name.id',
        elementProperty: 'id',
      },
      {
        displayedColumn: 'user.table.column.name.name',
        elementProperty: 'name',
      },
      {
        displayedColumn: 'user.table.column.name.surname',
        elementProperty: 'surname',
      },
      {
        displayedColumn: 'user.table.column.name.email',
        elementProperty: 'email',
      },
      {
        displayedColumn: 'user.table.column.name.role',
        elementProperty: 'userRole.name'
      }
    ]
  });
}
