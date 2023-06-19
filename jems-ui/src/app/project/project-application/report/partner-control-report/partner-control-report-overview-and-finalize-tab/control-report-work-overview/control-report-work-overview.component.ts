import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ControlWorkOverviewDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-control-report-work-overview',
  templateUrl: './control-report-work-overview.component.html',
  styleUrls: ['./control-report-work-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControlReportWorkOverviewComponent implements OnChanges {

  displayedColumns = ['declaredByPartner', 'inControlSample', 'inControlSamplePercentage', 'parked', 'deductedByControl', 'eligibleAfterControl', 'eligibleAfterControlPercentage'];
  dataSource = new MatTableDataSource<ControlWorkOverviewDTO>([]);

  @Input()
  overviewData: ControlWorkOverviewDTO;

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = [this.overviewData];
  }

}
