import {MatTableDataSource} from '@angular/material/table';
import {ActionConfiguration} from './action.configuration';

export class TableConfiguration {
  // the name of the columns that will appear in the header of the table.
  displayedColumns: string[];
  // the name of the properties of one object in the datasource that will be binded to the cells of the table.
  elementProperties: string[];
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
}
