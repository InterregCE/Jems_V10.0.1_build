import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {
  @Input()
  configuration: any;

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
