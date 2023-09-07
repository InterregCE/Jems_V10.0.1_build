import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {combineLatest, Observable} from 'rxjs';
import {PageProjectReportSummaryDTO} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ProjectReportListStoreService} from "@common/components/project-report-list/project-report-list-store.service";

@Component({
  selector: 'jems-project-report-list',
  templateUrl: './project-report-list.component.html',
  styleUrls: ['./project-report-list.component.scss'],
  providers: [ProjectReportListStoreService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProjectReportListComponent implements OnInit {

  @ViewChild('reportingPeriodCell', {static: true})
  reportingPeriodCell: TemplateRef<any>;

  @ViewChild('projectReportNumberCell', {static: true})
  projectReportNumberCell: TemplateRef<any>;

  @ViewChild('projectReportStatusCell', {static: true})
  projectReportStatusCell: TemplateRef<any>;

  @ViewChild('projectReportTypeCell', {static: true})
  projectReportTypeCell: TemplateRef<any>;

  data$: Observable<{
    page: PageProjectReportSummaryDTO;
  }>;

  tableConfiguration: TableConfiguration;

  currentPageSize = 10;

  constructor(public projectReportListStoreService: ProjectReportListStoreService) { }

  ngOnInit() {
    this.data$ = combineLatest([
      this.projectReportListStoreService.projectReportListPage$,
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
      routerLink: '/app/project/detail/{projectId}/projectReports/{id}/',
      extraPathParamFields: ['projectId', 'id'],
      columns: [
        {
          displayedColumn: 'user.project.reports.table.column.name.project.id',
          elementProperty: 'projectId',
          sortProperty: 'projectId',
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'common.id',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.projectReportNumberCell
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.status',
          sortProperty: 'status',
          columnWidth: ColumnWidth.MediumColumn,
          customCellTemplate: this.projectReportStatusCell
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.af.linked.version',
          elementProperty: 'linkedFormVersion',
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.reporting.period',
          customCellTemplate: this.reportingPeriodCell
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.reporting.type',
          elementProperty: 'type',
          sortProperty: 'type',
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.projectReportTypeCell
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.date.of.creation',
          columnType: ColumnType.DateColumn,
          elementProperty: 'createdAt',
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.first.submission.date',
          columnType: ColumnType.DateColumn,
          elementProperty: 'firstSubmission',
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'firstSubmission',
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.amount.requested',
          elementProperty: 'amountRequested',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.verification.end.date',
          columnType: ColumnType.DateColumn,
          elementProperty: 'verificationEndDate',
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'verificationEndDate',
        },
        {
          displayedColumn: 'user.project.reports.table.column.name.total.eligible.after.verification',
          elementProperty: 'totalEligibleAfterVerification',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
        }
      ]
    });
  }
}
