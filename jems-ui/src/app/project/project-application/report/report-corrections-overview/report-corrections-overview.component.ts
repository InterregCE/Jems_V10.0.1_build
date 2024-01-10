import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {PageAuditControlDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ReportCorrectionsOverviewStore} from '@project/project-application/report/report-corrections-overview/report-corrections-overview.store';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';

@Component({
  selector: 'jems-report-corrections-overview',
  templateUrl: './report-corrections-overview.component.html',
  styleUrls: ['./report-corrections-overview.component.scss']
})
export class ReportCorrectionsOverviewComponent implements OnInit {

  MAX_ALLOWED_AUDITS = 100;

  data$: Observable<{
    page: PageAuditControlDTO;
    canEdit: boolean;
  }>;

  tableConfiguration: TableConfiguration;

  @ViewChild('idCell', {static: true})
  idCell: TemplateRef<any>;
  @ViewChild('statusCell', {static: true})
  statusCell: TemplateRef<any>;
  @ViewChild('controllingBody', {static: true})
  controllingBody: TemplateRef<any>;
  @ViewChild('controlType', {static: true})
  controlType: TemplateRef<any>;

  constructor(
    public overviewStore: ReportCorrectionsOverviewStore,
  ) {
    this.data$ = combineLatest([
      overviewStore.auditControls$,
      overviewStore.canEdit$,
    ]).pipe(
      map(([page, canEdit]) => ({
        page,
        canEdit
      }))
    );
  }

  ngOnInit() {
    this.tableConfiguration = this.initConfiguration();
  }

  private initConfiguration(): TableConfiguration {
    return new TableConfiguration({
      sortable: true,
      isTableClickable: true,
      routerLink: 'auditControl/',
      columns: [
        {
          displayedColumn: 'common.id',
          columnWidth: ColumnWidth.IdLongColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.idCell,
          sortProperty: 'id'
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.status',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.StringColumn,
          customCellTemplate: this.statusCell,
          sortProperty: 'status'
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.controllingBody',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          customCellTemplate: this.controllingBody,
          sortProperty: 'controllingBody',
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.controlType',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          customCellTemplate: this.controlType,
          sortProperty: 'controlType',
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.startDate',
          elementProperty: 'startDate',
          columnWidth: ColumnWidth.DateColumn,
          columnType: ColumnType.DateColumn,
          sortProperty: 'startDate',
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.endDate',
          elementProperty: 'endDate',
          columnWidth: ColumnWidth.DateColumn,
          columnType: ColumnType.DateColumn,
          sortProperty: 'endDate',
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.finalReportDate',
          elementProperty: 'finalReportDate',
          columnWidth: ColumnWidth.DateColumn,
          columnType: ColumnType.DateColumn,
          sortProperty: 'finalReportDate',
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.totalControlledAmount',
          elementProperty: 'totalControlledAmount',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.Decimal,
          sortProperty: 'totalControlledAmount',
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.totalCorrectionsAmount',
          elementProperty: 'totalCorrectionsAmount',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.Decimal,
          sortProperty: 'totalCorrectionsAmount',
        },
      ]
    });
  }
}
