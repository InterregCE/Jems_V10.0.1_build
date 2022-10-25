import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ExpenditureLumpSumBreakdownDTO, ExpenditureLumpSumBreakdownLineDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-partner-breakdown-lump-sum',
  templateUrl: './partner-breakdown-lump-sum.component.html',
  styleUrls: ['./partner-breakdown-lump-sum.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerBreakdownLumpSumComponent implements OnChanges {
  Alert = Alert;

  displayedColumns = ['name', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalReportedSoFar',
    'totalReportedSoFarPercentage', 'remainingBudget', 'previouslyPaid'];

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  @Input()
  breakdown: ExpenditureLumpSumBreakdownDTO;

  dataSource: MatTableDataSource<ExpenditureLumpSumBreakdownLineDTO> = new MatTableDataSource([]);

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.lumpSums;
  }

}
