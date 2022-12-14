import {Injectable} from '@angular/core';
import {ProjectPartnerReportDTO, ProjectPartnerReportService} from '@cat/api';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@Injectable({providedIn: 'root'})
export class ControlReportGuard implements CanActivate {

  constructor(
    private router: Router,
    private pageStore: PartnerReportDetailPageStore,
    private partnerReportPageStore: PartnerReportPageStore,
    private projectPartnerReportService: ProjectPartnerReportService,
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    const partnerId = Number(route.parent?.params?.partnerId || 0);
    const reportId = Number(route.parent?.params?.reportId || 0);
    const tab = state.url.split('/').pop();

    return combineLatest([
      this.pageStore.partnerReport$.pipe(
        // take report from store or load it if it is not available in store
        switchMap(report => report.id ? of(report) : this.projectPartnerReportService.getProjectPartnerReport(partnerId, reportId)),
      ),
      this.partnerReportPageStore.institutionUserCanViewControlReports$,
      this.partnerReportPageStore.userCanViewReport$,
    ]).pipe(
      map(([report, controllerCanView, collaboratorOrProgrammeUserCanView]) =>
        controllerCanView
        || (collaboratorOrProgrammeUserCanView && report.status === ProjectPartnerReportDTO.StatusEnum.Certified)
      ),
      tap(allowed => {
        if (!allowed) {
          Log.info(`Current user role cannot access this control report tab. Tab: ${tab}`, this);
          this.router.navigate(['app']);
        }
      }),
    );
  }
}
