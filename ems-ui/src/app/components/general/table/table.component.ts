import {Component, Input, OnInit} from '@angular/core';
import {TableConfiguration} from '../configurations/table.configuration';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {
  @Input()
  configuration: TableConfiguration;

  columnsToDisplay: string[] = [];

  ngOnInit() {
    this.configuration.displayedColumns.forEach((column: string) => {
      this.columnsToDisplay.push(column);
    });
    if (this.configuration.actionColumn) {
      this.columnsToDisplay.push('Actions');
    }
  }
}
