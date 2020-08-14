import {ColumnType} from './column-type.enum';
import {TemplateRef} from '@angular/core';

export class ColumnConfiguration {
  // the name of the column that will appear in the header of the table.
  displayedColumn: string;
  // the name of the property of one object in the datasource that will be binded to the cells of the table.
  elementProperty?: string;
  // main translation key for that element
  elementTranslationKey?: string;
  // if present the table will be sorted by the given property
  sortProperty?: string
  // type of the column
  columnType?: ColumnType;
  // the custom template
  customCellTemplate?: TemplateRef<any>

  public constructor(init?: Partial<ColumnConfiguration>) {
    Object.assign(this, init);
  }
}
