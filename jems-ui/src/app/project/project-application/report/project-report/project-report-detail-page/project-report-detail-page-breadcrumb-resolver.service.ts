import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';
import {map} from 'rxjs/operators';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class ProjectReportDetailPageBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private translationService: TranslateService,
              private projectReportDetailPageStore: ProjectReportDetailPageStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.projectReportDetailPageStore.projectReport$
      .pipe(
        map(report =>
          this.translationService.instant(
            `project.application.project.reports.title.number`, {reportNumber: `${report.reportNumber}`}))
      )
    );
  }

}
