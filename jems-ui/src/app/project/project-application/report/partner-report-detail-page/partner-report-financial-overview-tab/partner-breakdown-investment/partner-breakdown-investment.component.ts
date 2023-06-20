import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {Alert} from '@common/components/forms/alert';
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
  Alert = Alert;

  @Input()
  breakdown: ExpenditureInvestmentBreakdownDTO;

  @Input()
  isCertified = false;

  dataSource: MatTableDataSource<ExpenditureInvestmentBreakdownLineDTO> = new MatTableDataSource([]);
  certifiedColumns = ['totalEligibleAfterControl'];
  columnsAvailable = ['investmentNr', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalEligibleAfterControl', 'totalReportedSoFar',
    'totalReportedSoFarPercentage', 'remainingBudget', 'previouslyValidated'];
  displayedColumns = this.columnsAvailable;

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.investments;
    this.displayedColumns = [...this.columnsAvailable]
      .filter(column => this.isCertified || !this.certifiedColumns.includes(column));
  }

}
