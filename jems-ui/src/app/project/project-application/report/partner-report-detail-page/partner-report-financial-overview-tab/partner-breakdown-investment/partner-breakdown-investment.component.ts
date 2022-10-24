import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {
ExpenditureInvestmentBreakdownDTO, ExpenditureInvestmentBreakdownLineDTO,
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@UntilDestroy()
@Component({
  selector: 'jems-partner-breakdown-investment',
  templateUrl: './partner-breakdown-investment.component.html',
  styleUrls: ['./partner-breakdown-investment.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerBreakdownInvestmentComponent implements OnChanges {

  @Input()
  breakdown: ExpenditureInvestmentBreakdownDTO;

  dataSource: MatTableDataSource<ExpenditureInvestmentBreakdownLineDTO> = new MatTableDataSource([]);
  displayedColumns = ['investmentNr', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalReportedSoFar', 'totalReportedSoFarPercentage', 'remainingBudget'];

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.investments;
  }

}
