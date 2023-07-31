import {Injectable} from "@angular/core";
import {combineLatest, Observable} from "rxjs";
import {map} from "rxjs/operators";
import {
  ProjectReportDetailPageStore
} from "@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service";
import {
  ProjectReportPageStore
} from "@project/project-application/report/project-report/project-report-page-store.service";
import {ProjectReportDTO} from "@cat/api";

@Injectable({providedIn: 'root'})
export class ProjectVerificationReportStore {

  projectReportVerificationEditable$: Observable<boolean>;

  constructor(
    public reportDetailPageStore: ProjectReportDetailPageStore,
    private projectReportPageStore: ProjectReportPageStore,
  ) {
    this.projectReportVerificationEditable$ = this.projectReportVerificationEditable();
  }

  projectReportVerificationEditable(): Observable<boolean> {
    return combineLatest([
      this.projectReportPageStore.userCanEditVerification$,
      this.reportDetailPageStore.projectReport$
    ])
    .pipe(
      map(([canEdit, projectReport]) => canEdit && (projectReport.status == ProjectReportDTO.StatusEnum.InVerification))
    );
  }

}
