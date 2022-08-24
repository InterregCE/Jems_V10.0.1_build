import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {TranslateService} from '@ngx-translate/core';
import {map} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class ControlReportPageBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private projectPartnerStore: ProjectPartnerStore,
              private partnerReportPageStore: PartnerReportPageStore,
              private translateService: TranslateService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(
      combineLatest([this.partnerReportPageStore.partnerSummary$, this.projectPartnerStore.projectCallType$])
        .pipe(
          map(() =>
            this.translateService.instant('project.application.partner.control.report.breadcrumb.title')
          )
        )
    );
  }
}
