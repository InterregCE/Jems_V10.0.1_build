import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ProjectPartnerReportDTO, UserRoleDTO} from '@cat/api';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {MatDialog} from '@angular/material/dialog';
import {APIError} from '@common/models/APIError';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {Alert} from '@common/components/forms/alert';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-partner-control-report',
  templateUrl: './partner-control-report.component.html',
  styleUrls: ['./partner-control-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportComponent {

    ProjectPartnerReportDTO = ProjectPartnerReportDTO;
    Alert = Alert;
    data$: Observable<{
        report: ProjectPartnerReportDTO;
        allTabsVisible: boolean;
        partnerName: string;
        partnerId: string | number | null;
        projectAcronym: string;
        hasReopenPermission: boolean;
    }>;
    actionPending = false;
    error$ = new BehaviorSubject<APIError | null>(null);

    constructor(
    private activatedRoute: ActivatedRoute,
    private router: RoutingService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private partnerReportPageStore: PartnerReportPageStore,
    private permissionService: PermissionService,
    private dialog: MatDialog,
    private pageStore: PartnerControlReportStore,
    private route: ActivatedRoute
    ) {
    this.data$ = combineLatest([
      partnerReportDetailPageStore.partnerReport$,
      partnerReportPageStore.institutionUserCanViewControlReports$,
      partnerReportPageStore.userCanViewReport$,
      pageStore.partnerId$,
      permissionService.hasPermission(PermissionsEnum.ProjectPartnerControlReportingReOpen)
    ]).pipe(
      map(([report, controllerCanView, collaboratorOrProgrammeUserCanView, partnerId, hasReopenPermission]) => ({
        report,
        allTabsVisible: controllerCanView || (collaboratorOrProgrammeUserCanView && report.status === ProjectPartnerReportDTO.StatusEnum.Certified),
        partnerName: `${report.identification?.partnerNumber} ${report.identification?.partnerAbbreviation}`,
        partnerId,
        projectAcronym: report.identification?.projectAcronym,
        hasReopenPermission
      })),
      tap(data => {
        if (!data.allTabsVisible && !this.activeTab('document')) {
          this.routeTo('document');
        }
      }),
    );
  }

  reopenControlReport(partnerId: number, reportId: number) {
        this.actionPending = true;
        return this.pageStore.reopenControlReport(partnerId, reportId)
            .pipe(
                tap(() => this.redirectToReportList()),
                catchError((error) => this.showErrorMessage(error.error)),
                finalize(() => this.actionPending = false)
            ).subscribe();
  }

    private showErrorMessage(error: APIError): Observable<null> {
        this.error$.next(error);
        setTimeout(() => {
            this.error$.next(null);
        }, 4000);
        return of(null);
    }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

  public redirectToReport(reportId: number): void {
    this.router.navigate([`../../${reportId}/identification`], {
      relativeTo: this.activatedRoute,
      queryParamsHandling: 'merge'
    })
  }

  private redirectToReportList(): void {
    this.router.navigate(['../..'], {relativeTo: this.route});
  }
}
