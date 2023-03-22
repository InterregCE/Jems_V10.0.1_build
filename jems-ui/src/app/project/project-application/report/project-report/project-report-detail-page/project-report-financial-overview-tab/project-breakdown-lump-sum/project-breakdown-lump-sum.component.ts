import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {
  CertificateLumpSumBreakdownDTO, CertificateLumpSumBreakdownLineDTO,
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import { Alert } from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-project-breakdown-lump-sum',
  templateUrl: './project-breakdown-lump-sum.component.html',
  styleUrls: ['./project-breakdown-lump-sum.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectBreakdownLumpSumComponent implements OnChanges {
  Alert = Alert;
  columnsAvailable = ['name', 'totalEligibleBudget', 'previouslyReported', 'currentReport',
    'totalReportedSoFar', 'totalReportedSoFarPercentage', 'remainingBudget', 'previouslyPaid'];
  displayedColumns = this.columnsAvailable;

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  @Input()
  breakdown: CertificateLumpSumBreakdownDTO;

  @Input()
  isCertified = false;

  dataSource: MatTableDataSource<CertificateLumpSumBreakdownLineDTO> = new MatTableDataSource([]);

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.lumpSums;
    this.displayedColumns = [...this.columnsAvailable];
  }

}
