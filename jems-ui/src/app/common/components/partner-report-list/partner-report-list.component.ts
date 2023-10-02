import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {PartnerReportListStoreService} from '@common/components/partner-report-list/partner-report-list-store.service';
import {combineLatest, Observable} from 'rxjs';
import {PageProjectPartnerReportSummaryDTO} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';

@Component({
  selector: 'jems-partner-report-list',
  templateUrl: './partner-report-list.component.html',
  styleUrls: ['./partner-report-list.component.scss'],
  providers: [PartnerReportListStoreService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class PartnerReportListComponent implements OnInit {

  @ViewChild('partnerRoleCell', {static: true})
  partnerRoleCell: TemplateRef<any>;

  @ViewChild('reportingPeriodCell', {static: true})
  reportingPeriodCell: TemplateRef<any>;

  @ViewChild('controlCell', {static: true})
  controlCell: TemplateRef<any>;

  @ViewChild('partnerReportNumberCell', {static: true})
  partnerReportNumberCell: TemplateRef<any>;

  @ViewChild('partnerReportStatusCell', {static: true})
  partnerReportStatusCell: TemplateRef<any>;

  data$: Observable<{
    page: PageProjectPartnerReportSummaryDTO;
  }>;

  tableConfiguration: TableConfiguration;

  currentPageSize = 10;

  constructor(public partnerReportListStoreService: PartnerReportListStoreService) {}

  ngOnInit() {
    this.data$ = combineLatest([
      this.partnerReportListStoreService.partnerReportListPage$,
    ])
      .pipe(
        map(([page]) => ({
          page,
        })),
        tap(_ => this.generateTableConfiguration())
      );
  }

  generateTableConfiguration(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      routerLink: '/app/project/detail/{projectId}/reporting/{partnerId}/reports/{id}/',
      extraPathParamFields: ['projectId', 'partnerId', 'id'],
      columns: [
        {
          displayedColumn: 'user.partner.reports.table.column.name.project.id',
          elementProperty: 'projectCustomIdentifier',
          sortProperty: 'identification.projectIdentifier',
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.partner.role',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.partnerRoleCell
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.partner.name',
          elementProperty: 'partnerAbbreviation',
          sortProperty: 'identification.partnerAbbreviation',
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.partner.report.number',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.partnerReportNumberCell
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.status',
          sortProperty: 'status',
          columnWidth: ColumnWidth.MediumColumn,
          customCellTemplate: this.partnerReportStatusCell
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.af.linked.version',
          elementProperty: 'linkedFormVersion',
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.reporting.period',
          customCellTemplate: this.reportingPeriodCell
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.creation.date',
          columnType: ColumnType.DateColumn,
          elementProperty: 'createdAt',
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.first.submission.date',
          columnType: ColumnType.DateColumn,
          elementProperty: 'firstSubmission',
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'firstSubmission',
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.last.submission.date',
          columnType: ColumnType.DateColumn,
          elementProperty: 'lastReSubmission',
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.amount.submitted',
          elementProperty: 'totalAfterSubmitted',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.control.end.date',
          columnType: ColumnType.DateColumn,
          elementProperty: 'controlEnd',
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'controlEnd',
        },
        {
          displayedColumn: 'user.partner.reports.table.column.name.total.eligible.after.control',
          elementProperty: 'totalEligibleAfterControl',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
        },
      ]
    });
  }
}
