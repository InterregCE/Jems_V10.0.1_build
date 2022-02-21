import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {map} from 'rxjs/operators';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class ReportDetailPageBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private projectPartnerStore: ProjectPartnerStore,
              private translationService: TranslateService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.partnerReportDetailPageStore.partnerReport$
      .pipe(
        map(report =>
          this.translationService.instant(
            `project.application.partner.reports.title.number`, {reportNumber: `${report.reportNumber}`}))
      )
    );
  }

}
