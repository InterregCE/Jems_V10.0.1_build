import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  CertificateInvestmentBreakdownDTO, CertificateInvestmentBreakdownLineDTO,
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import { Alert } from '@common/components/forms/alert';

@Component({
  selector: 'jems-project-breakdown-investment',
  templateUrl: './project-breakdown-investment.component.html',
  styleUrls: ['./project-breakdown-investment.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectBreakdownInvestmentComponent implements OnChanges {
  Alert = Alert;

  @Input()
  breakdown: CertificateInvestmentBreakdownDTO;

  dataSource: MatTableDataSource<CertificateInvestmentBreakdownLineDTO> = new MatTableDataSource([]);

  displayedColumns = ['investmentNr', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalReportedSoFar', 'totalReportedSoFarPercentage', 'remainingBudget'];

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.investments;
  }
}

