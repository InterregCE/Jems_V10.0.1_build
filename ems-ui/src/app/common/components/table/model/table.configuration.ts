import {ActionConfiguration} from './action.configuration';
import {ColumnConfiguration} from './column.configuration';
import {MatTableDataSource} from '@angular/material/table';

export class TableConfiguration {
  // configurtions of columns
  columns: ColumnConfiguration[];
  // boolean that defines if a table row has a click action.
  isTableClickable: boolean;
  // table data source.
  dataSource: MatTableDataSource<any>;
  // boolean that defines if a table will have a column with icons for doing actions on the table row inside the table(edit, delete and so on)
  actionColumn?: boolean;
  // If the table row has a click action, this link is used by the router to determine what page should be shown.
  routerLink?: string;
  // Defines a series of action configs. Each of them will have an icon and a lambda function specified.
  actions?: ActionConfiguration[];

  public constructor(config?: Partial<TableConfiguration>) {
    Object.assign(this, config);
  }
}
