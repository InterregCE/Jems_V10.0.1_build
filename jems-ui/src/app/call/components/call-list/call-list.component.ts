import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {MatSort} from '@angular/material/sort';
import {OutputCall, PageOutputCallList} from '@cat/api';
import {Router} from '@angular/router';
import * as moment from 'moment';

@Component({
  selector: 'app-call-list',
  templateUrl: './call-list.component.html',
  styleUrls: ['./call-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallListComponent implements OnInit {
  @ViewChild('callActionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  @Input()
  callPage: PageOutputCallList;
  @Input()
  pageIndex: number;
  @Input()
  isApplicant: boolean;

  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  tableConfiguration: TableConfiguration;


  constructor(private router: Router) {
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: '/app/call/detail',
      isTableClickable: true,
      columns: [
        {
          displayedColumn: 'call.table.column.name.id',
          elementProperty: 'id',
          sortProperty: 'id'
        },
        {
          displayedColumn: 'call.table.column.name.name',
          elementProperty: 'name',
          sortProperty: 'name',
        },
        {
          displayedColumn: 'call.table.column.name.status',
          elementProperty: 'status',
          elementTranslationKey: 'common.label.callstatus',
          sortProperty: 'status'
        },
        {
          displayedColumn: 'call.table.column.name.started',
          columnType: ColumnType.DateColumn,
          elementProperty: 'startDate',
          sortProperty: 'startDate'
        },
        {
          displayedColumn: 'call.table.column.name.end',
          columnType: ColumnType.DateColumn,
          elementProperty: 'endDate',
          sortProperty: 'endDate'
        }
      ]
    });
    if (this.isApplicant) {
      this.tableConfiguration.columns.push({
        displayedColumn: 'call.table.column.name.action',
        customCellTemplate: this.actionsCell
      });
    }
  }

  applyToCall(callId: number): void {
    this.router.navigate(['/app/project/applyTo/' + callId]);
  }

  isOpen(call: OutputCall): boolean {
    const currentDate = moment(new Date());
    return currentDate.isBefore(call.endDate) && currentDate.isAfter(call.startDate);
  }
}
