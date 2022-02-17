import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  ProjectPartnerReportPageStore
} from '@project/project-application/report/project-partner-report-page-store.service';

@Component({
  selector: 'jems-partner-report-detail-page',
  templateUrl: './partner-report-detail-page.component.html',
  styleUrls: ['./partner-report-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportDetailPageComponent {

  constructor(private activatedRoute: ActivatedRoute,
              public projectPartnerReportPageStore: ProjectPartnerReportPageStore,
              private router: RoutingService,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

}
