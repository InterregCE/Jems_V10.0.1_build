import {Component, Input, OnChanges} from '@angular/core';
import {PaymentAccountAmountSummaryDTO, PaymentAccountAmountSummaryLineDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-accounts-cumulative-table',
  templateUrl: './accounts-cumulative-table.component.html',
  styleUrls: ['./accounts-cumulative-table.component.scss']
})
export class AccountsCumulativeTableComponent implements OnChanges{

  @Input()
  data: PaymentAccountAmountSummaryDTO;
  @Input()
  infoBubble: 'correction' | 'summary' = 'correction';



  dataSource: MatTableDataSource<PaymentAccountAmountSummaryLineDTO> = new MatTableDataSource([]);
  totalEligibleInfo: string;
  totalPublicInfo: string;

  displayedColumns = ['priorityAxis', 'totalEligibleExpenditure', 'totalPublicContribution'];

  ngOnChanges(): void {
    this.dataSource.data = this.data.amountsGroupedByPriority;
    this.totalEligibleInfo = this.infoBubble == 'correction'
      ? 'payments.accounts.corrections.overview.amounts.total.eligible.expenditure.info.bubble'
      : 'payments.accounts.detail.summary.tab.amounts.summary.total.eligible.expenditure.info.bubble';
    this.totalPublicInfo = this.infoBubble == 'correction'
      ? 'payments.accounts.corrections.overview.amounts.total.public.contribution.info.bubble'
      : 'payments.accounts.detail.summary.tab.amounts.summary.public.contribution.info.bubble';
  }
}
