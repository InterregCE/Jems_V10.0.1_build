import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {PaymentAccountAmountSummaryDTO, PaymentAccountAmountSummaryLineDTO, PaymentToEcAmountSummaryDTO, PaymentToEcAmountSummaryLineDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-accounts-correction-cumulative-table',
  templateUrl: './accounts-correction-cumulative-table.component.html',
  styleUrls: ['./accounts-correction-cumulative-table.component.scss']
})
export class AccountsCorrectionCumulativeTableComponent implements OnChanges{

  @Input()
  data: PaymentAccountAmountSummaryDTO;

  dataSource: MatTableDataSource<PaymentAccountAmountSummaryLineDTO> = new MatTableDataSource([]);

  displayedColumns = ['priorityAxis', 'totalEligibleExpenditure', 'totalPublicContribution'];

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.data.amountsGroupedByPriority;
  }
}
