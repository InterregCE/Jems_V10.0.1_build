import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {ProjectPartnerReportWorkPackageDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {
  PartnerReportWorkPlanPageStore
} from '@project/project-application/report/partner-report-work-plan-progress-tab/partner-report-work-plan-page-store.service';

@Component({
  selector: 'jems-partner-report-detail-page',
  templateUrl: './partner-report-detail-page.component.html',
  styleUrls: ['./partner-report-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportDetailPageComponent {

  data$: Observable<{
    workPackages: ProjectPartnerReportWorkPackageDTO[];
  }>;

  constructor(private activatedRoute: ActivatedRoute,
              public pageStore: PartnerReportDetailPageStore,
              private router: RoutingService,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              private partnerReportWorkPlanPageStore: PartnerReportWorkPlanPageStore) {

    this.data$ = combineLatest([
      this.partnerReportWorkPlanPageStore.partnerWorkPackages$,
    ])
      .pipe(
        map(([workPackages]) => ({
          workPackages
        })),
      );
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

}
