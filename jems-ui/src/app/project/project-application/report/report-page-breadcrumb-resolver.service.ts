import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {TranslateService} from '@ngx-translate/core';
import {map} from 'rxjs/operators';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@Injectable({providedIn: 'root'})
export class ReportPageBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private projectPartnerStore: ProjectPartnerStore,
              private partnerReportPageStore: PartnerReportPageStore,
              private translateService: TranslateService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(
      combineLatest([this.partnerReportPageStore.partnerSummary$, this.projectPartnerStore.projectCallType$])
        .pipe(
          map(([partner, callType]) =>
            this.translateService.instant(
              ProjectPartnerStore.getPartnerTranslationKey(partner.role, callType),
              {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}))
        )
    );
  }
}
