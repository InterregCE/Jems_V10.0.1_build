import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CertificateVerificationDeductionOverviewDTO,
  FinancingSourceBreakdownDTO,
  VerificationWorkOverviewDTO,
} from '@cat/api';
import {
  ProjectVerificationReportOverviewTabStoreService
} from '@project/project-application/report/project-verification-report/project-verification-report-overview-tab/project-verification-report-overview-tab-store.service';
import {map} from 'rxjs/operators';

@Component({
  selector: 'jems-project-verification-report-overview-tab',
  templateUrl: './project-verification-report-overview-tab.component.html',
  styleUrls: ['./project-verification-report-overview-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ProjectVerificationReportOverviewTabComponent {

  data$: Observable<{
    verificationWorkOverview: VerificationWorkOverviewDTO;
    financingSourceBreakdown: FinancingSourceBreakdownDTO;
    certificatesDeductionOverviews: CertificateVerificationDeductionOverviewDTO[];
  }>;

  constructor(
    private projectVerificationReportOverviewTabStoreService: ProjectVerificationReportOverviewTabStoreService
  ) {
    this.data$ = combineLatest([
      projectVerificationReportOverviewTabStoreService.verificationWorkOverview$,
      projectVerificationReportOverviewTabStoreService.financingSourceBreakdown$,
      projectVerificationReportOverviewTabStoreService.certificatesVerificationDeductionOverview$,
    ]).pipe(
      map(([verificationWorkOverview, financingSourceBreakdown, certificatesDeductionOverviews]: any) => ({
        verificationWorkOverview,
        financingSourceBreakdown,
        certificatesDeductionOverviews
      })),
    );
  }
}
