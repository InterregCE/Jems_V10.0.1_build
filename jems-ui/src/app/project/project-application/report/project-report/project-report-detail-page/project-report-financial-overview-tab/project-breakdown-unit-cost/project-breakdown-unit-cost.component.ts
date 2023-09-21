import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  CertificateUnitCostBreakdownDTO,
  CertificateUnitCostBreakdownLineDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import { Alert } from '@common/components/forms/alert';

@Component({
  selector: 'jems-project-breakdown-unit-cost',
  templateUrl: './project-breakdown-unit-cost.component.html',
  styleUrls: ['./project-breakdown-unit-cost.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectBreakdownUnitCostComponent implements OnChanges {
  Alert = Alert;
  columnsAvailable = ['name', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalReportedSoFar', 'totalReportedSoFarPercentage', 'previouslyVerified', 'currentVerified', 'remainingBudget'];
  verifiedColumns = ['currentVerified'];
  displayedColumns = this.columnsAvailable;

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  @Input()
  breakdown: CertificateUnitCostBreakdownDTO;

  @Input()
  isVerified = false;


  dataSource: MatTableDataSource<CertificateUnitCostBreakdownLineDTO> = new MatTableDataSource([]);

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.unitCosts;
    this.displayedColumns = [...this.columnsAvailable]
        .filter(column => this.isVerified || !this.verifiedColumns.includes(column));
  }
}
