import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Router} from '@angular/router';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {CallDTO, PageCallDTO} from '@cat/api';
import moment from 'moment/moment';
import {CallListStore} from '@common/components/call-list/call-list-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-call-list',
  templateUrl: './call-list.component.html',
  styleUrls: ['./call-list.component.scss'],
  providers: [CallListStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallListComponent implements OnInit {
  @ViewChild('callActionsCell', {static: true})
  actionsCell: TemplateRef<any>;
  @ViewChild('endDateCell', {static: true})
  endDateCell: TemplateRef<any>;

  @Input()
  publishedCallsOnly: boolean;

  data$: Observable<{
    page: PageCallDTO,
    tableConfiguration: TableConfiguration
  }>;

  constructor(private router: Router,
              public listStore: CallListStore) {
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.publishedCallsOnly ? this.listStore.publishedCallPage$ : this.listStore.callPage$,
      this.listStore.canApply$
    ])
      .pipe(
        map(([page, canApply]) => ({page, tableConfiguration: this.getTableConfig(canApply)}))
      );
  }

  applyToCall(callId: number): void {
    this.router.navigate(['/app/project/applyTo/' + callId]);
  }

  isOpen(call: CallDTO): boolean {
    const currentDate = moment(new Date());
    const endDateTime = call.endDateTimeStep1 || call.endDateTime;
    return currentDate.isBefore(endDateTime) && currentDate.isAfter(call.startDateTime);
  }

  private getTableConfig(canApply: boolean): TableConfiguration {
    const config = new TableConfiguration({
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
          elementProperty: 'startDateTime',
          sortProperty: 'startDate'
        },
        {
          displayedColumn: 'call.table.column.name.end',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.endDateCell
        }
      ]
    });
    if (this.publishedCallsOnly && canApply) {
      config.columns.push({
        displayedColumn: 'call.table.column.name.action',
        customCellTemplate: this.actionsCell
      });
    }
    return config;
  }

}
