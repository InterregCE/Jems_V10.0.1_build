import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ActivatedRoute} from '@angular/router';
import {
  PartnerReportProcurementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurement-detail',
  templateUrl: './partner-report-procurement-detail.component.html',
  styleUrls: ['./partner-report-procurement-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportProcurementDetailComponent {

  procurementId = Number(this.activatedRoute?.snapshot?.params?.procurementId);

  constructor(
    private activatedRoute: ActivatedRoute,
    private procurementStore: PartnerReportProcurementStore,
  ) {
    this.procurementStore.procurementId$.next(this.procurementId);
  }

}
