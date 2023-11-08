import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';

@Injectable({providedIn: 'root'})
export class AuditControlCorrectionDetailPageBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private translateService: TranslateService,
              private pageStore: AuditControlCorrectionDetailPageStore) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.pageStore.correction$.pipe(
      map(correction => this.translateService.instant('project.application.reporting.corrections.audit.control.correction.title',
        {auditControlNumber: correction?.auditControlNumber, correctionNumber: correction?.orderNr}))
    ));
  }

}
