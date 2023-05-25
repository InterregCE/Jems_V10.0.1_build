import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {PageProjectPartnerReportSummaryDTO, ProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO, ProjectVersionDTO, UserRoleDTO} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {ProjectApplicationFormSidenavService} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {TranslateService} from '@ngx-translate/core';
import {MultiLanguageGlobalService} from '@common/components/forms/multi-language-container/multi-language-global.service';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';
import {ReportUtil} from '@project/common/report-util';
import {MatTableDataSource} from '@angular/material/table';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

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

  private allColumns: string[] = ['id', 'status', 'projectReport', 'version', 'period', 'createdAt', 'firstSubmission',
    'lastReSubmission', 'totalAfterSubmitted', 'controlEnd', 'totalEligible', 'control', 'delete'];
  displayedColumns: string[] = [];
  dataSource = new MatTableDataSource<ProjectPartnerReportSummaryDTO>();

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  tableConfiguration: TableConfiguration;
  actionPending = false;
  controlActionMap = new Map<number, BehaviorSubject<boolean>>();
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;
  isStartControlButtonDisabled = false;

  data$: Observable<{
    totalElements: number;
    partnerReports: PageProjectPartnerReportSummaryDTO;
    partner: ProjectPartnerSummaryDTO;
    isController: boolean;
    canEditReport: boolean;
    currentApprovedVersion: string | undefined;
    canViewProjectReport: boolean;
    canCreateReport: boolean;
    lastReportIsNotReOpened: boolean;
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
      pageStore.partnerReportLevel$,
      pageStore.canCreateReport$,
    ]).pipe(
      tap(([partnerReports, _]: [PageProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO, boolean, boolean, ProjectVersionDTO | undefined, boolean, string, boolean]) => this.dataSource.data = partnerReports.content),
      map(([partnerReports, partner, canEditReport, isController, approvedVersion, canViewProjectReport, partnerReportLevel, lastReportIsNotReOpened]: [PageProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO, boolean, boolean, ProjectVersionDTO | undefined, boolean, string, boolean]) => {
          return {
            totalElements: partnerReports.totalElements,
            partnerReports,
            isController,
            partner,
            canEditReport,
            currentApprovedVersion: approvedVersion?.version,
            canViewProjectReport,
            canCreateReport: partnerReportLevel === 'EDIT',
            lastReportIsNotReOpened,
          };
        }
      ),
      tap(data => {
        const someDraft = data.partnerReports.content
          .find((x) => x.status === ProjectPartnerReportSummaryDTO.StatusEnum.Draft);
        const someCertified = data.partnerReports.content
          .find((x) => x.status === ProjectPartnerReportSummaryDTO.StatusEnum.Certified);

        this.displayedColumns.splice(0);
        this.allColumns.forEach(column => {
          if (column === 'delete' && (!data.canEditReport || !someDraft)) {
            return;
          }
          if (column === 'controlEnd' && (!someCertified)) {
            return;
          }
          this.displayedColumns.push(column);
        });
        this.currentApprovedVersion = data.currentApprovedVersion ?? '';
        this.canViewProjectReport = data.canViewProjectReport ?? false;

        data.partnerReports.content.forEach((report) =>
          this.controlActionMap.set(report.id, new BehaviorSubject<boolean>(false)));
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
        tap(() => this.router.navigate([`../${partnerReport.id}/controlReport/identificationTab`], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge'
        })),
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

}
