import {Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {map} from 'rxjs/operators';
import {ProjectReportDTO} from '@cat/api';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-project-verification-report',
  templateUrl: './project-verification-report.component.html',
  styleUrls: ['./project-verification-report.component.scss']
})
export class ProjectVerificationReportComponent {

  Alert = Alert;
  data$: Observable<{
    projectReport: ProjectReportDTO,
  }>;
  error$ = new BehaviorSubject<APIError | null>(null);

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: RoutingService,
    private projectReportDetailStore: ProjectReportDetailPageStore,
  ) {
    this.data$ = combineLatest([
      this.projectReportDetailStore.projectReport$
    ]).pipe(
      map(([projectReport]) => ({
        projectReport
      }))
    );
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

  public redirectToReport(reportId: number): void {
    this.router.navigate([`../../${reportId}/identification`], {
      relativeTo: this.activatedRoute,
      queryParamsHandling: 'merge'
    });
  }
}
