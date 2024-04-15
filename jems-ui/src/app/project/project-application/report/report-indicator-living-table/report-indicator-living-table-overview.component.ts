import {ChangeDetectionStrategy, Component} from '@angular/core';
import {OutputRowWithCurrentDTO, PageAdvancePaymentDTO, ProjectReportOutputLineOverviewDTO} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Observable} from 'rxjs';
import {
  ReportIndicatorLivingTablePageStore
} from '@project/project-application/report/report-indicator-living-table/report-indicator-living-table-page.store';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-indicator-living-table',
  templateUrl: './report-indicator-living-table-overview.component.html',
  styleUrls: ['./report-indicator-living-table-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportIndicatorLivingTableOverviewComponent {

  displayedColumns: string[] = ['identifier', 'measurementUnit', 'baseline', 'targetValue', 'currentReport'];

  data$: Observable<{
    page: PageAdvancePaymentDTO;
    tableConfiguration: TableConfiguration;
  }>;

  tableConfiguration: TableConfiguration;

  constructor(public pageStore: ReportIndicatorLivingTablePageStore) {}

  getDataSource(lines: OutputRowWithCurrentDTO[]) {
    return new MatTableDataSource<OutputRowWithCurrentDTO>(lines);
  }

}
