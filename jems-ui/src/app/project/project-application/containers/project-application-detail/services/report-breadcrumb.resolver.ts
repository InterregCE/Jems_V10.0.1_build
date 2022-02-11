import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {TranslateService} from '@ngx-translate/core';
import {map} from 'rxjs/operators';
import {
  ProjectPartnerReportPageStore
} from '@project/project-application/report/project-partner-report-page-store.service';

@Injectable()
export class ReportBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private projectPartnerStore: ProjectPartnerStore, private projectPartnerReportPageStore: ProjectPartnerReportPageStore, private translateService: TranslateService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(
      combineLatest([this.projectPartnerReportPageStore.partnerReport$, this.projectPartnerStore.projectCallType$])
        .pipe(
          map(([partner, callType]) =>
            this.translateService.instant(
              `project.application.partner.reports.title.number`, {reportNumber: `${partner?.reportNumber}`}))
        )
    );
  }
}
