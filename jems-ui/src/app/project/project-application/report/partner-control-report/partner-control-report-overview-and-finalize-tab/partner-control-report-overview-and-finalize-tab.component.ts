import {Component} from '@angular/core';
import {ControlWorkOverviewDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {
  PartnerControlReportOverviewAndFinalizeStore
} from '@project/project-application/report/partner-control-report/partner-control-report-overview-and-finalize-tab/partner-control-report-overview-and-finalize.store';

@Component({
  selector: 'jems-partner-control-report-overview-and-finalize-tab',
  templateUrl: './partner-control-report-overview-and-finalize-tab.component.html',
  styleUrls: ['./partner-control-report-overview-and-finalize-tab.component.scss']
})
export class PartnerControlReportOverviewAndFinalizeTabComponent {

  displayedColumns = ['declaredByPartner', 'inControlSample', 'parked', 'deductedByControl', 'eligibleAfterControl', 'eligibleAfterControlPercentage'];
  dataSource$: Observable<MatTableDataSource<ControlWorkOverviewDTO>>;

  constructor(private pageStore: PartnerControlReportOverviewAndFinalizeStore,) {
    this.dataSource$ = this.pageStore.controlWorkOverview$.pipe(
        map(data => new MatTableDataSource([data]))
    );
  }

}
