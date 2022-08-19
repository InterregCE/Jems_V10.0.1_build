import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectPartnerReportSummaryDTO} from '@cat/api';

@Component({
  selector: 'jems-partner-report-status',
  templateUrl: './partner-report-status.component.html',
  styleUrls: ['./partner-report-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportStatusComponent {

  statusesOrdered = [
    ProjectPartnerReportSummaryDTO.StatusEnum.Draft,
    ProjectPartnerReportSummaryDTO.StatusEnum.Submitted,
    ProjectPartnerReportSummaryDTO.StatusEnum.InControl,
    ProjectPartnerReportSummaryDTO.StatusEnum.Certified,
  ];

  @Input()
  status: ProjectPartnerReportSummaryDTO.StatusEnum;

  @Input()
  longVersion = false;

  getIconFromStatus(currentChip: ProjectPartnerReportSummaryDTO.StatusEnum): string {
    if (!this.isEnabled(currentChip)) {
      return 'cancel';
    }
    switch (currentChip) {
      case 'Draft':
        return 'donut_large';
      case 'Submitted':
        return 'check_circle';
      case 'InControl':
        return 'checklist';
      case 'Certified':
        return 'workspace_premium';
      default:
        return 'help';
    }
  }

  isEnabled(currentChip: ProjectPartnerReportSummaryDTO.StatusEnum): boolean {
    return this.statusesOrdered.indexOf(currentChip) <= this.statusesOrdered.indexOf(this.status);
  }

}
