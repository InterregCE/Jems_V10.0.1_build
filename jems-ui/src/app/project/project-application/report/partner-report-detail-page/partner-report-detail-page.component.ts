import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {ProjectPartnerReportDTO, ProjectPartnerReportWorkPackageDTO, UserRoleCreateDTO} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, filter, finalize, map, switchMap, take} from 'rxjs/operators';
import {
  PartnerReportWorkPlanPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-work-plan-progress-tab/partner-report-work-plan-page-store.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import {ReportUtil} from '@project/common/report-util';

@Component({
  selector: 'jems-partner-report-detail-page',
  templateUrl: './partner-report-detail-page.component.html',
  styleUrls: ['./partner-report-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportDetailPageComponent {

  ReportUtil = ReportUtil;
  StatusEnum = ProjectPartnerReportDTO.StatusEnum;
  ProjectPartnerReportDTO = ProjectPartnerReportDTO;
  data$: Observable<{
    workPackages: ProjectPartnerReportWorkPackageDTO[];
    hasReopenPermission: boolean;
    partnerId: string | number | null;
    partnerReportId: number;
  }>;
  actionPending = false;
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;

  constructor(private activatedRoute: ActivatedRoute,
              public pageStore: PartnerReportDetailPageStore,
              private router: RoutingService,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              private partnerReportWorkPlanPageStore: PartnerReportWorkPlanPageStore,
              private permissionService: PermissionService,
              private dialog: MatDialog) {

    this.data$ = combineLatest([
      this.partnerReportWorkPlanPageStore.partnerWorkPackages$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingReOpen),
      this.pageStore.partnerId$,
      this.pageStore.partnerReportId$,
    ])
      .pipe(
        map(([workPackages, hasReopenPermission, partnerId, partnerReportId]) => ({
          workPackages,
          hasReopenPermission,
          partnerId,
          partnerReportId
        })),
      );
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

  reopenReport(partnerId: number, reportId: number) {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.partner.reports.reopen',
        message: {
          i18nKey: 'project.application.partner.reports.reopen.confirm.message',
        }
      }).pipe(
      take(1),
      filter(confirmed => confirmed),
      switchMap(() => {
        this.actionPending = true;
        return this.pageStore.reopenReport(partnerId, reportId).pipe(
          catchError((error) => this.showErrorMessage(error.error)),
          finalize(() => this.actionPending = false)
        );
      }),
    ).subscribe();
  }

  public showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 4000);
    return of(null);
  }

}
