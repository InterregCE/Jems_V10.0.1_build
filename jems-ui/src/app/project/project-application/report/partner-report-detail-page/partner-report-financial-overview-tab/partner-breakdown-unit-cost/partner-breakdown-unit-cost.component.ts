import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ExpenditureUnitCostBreakdownDTO, ExpenditureUnitCostBreakdownLineDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-partner-breakdown-unit-cost',
  templateUrl: './partner-breakdown-unit-cost.component.html',
  styleUrls: ['./partner-breakdown-unit-cost.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerBreakdownUnitCostComponent implements OnChanges {
  Alert = Alert;

  certifiedColumns = ['totalEligibleAfterControl'];
  columnsAvailable = [
    'name',
    'totalEligibleBudget',
    'previouslyReported',
    'currentReport',
    'totalReportedSoFar',
    'totalReportedSoFarPercentage',
    'remainingBudget',
    'previouslyValidated',
    'totalEligibleAfterControl',
  ];
  displayedColumns = this.columnsAvailable;

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  @Input()
  breakdown: ExpenditureUnitCostBreakdownDTO;

  @Input()
  isCertified = false;

  dataSource: MatTableDataSource<ExpenditureUnitCostBreakdownLineDTO> = new MatTableDataSource([]);

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.unitCosts;
    this.displayedColumns = [...this.columnsAvailable]
      .filter(column => this.isCertified || !this.certifiedColumns.includes(column));
  }

}
