import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {ProjectReportDTO} from '@cat/api';

@Component({
  selector: 'jems-project-report-detail-page',
  templateUrl: './project-report-detail-page.component.html',
  styleUrls: ['./project-report-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportDetailPageComponent {

  ProjectReportDTO = ProjectReportDTO;
  TypeEnum = ProjectReportDTO.TypeEnum;
  constructor(private activatedRoute: ActivatedRoute,
              public pageStore: ProjectReportDetailPageStore,
              private router: RoutingService,
              private projectSidenavService: ProjectApplicationFormSidenavService,) { }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }
}
