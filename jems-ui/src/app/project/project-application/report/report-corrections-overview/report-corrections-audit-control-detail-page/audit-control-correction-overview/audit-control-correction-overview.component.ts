import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
    PageProjectAuditControlCorrectionLineDTO, ProgrammeChecklistDTO,
    ProjectAuditControlCorrectionDTO,
    ProjectAuditControlCorrectionLineDTO
} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {
  AuditControlCorrectionStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-store.service';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {APIError} from '@common/models/APIError';
import { Alert } from '@common/components/forms/alert';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'jems-audit-control-correction-overview',
  templateUrl: './audit-control-correction-overview.component.html',
  styleUrls: ['./audit-control-correction-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditControlCorrectionOverviewComponent implements OnInit {

  MAX_ALLOWED_CORRECTIONS = 100;
  actionPending = false;
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;
  TypeEnum = ProjectAuditControlCorrectionLineDTO.TypeEnum;

  canEdit = true;

  @ViewChild('idCell', {static: true})
  idCell: TemplateRef<any>;
  @ViewChild('statusCell', {static: true})
  statusCell: TemplateRef<any>;
  @ViewChild('partnerRoleCell', {static: true})
  partnerRoleCell: TemplateRef<any>;
  @ViewChild('reportCell', {static: true})
  reportCell: TemplateRef<any>;
  @ViewChild('linkedCell', {static: true})
  linkedCell: TemplateRef<any>;
  @ViewChild('followUpCell', {static: true})
  followUpCell: TemplateRef<any>;
  @ViewChild('impactCell', {static: true})
  impactCell: TemplateRef<any>;
  @ViewChild('scenarioCell', {static: true})
  scenarioCell: TemplateRef<any>;
  @ViewChild('actionCell', {static: true})
  actionCell: TemplateRef<any>;

  data$: Observable<{
    page: PageProjectAuditControlCorrectionLineDTO;
    projectId: number;
    auditControlId: number;
    canEdit: boolean;
  }>;

  tableConfiguration: TableConfiguration;

  constructor(
    public correctionsOverviewStore: AuditControlCorrectionStore,
    public router: RoutingService,
    private detailStore: ReportCorrectionsAuditControlDetailPageStore,
    private activatedRoute: ActivatedRoute,
    private dialog: MatDialog
  ) {
    this.data$ = combineLatest([
      correctionsOverviewStore.corrections$,
      detailStore.projectId$,
      detailStore.auditControlId$,
      detailStore.canEdit$,
    ]).pipe(
      map(([page, projectId, auditControlId, canEdit]) => ({
        page,
        projectId,
        auditControlId: Number(auditControlId),
        canEdit})),
      tap(data => this.canEdit = data.canEdit),
      tap(data => this.canEdit = data.canEdit),
      );
  }

  ngOnInit() {
    this.tableConfiguration = this.initConfiguration();
  }

  private initConfiguration(): TableConfiguration {
    return new TableConfiguration({
      sortable: true,
      isTableClickable: true,
      routerLink: 'correction/',
      columns: [
        {
          displayedColumn: 'common.id',
          columnWidth: ColumnWidth.IdColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.idCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.status',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.statusCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.partner',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.partnerRoleCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.partner.report',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.reportCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.linked',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.linkedCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.follow.up',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.followUpCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.fund.name',
          elementProperty: 'fundType',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.StringColumn,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.fund.amount',
          elementProperty: 'fundAmount',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.public.contribution',
          elementProperty: 'publicContribution',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.auto.public.contribution',
          elementProperty: 'autoPublicContribution',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.private.contribution',
          elementProperty: 'privateContribution',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.total',
          elementProperty: 'total',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.impact',
          columnWidth: ColumnWidth.MediumColumnNoLimit,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.impactCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.scenario',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.scenarioCell,
        },
        {
          displayedColumn: 'project.application.reporting.corrections.audit.control.correction.table.action.delete',
          columnWidth: ColumnWidth.SmallColumn,
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.actionCell,
        },
      ]
    });
  }

  createCorrection(projectId: number, auditControlId: number, linking: boolean): void {
    this.actionPending = true;
    this.correctionsOverviewStore.createEmptyCorrection(projectId, auditControlId, linking)
      .pipe(
        take(1),
        tap((correction) => this.router.navigate([`../correction/${correction.id}`], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  public showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 4000);
    return of(null);
  }

  delete(correctionId: number): void {
    Forms.confirm(
      this.dialog, {
        title:  'project.application.reporting.corrections.audit.control.correction.table.action.delete.confirmation.title',
        message: 'project.application.reporting.corrections.audit.control.correction.table.action.delete.confirmation.message'
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.correctionsOverviewStore.deleteCorrection(correctionId)),
      ).subscribe();
  }
}
