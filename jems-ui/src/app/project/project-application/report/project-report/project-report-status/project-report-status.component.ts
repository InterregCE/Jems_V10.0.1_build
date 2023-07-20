import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectReportSummaryDTO} from '@cat/api';

@Component({
  selector: 'jems-project-report-status',
  templateUrl: './project-report-status.component.html',
  styleUrls: ['./project-report-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectReportStatusComponent {

  statusesOrdered = [
    ProjectReportSummaryDTO.StatusEnum.Draft,
    ProjectReportSummaryDTO.StatusEnum.Submitted,
    ProjectReportSummaryDTO.StatusEnum.InVerification,
    ProjectReportSummaryDTO.StatusEnum.Finalized,
  ];

  @Input()
  status: ProjectReportSummaryDTO.StatusEnum;

  @Input()
  longVersion = false;

  getIconFromStatus(currentChip: ProjectReportSummaryDTO.StatusEnum): string {
    switch (currentChip) {
      case 'Draft':
        return 'donut_large';
      case 'Submitted':
        return 'send';
      case 'InVerification':
        return 'checklist';
      case 'Finalized':
        return 'workspace_premium';
      default:
        return 'help';
    }
  }

  isEnabled(currentChip: ProjectReportSummaryDTO.StatusEnum): boolean {
    return this.statusesOrdered.indexOf(currentChip) <= this.statusesOrdered.indexOf(this.status);
  }

}
