import {ColumnConfiguration} from './column.configuration';

export class TableConfiguration {
  // configurations of columns
  columns: ColumnConfiguration[];
  // boolean that defines if a table row has a click action.
  isTableClickable: boolean;
  sortable? = true;
  // If the table row has a click action, this link is used by the router to determine what page should be shown.
  routerLink?: string;

  public constructor(config?: Partial<TableConfiguration>) {
    Object.assign(this, config);
  }
}
