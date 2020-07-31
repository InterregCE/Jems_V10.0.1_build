import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {MatSort} from '@angular/material/sort';
import {PageOutputCall} from '@cat/api'
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'app-call-list',
  templateUrl: './call-list.component.html',
  styleUrls: ['./call-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallListComponent {
  Alert = Alert;

  @Input()
  publishedCall: string;
  @Input()
  callPage: PageOutputCall;
  @Input()
  pageIndex: number;

  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/call',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'Id',
        elementProperty: 'id',
        sortProperty: 'id'
      },
      {
        displayedColumn: 'Name',
        elementProperty: 'name',
        sortProperty: 'name',
      },
      {
        displayedColumn: 'Status',
        elementProperty: 'status',
        sortProperty: 'status'
      },
      {
        displayedColumn: 'Started',
        columnType: ColumnType.Date,
        elementProperty: 'startDate',
        sortProperty: 'startDate'
      },
      {
        displayedColumn: 'Ends',
        columnType: ColumnType.Date,
        elementProperty: 'endDate',
        sortProperty: 'endDate'
      }
    ]
  });

}
