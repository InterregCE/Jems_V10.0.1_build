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
  displayedColumns = ['reportNumber', 'status', 'linkedFormVersion', 'reportingPeriod', 'type', 'createdAt', 'firstSubmission', 'delete'];
  dataSource: MatTableDataSource<ProjectReportSummaryDTO> = new MatTableDataSource([]);

  data$: Observable<{
    projectReports: PageProjectReportSummaryDTO;
    canEditReports: boolean;
    totalElements: number;
  }>;

  constructor(
    public pageStore: ProjectReportPageStore,
    public store: PartnerControlReportStore,
    private activatedRoute: ActivatedRoute,
    private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
    private router: RoutingService,
    private translateService: TranslateService,
    private multiLanguageGlobalService: MultiLanguageGlobalService,
    private dialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.data$ = combineLatest([
      pageStore.projectReports$,
      pageStore.userCanEditReport$
    ]).pipe(
      tap(([projectReports, isEditable]) => this.dataSource.data = projectReports.content),
      map(([projectReports, isEditable]) => ({
        projectReports,
        canEditReports: isEditable,
        totalElements: projectReports.totalElements
      })),
    );
  }

  private showErrorMessage(error: APIError): Observable<null> {
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

}
