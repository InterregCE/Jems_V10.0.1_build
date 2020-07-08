import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {PageOutputUser} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../../common/utils/tables';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserListComponent {
  Tables = Tables;

  @Input()
  userPage: PageOutputUser;
  @Output()
  newPage: EventEmitter<PageEvent> = new EventEmitter<PageEvent>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/user/',
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
}
