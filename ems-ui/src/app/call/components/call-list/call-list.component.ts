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
import {PageOutputCall} from '@cat/api'
import {Alert} from '@common/components/forms/alert';
import {Permission} from '../../../security/permissions/permission';
import {Router} from '@angular/router';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-call-list',
  templateUrl: './call-list.component.html',
  styleUrls: ['./call-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallListComponent extends BaseComponent implements OnInit {
  @ViewChild('callActionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  Alert = Alert;
  Permission = Permission;

  @Input()
  publishedCall: string;
  @Input()
  callPage: PageOutputCall;
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

  tableConfigurationProgramme: TableConfiguration;
  tableConfigurationApplicant: TableConfiguration;

  constructor(private router: Router) {
    super();
  }

  ngOnInit() {
    this.tableConfigurationProgramme = new TableConfiguration({
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

    this.tableConfigurationApplicant = new TableConfiguration({
      isTableClickable: false,
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
        },
        {
          displayedColumn: 'Actions',
          customCellTemplate: this.actionsCell
        }
      ]
    });
  }

  applyToCall(callId: number): void {
    this.router.navigate(['/call/' + callId + '/apply']);
  }
}
