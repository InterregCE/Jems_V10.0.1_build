import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerReportDTO} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@Component({
  selector: 'jems-partner-control-report',
  templateUrl: './partner-control-report.component.html',
  styleUrls: ['./partner-control-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportComponent {

  data$: Observable<{
    report: ProjectPartnerReportDTO;
    allTabsVisible: boolean;
    partnerName: string;
    projectAcronym: string;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: RoutingService,
    private pageStore: PartnerReportDetailPageStore,
    private partnerReportPageStore: PartnerReportPageStore,
  ) {
    this.data$ = combineLatest([
      pageStore.partnerReport$,
      partnerReportPageStore.institutionUserCanViewControlReports$,
      partnerReportPageStore.userCanViewReport$,
    ]).pipe(
      map(([report, controllerCanView, collaboratorOrProgrammeUserCanView]) => ({
        report,
        allTabsVisible: controllerCanView || (collaboratorOrProgrammeUserCanView && report.status === ProjectPartnerReportDTO.StatusEnum.Certified),
        partnerName: `${report.identification?.partnerNumber} ${report.identification?.partnerAbbreviation}`,
        projectAcronym: report.identification?.projectAcronym,
      })),
      tap(data => {
        if (!data.allTabsVisible && !this.activeTab('document')) {
          this.routeTo('document');
        }
      }),
    );
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

}
