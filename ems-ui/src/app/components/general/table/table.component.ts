import {Component, Input, OnInit} from '@angular/core';
import {TableConfiguration} from '../configurations/table.configuration';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {
  @Input()
  configuration: TableConfiguration;

  columnsToDisplay: string[] = [];

  constructor(private datepipe: DatePipe) {
  }

  ngOnInit() {
    this.configuration.displayedColumns.forEach((column: string) => {
      this.columnsToDisplay.push(column);
    });
    if (this.configuration.actionColumn) {
      this.columnsToDisplay.push('Actions');
    }
  }

  formatColumnValue(column: string, element: any): any {
    if (column === 'Timestamp') {
      return this.datepipe.transform(element, 'yyyy-MM-dd HH:mm:ss');
    } else {
      return element;
    }
  }
}
