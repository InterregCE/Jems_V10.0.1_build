import {ColumnType} from './column-type.enum';
import {TemplateRef} from '@angular/core';
import {TooltipConfiguration} from '@common/components/table/model/tooltip.configuration';

export class ColumnConfiguration {
  // the name of the column that will appear in the header of the table.
  displayedColumn: string;
  // the name of the property of one object in the datasource that will be binded to the cells of the table.
  elementProperty?: string;
  // main translation key for that element
  elementTranslationKey?: string;
  // fixed i18n translation key to use with argument
  i18nFixedKey?: string;
  // args object for translation keys
  i18nArgs?: (element: any) => any;
  // optional function as condition to replace element value
  alternativeValueCondition?: (element: any) => {};
  // optional value if condition is set and applies
  alternativeValue?: string;
  // if present the table will be sorted by the given property
  sortProperty?: string;
  // type of the column
  columnType?: ColumnType;
  // the custom template
  customCellTemplate?: TemplateRef<any>;
  // the cell tooltip
  tooltip?: TooltipConfiguration;

  public constructor(init?: Partial<ColumnConfiguration>) {
    Object.assign(this, init);
  }
}
