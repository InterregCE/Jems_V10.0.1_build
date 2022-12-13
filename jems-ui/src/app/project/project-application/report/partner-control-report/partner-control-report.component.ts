import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerReportDTO, ProjectPartnerSummaryDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@Component({
  selector: 'jems-partner-control-report',
  templateUrl: './partner-control-report.component.html',
  styleUrls: ['./partner-control-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportComponent {

  data$: Observable<{
    partner: ProjectPartnerSummaryDTO;
    report: ProjectPartnerReportDTO;
    allTabsVisible: boolean;
    partnerName: string;
    projectAcronym: string;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: RoutingService,
    private store: PartnerControlReportStore,
    private pageStore: PartnerReportDetailPageStore,
    private projectStore: ProjectStore,
    private partnerReportPageStore: PartnerReportPageStore,
    private projectSidenavService: ProjectApplicationFormSidenavService,
  ) {
    this.data$ = combineLatest([
      pageStore.partnerSummary$,
      pageStore.partnerReport$,
      projectStore.project$,
      partnerReportPageStore.institutionUserCanViewControlReports$,
      partnerReportPageStore.userCanViewReport$,
    ]).pipe(
      map(([partner, report, project, controllerCanView, collaboratorCanView]) => ({
        partner,
        report,
        allTabsVisible: controllerCanView || (collaboratorCanView && report.status === ProjectPartnerReportDTO.StatusEnum.Certified),
        partnerName: `${report.identification?.partnerNumber} ${partner.abbreviation}`,
        projectAcronym: project.acronym,
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
