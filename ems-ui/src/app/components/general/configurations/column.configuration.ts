import {ComponentType} from '@angular/cdk/overlay';
import {ColumnType} from '../enums/column-type.enum';

export class ColumnConfiguration {
  // the name of the column that will appear in the header of the table.
  displayedColumn: string;
  // the name of the property of one object in the datasource that will be binded to the cells of the table.
  elementProperty: string;
  // type of the column
  columnType?: ColumnType;
  // if the column type is CustomComponent, this property defines the name of the component to be used
  component?: ComponentType<any>;
  // extra properties
  extraProps?: any;

  public constructor(init?: Partial<ColumnConfiguration>) {
    Object.assign(this, init);
  }
}
