import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TableConfiguration} from './model/table.configuration';
import {DatePipe} from '@angular/common';
import {ColumnConfiguration} from './model/column.configuration';
import {ColumnType} from './model/column-type.enum';
import {Observable} from 'rxjs';
import {Tools} from '../../utils/tools';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../utils/tables';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {
  @Input()
  configuration: TableConfiguration;
  @Input()
  rows: Observable<any[]> | any[];
  @Output()
  sortRows = new EventEmitter<Partial<MatSort>>();

  columnsToDisplay: string[] = [];

  constructor(private datepipe: DatePipe,) {
  }

  ngOnInit(): void {
    this.columnsToDisplay = this.configuration.columns.map(col => col.displayedColumn);
  }

  formatColumnValue(column: ColumnConfiguration, element: any): any {
    if (!column.elementProperty) {
      return element;
    }
    const elementValue = Tools.getChainedProperty(element, column.elementProperty, '');
    if (column.columnType === ColumnType.Date) {
      return this.datepipe.transform(elementValue, Tables.DEFAULT_DATE_FORMAT);
    }
    return elementValue;
  }
}
