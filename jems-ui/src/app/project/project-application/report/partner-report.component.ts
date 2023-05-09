import {ChangeDetectionStrategy, ChangeDetectorRef, Component, TemplateRef, ViewChild} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  PageProjectPartnerReportSummaryDTO,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO, ProjectVersionDTO,
  UserRoleDTO
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {catchError, distinctUntilChanged, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {
  ProjectApplicationFormSidenavService
} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {TranslateService} from '@ngx-translate/core';
import {
  MultiLanguageGlobalService
} from '@common/components/forms/multi-language-container/multi-language-global.service';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import StatusEnum = ProjectPartnerReportSummaryDTO.StatusEnum;
import {ReportUtil} from '@project/common/report-util';

@UntilDestroy()
@Component({
  selector: 'jems-contract-monitoring',
  templateUrl: './partner-report.component.html',
  styleUrls: ['./partner-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportComponent {
  PermissionsEnum = PermissionsEnum;
  ProjectPartnerReportSummaryDTO = ProjectPartnerReportSummaryDTO;
  successfulDeletionMessage: boolean;
  ReportUtil = ReportUtil;

  @ViewChild('numberingCell', {static: true})
  numberingCell: TemplateRef<any>;

  @ViewChild('statusCell', {static: true})
  statusCell: TemplateRef<any>;
  @ViewChild('projectReportCell', {static: true})
  projectReportCell: TemplateRef<any>;

  @ViewChild('periodCell', {static: true})
  periodCell: TemplateRef<any>;

  @ViewChild('actionCell', {static: true})
  actionCell: TemplateRef<any>;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  @ViewChild('versionCell', {static: true})
  versionCell: TemplateRef<any>;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  tableConfiguration: TableConfiguration;
  actionPending = false;
  controlActionMap = new Map<number, BehaviorSubject<boolean>>();
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;
  isStartControlButtonDisabled = false;
  deletableReportId: number | null = null;

  data$: Observable<{
    totalElements: number;
    partnerReports: ProjectPartnerReportSummaryDTO[];
    partner: ProjectPartnerSummaryDTO;
    canEditReport: boolean;
    currentApprovedVersion: string | undefined;
    canViewProjectReport: boolean;
    canCreateReport: boolean;
  }>;
  currentApprovedVersion: string;
  canViewProjectReport: boolean;

  readonly isControlButtonVisible$: Observable<boolean> = combineLatest([
    this.pageStore.institutionUserCanViewControlReports$,
    this.pageStore.institutionUserCanEditControlReports$,
    this.pageStore.userCanViewReport$,
    this.pageStore.userCanEditReport$
  ]).pipe(
    map(([
           canViewFromInstitution,
           canEditFromInstitution,
           canViewFromProjectPrivileges,
           canEditFromProjectPrivileges]) => (canViewFromInstitution || canEditFromInstitution) || canViewFromProjectPrivileges || canEditFromProjectPrivileges
    )
  );

  constructor(
    public pageStore: PartnerReportPageStore,
    public versionStore: ProjectVersionStore,
    public store: PartnerControlReportStore,
    public partnerReportDetail: PartnerReportDetailPageStore,
    private activatedRoute: ActivatedRoute,
    private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
    private router: RoutingService,
    private translateService: TranslateService,
    private multiLanguageGlobalService: MultiLanguageGlobalService,
    private dialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef,
    private projectReportPageStore: ProjectReportPageStore,
  ) {
    this.data$ = combineLatest([
      pageStore.partnerReports$,
      pageStore.partnerSummary$,
      pageStore.userCanEditReport$,
      pageStore.institutionUserCanViewControlReports$,
      versionStore.lastApprovedOrContractedVersion$,
      projectReportPageStore.userCanViewReport$,
      pageStore.canCreateReport$,
    ]).pipe(
      map(([partnerReports, partner, canEditReport, isController, approvedVersion, canViewProjectReport, canCreateReport]: [PageProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO, boolean, boolean, ProjectVersionDTO | undefined, boolean, boolean]) => {
          return {
            totalElements: partnerReports.totalElements,
            partnerReports: partnerReports.content.map(report => ({
              ...report,
              routeToControl: `${report.id}/controlReport/${(report.status === StatusEnum.Certified || isController) ? 'identificationTab' : 'document'}`,
            })),
            partner,
            canEditReport,
            currentApprovedVersion: approvedVersion?.version,
            canViewProjectReport,
            canCreateReport
          };
        }
      ),
      tap(data => {
        data.partnerReports.forEach((report) => {
          this.controlActionMap.set(report.id, new BehaviorSubject<boolean>(false));
          if (report.reportNumber === data.totalElements && report.status === StatusEnum.Draft) {
            this.deletableReportId = report.reportNumber;
          }
          this.currentApprovedVersion = data.currentApprovedVersion ?? '';
          this.canViewProjectReport = data.canViewProjectReport ?? false;
          const someDraft = data.partnerReports
            .find((x) => x.status === ProjectPartnerReportSummaryDTO.StatusEnum.Draft);
          const someCertified = data.partnerReports
            .find((x) => x.status === ProjectPartnerReportSummaryDTO.StatusEnum.Certified);
          this.refreshColumns(data.canEditReport, !!someDraft, !!someCertified);
        });
      })
    );

    combineLatest([
      this.pageStore.institutionUserCanViewControlReports$,
      this.pageStore.institutionUserCanEditControlReports$,
    ]).pipe(
      map(([canViewFromInstitution, canEditFromInstitution]) => {
          if (canViewFromInstitution && !canEditFromInstitution) {
            this.isStartControlButtonDisabled = true;
          }
          this.isStartControlButtonDisabled = !canEditFromInstitution;
        }
      )
    ).subscribe();
  }

  private refreshColumns(canEditReport: boolean, thereIsDraft: boolean, thereIsCertified: boolean) {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: true,
      columns: [
        {
          displayedColumn: 'project.application.partner.reports.table.id',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.numberingCell,
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.status',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.statusCell,
          columnWidth: ColumnWidth.ChipColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.included',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.projectReportCell,
          columnWidth: ColumnWidth.SmallColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.version',
          elementProperty: 'linkedFormVersion',
          customCellTemplate: this.versionCell,
          columnWidth: ColumnWidth.SmallColumn
        },
        {
          displayedColumn: 'project.application.partner.report.reporting.period',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.periodCell,
          columnWidth: ColumnWidth.MediumColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.created.at',
          elementProperty: 'createdAt',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.first.submission',
          elementProperty: 'firstSubmission',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.last.submission',
          elementProperty: 'lastSubmission',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.amount.submitted',
          elementProperty: 'amountSubmitted',
          columnType: ColumnType.Decimal,
        },
        ...(thereIsCertified) ? [{
          displayedColumn: 'project.application.partner.reports.table.control.end',
          elementProperty: 'controlEnd',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn
        }, {
          displayedColumn: 'project.application.partner.report.control.tab.overviewAndFinalize.total.eligible.after.control',
          elementProperty: 'totalEligibleAfterControl',
          columnType: ColumnType.Decimal,
        }] : [],
        {
          displayedColumn: 'project.application.partner.reports.table.control',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.actionCell,
          clickable: false,
        },
        ...(canEditReport && thereIsDraft) ? [{
          displayedColumn: 'common.delete.entry',
          customCellTemplate: this.deleteCell,
          columnWidth: ColumnWidth.DeletionColumn,
          clickable: false,
        }] : [],
      ]
    });

    this.pageStore.partnerId$
      .pipe(
        distinctUntilChanged(),
        filter(partnerId => !!partnerId),
        tap(partnerId => this.initializeTableConfiguration(partnerId as any)),
        untilDestroyed(this)
      ).subscribe();
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): string {
    return `${partner.sortNumber || ''} ${partner.abbreviation}`;
  }

  createPartnerReport(): void {
    this.actionPending = true;
    this.pageStore.createPartnerReport()
      .pipe(
        take(1),
        tap((report) => this.router.navigate([`../${report.id}/identification`], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  createControlReportForPartnerReport(partnerReport: ProjectPartnerReportSummaryDTO, partnerId: string | number | null): void {
    if (this.isStartControlButtonDisabled) {
      return;
    }

    this.getPendingActionStatus(partnerReport.id).next(true);
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.partner.report.confirm.control.start.header',
        message: {
          i18nKey: 'project.application.partner.report.confirm.control.start.message'
        },
      }).pipe(
      take(1),
      tap((answer) => {
        if (answer) {
          this.changeStatusOfReport(partnerReport, partnerId);
        } else {
          this.getPendingActionStatus(partnerReport.id).next(false);
        }
      })).subscribe();
  }

  getPendingActionStatus(reportId: number): any {
    return this.controlActionMap.get(reportId);
  }

  private initializeTableConfiguration(partnerId: number): void {
    this.tableConfiguration.routerLink = `/app/project/detail/${this.projectId}/reporting/${partnerId}/reports/`;
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 4000);
    return of(null);
  }

  private showSuccessMessageAfterDeletion(): void {
    this.successfulDeletionMessage = true;
    setTimeout(() => {
      this.successfulDeletionMessage = false;
      this.changeDetectorRef.markForCheck();
    }, 4000);
  }

  private changeStatusOfReport(partnerReport: ProjectPartnerReportSummaryDTO, partnerId: string | number | null): void {
    if (!partnerId) {
      return;
    }

    this.partnerReportDetail.startControlOnPartnerReport(Number(partnerId), partnerReport.id)
      .pipe(
        take(1),
        tap(() => this.router.navigate([`../${partnerReport.id}/controlReport/identificationTab`], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.getPendingActionStatus(partnerReport.id).next(false))
      ).subscribe();
  }

  delete(partnerReport: ProjectPartnerReportSummaryDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'project.application.partner.report.confirm.deletion.header',
        message: {i18nKey: 'project.application.partner.report.confirm.deletion.message', i18nArguments: {id: partnerReport.reportNumber.toString()}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deletePartnerReport(partnerReport.id)),
        tap(() => this.showSuccessMessageAfterDeletion()),
        catchError((error) => this.showErrorMessage(error.error)),
      ).subscribe();
  }

  isReportVersionNotLatestApproved(reportVersion: string): boolean {
    return reportVersion !== this.currentApprovedVersion;
  }
}
