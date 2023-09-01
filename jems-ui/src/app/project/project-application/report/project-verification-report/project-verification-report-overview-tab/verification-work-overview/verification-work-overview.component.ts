import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {MatTableDataSource} from '@angular/material/table';
import {
  VerificationWorkOverviewDTO,
  VerificationWorkOverviewLineDTO
} from '@cat/api';

@UntilDestroy()
@Component({
  selector: 'jems-verification-work-overview',
  templateUrl: './verification-work-overview.component.html',
  styleUrls: ['./verification-work-overview.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationWorkOverviewComponent implements OnChanges {

  displayedColumns = [
    'partnerReport',
    'partner',
    'requestedByPartner',
    'inVerificationSample',
    'inVerificationSamplePercentage',
    'parked',
    'deductedByJs',
    'deductedByMa',
    'deducted',
    'afterVerification',
    'afterVerificationPercentage',
  ];

  dataSource: MatTableDataSource<VerificationWorkOverviewLineDTO> = new MatTableDataSource([]);

  @Input()
  overview: VerificationWorkOverviewDTO;

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.overview.certificates;
  }

}
