import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {ActivatedRoute} from '@angular/router';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {TranslateService} from '@ngx-translate/core';
import {MultiLanguageGlobalService} from '@common/components/forms/multi-language-container/multi-language-global.service';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {MatTableDataSource} from '@angular/material/table';
import {PageProjectReportSummaryDTO, ProjectReportSummaryDTO, UserRoleDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {ProjectReportPageStore} from './project-report-page-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Component({
  selector: 'jems-project-report',
  templateUrl: './project-report.component.html',
  styleUrls: ['./project-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportComponent {

  PermissionsEnum = PermissionsEnum;
  ProjectReportSummaryDTO = ProjectReportSummaryDTO;
  successfulDeletionMessage: boolean;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;
  displayedColumns = ['reportNumber', 'status', 'linkedFormVersion', 'reportingPeriod', 'type', 'createdAt', 'firstSubmission',
    'lastSubmission', 'amountRequested', 'verificationEndDate', 'totalEligible', 'verification', 'delete', 'anchor'];
  dataSource: MatTableDataSource<ProjectReportSummaryDTO> = new MatTableDataSource([]);
  MAX_PROJECT_REPORTS_ALLOWED = 100;
  availablePeriodNumbers: number[] = [];

  data$: Observable<{
    projectReports: PageProjectReportSummaryDTO;
    currentApprovedVersion: string | undefined;
    canEditReports: boolean;
    totalElements: number;
    viewVerification: boolean;
  }>;

  constructor(
    public pageStore: ProjectReportPageStore,
    public versionStore: ProjectVersionStore,
    public store: PartnerControlReportStore,
    private activatedRoute: ActivatedRoute,
    private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
    private router: RoutingService,
    private translateService: TranslateService,
    private multiLanguageGlobalService: MultiLanguageGlobalService,
    private dialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef,
    private projectStore: ProjectStore
  ) {
    this.data$ = combineLatest([
      pageStore.projectReports$,
      versionStore.lastApprovedOrContractedOrClosedVersion$,
      pageStore.userCanEditReport$,
      pageStore.userCanViewVerification$,
      projectStore.projectPeriods$
    ]).pipe(
      tap(([projectReports, approvedVersion, _, viewVerification, projectPeriods]) => {
        if (!viewVerification) {
          this.displayedColumns = this.displayedColumns.filter(column => column !== 'verification');
        }
        this.dataSource.data = projectReports.content;
        this.availablePeriodNumbers = [...projectPeriods.map(p => p.number), 255];
      }),
      map(([projectReports, approvedVersion, isEditable, viewVerification]) => ({
        projectReports,
        currentApprovedVersion: approvedVersion?.version,
        canEditReports: isEditable,
        totalElements: projectReports.totalElements,
        viewVerification
      })),
    );
  }

  showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },4000);
    return of(null);
  }

  private showSuccessMessageAfterDeletion(): void {
    this.successfulDeletionMessage = true;
    setTimeout(() => {
      this.successfulDeletionMessage = false;
      this.changeDetectorRef.markForCheck();
    }, 4000);
  }

  delete(projectReport: ProjectReportSummaryDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'project.application.project.report.confirm.deletion.header',
        message: {i18nKey: 'project.application.project.report.confirm.deletion.message', i18nArguments: {id: projectReport.reportNumber.toString()}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deleteProjectReport(projectReport.id)),
        tap(() => this.showSuccessMessageAfterDeletion()),
        catchError((error) => this.showErrorMessage(error.error)),
      ).subscribe();
  }

  createProjectReport(): void {
    this.router.navigate([`/app/project/detail/${this.projectId}/projectReports/create`]);
  }
}
