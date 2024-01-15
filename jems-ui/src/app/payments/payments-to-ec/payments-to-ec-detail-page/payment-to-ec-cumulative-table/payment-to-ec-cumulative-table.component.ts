import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {PaymentToEcAmountSummaryDTO, PaymentToEcAmountSummaryLineDTO} from '@cat/api';

@Component({
  selector: 'jems-payment-to-ec-cumulative-table',
  templateUrl: './payment-to-ec-cumulative-table.component.html',
  styleUrls: ['./payment-to-ec-cumulative-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentToEcCumulativeTableComponent implements OnChanges{

  @Input()
  data: PaymentToEcAmountSummaryDTO;

  dataSource: MatTableDataSource<PaymentToEcAmountSummaryLineDTO> = new MatTableDataSource([]);

  displayedColumns = ['priorityAxis', 'totalEligibleExpenditure', 'totalUnionContribution', 'totalPublicContribution'];

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.data.amountsGroupedByPriority;
  }
}
