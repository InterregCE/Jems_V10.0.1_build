import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {OutputUser} from '@cat/api';
import {TableConfiguration} from '../../../general/configurations/table.configuration';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserListComponent {

  @Input() filtered: OutputUser[];
  tableConfiguration: TableConfiguration = new TableConfiguration({
    columns: [
      {
        displayedColumn: 'user.table.column.name.id',
        elementProperty: 'id',
      },
      {
        displayedColumn: 'user.table.column.name.name',
        elementProperty: 'email',
        i18nHeader: 'user.table.custom.name.',
      },
      {
        displayedColumn: 'user.table.column.name.surname',
        elementProperty: '',
      },
      {
        displayedColumn: 'user.table.column.name.email',
        elementProperty: 'email',
      },
      {
        displayedColumn: 'user.table.column.name.role',
        i18nHeader: 'user.role.',
        elementProperty: 'userRole.name'
      }
    ]
  });
}
