import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';
import {map} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';

@Injectable({providedIn: 'root'})
export class ReportCorrectionsAuditControlDetailPageBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private translateService: TranslateService,
              private pageStore: ReportCorrectionsAuditControlDetailPageStore) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.pageStore.auditControl$.pipe(
      map(auditControl => this.translateService.instant('project.application.reporting.corrections.audit.control.title',
        {projectIdentifier: auditControl?.projectCustomIdentifier, auditControlNumber: auditControl?.number}))
    ));
  }

}
