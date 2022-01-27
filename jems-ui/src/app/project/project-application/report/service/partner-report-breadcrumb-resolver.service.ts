import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {map} from 'rxjs/operators';
import {ProjectPartnerReportPageStore} from '@project/project-application/report/project-partner-report-page-store.service';

@Injectable()
export class PartnerReportBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private projectPartnerStore: ProjectPartnerStore,
              private translationService: TranslateService,
              private projectPartnerReportPageStore: ProjectPartnerReportPageStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(
      combineLatest([this.projectPartnerReportPageStore.partnerReportSummary$, this.projectPartnerStore.projectCallType$])
        .pipe(
          map(([partner, callType]) =>
            this.translationService.instant(
              ProjectPartnerStore.getPartnerTranslationKey(partner.role, callType),
              {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}))
        )
    );
  }
}
