import {Component} from '@angular/core';
import {ProjectReportOutputLineOverviewDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {NumberService} from '@common/services/number.service';
import {
  ProjectReportIdentificationExtensionStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-identification-tab/project-report-identification-extension/project-report-identification-extension-store.service';

@Component({
  selector: 'jems-project-report-identification-extension-result-output',
  templateUrl: './project-report-identification-extension-result-output.component.html',
  styleUrls: ['./project-report-identification-extension-result-output.component.scss']
})
export class ProjectReportIdentificationExtensionResultOutputComponent {

  displayedColumns: string[] = ['identifier', 'measurementUnit', 'baseline', 'targetValue', 'previouslyReported', 'currentReport', 'totalReportedSoFar'];

  constructor(readonly pageStore: ProjectReportIdentificationExtensionStore) {}

  getDataSource(lines: ProjectReportOutputLineOverviewDTO[]) {
    return new MatTableDataSource<ProjectReportOutputLineOverviewDTO>(lines);
  }

  add(previouslyReported: number, currentReport: number) {
    return NumberService.sum([previouslyReported, currentReport]);
  }
}
