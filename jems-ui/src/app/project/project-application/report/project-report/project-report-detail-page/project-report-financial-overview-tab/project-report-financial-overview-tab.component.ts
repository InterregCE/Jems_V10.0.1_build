import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CallFundRateDTO, CertificateCoFinancingBreakdownDTO
} from '@cat/api';
import {map} from 'rxjs/operators';
import {
  ProjectReportFinancialOverviewStoreService
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-financial-overview-tab/project-report-financial-overview-store.service';

@Component({
  selector: 'jems-project-report-financial-overview-tab',
  templateUrl: './project-report-financial-overview-tab.component.html',
  styleUrls: ['./project-report-financial-overview-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportFinancialOverviewTabComponent {

  data$: Observable<{
    perCoFinancing: CertificateCoFinancingBreakdownDTO;
    funds: CallFundRateDTO[];
  }>;

  constructor(
    private financialOverviewStore: ProjectReportFinancialOverviewStoreService,
  ) {
    this.data$ = combineLatest([
      financialOverviewStore.perCoFinancing$,
      financialOverviewStore.callFunds$,
    ]).pipe(
      map(([perCoFinancing, funds]: any) => ({
        perCoFinancing,
        funds,
      })),
    );
  }
}
