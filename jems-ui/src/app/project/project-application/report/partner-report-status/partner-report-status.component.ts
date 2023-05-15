import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectPartnerReportSummaryDTO} from '@cat/api';

@Component({
  selector: 'jems-partner-report-status',
  templateUrl: './partner-report-status.component.html',
  styleUrls: ['./partner-report-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportStatusComponent {

  @Input()
  status: ProjectPartnerReportSummaryDTO.StatusEnum;

  @Input()
  longVersion = false;

  getIconFromStatus(currentChip: ProjectPartnerReportSummaryDTO.StatusEnum): string {
    switch (currentChip) {
      case 'Draft':
        return 'donut_large';
      case 'ReOpenSubmittedLast':
      case 'ReOpenSubmittedLimited':
      case 'ReOpenInControlLast':
      case 'ReOpenInControlLimited':
        return 'undo';
      case 'Submitted':
        return 'send';
      case 'InControl':
        return 'checklist';
      case 'Certified':
        return 'workspace_premium';
      default:
        return 'help';
    }
  }

  getStatusesOrdered(): ProjectPartnerReportSummaryDTO.StatusEnum[] {
    return [
      ProjectPartnerReportSummaryDTO.StatusEnum.Draft,
      ... this.status === ProjectPartnerReportSummaryDTO.StatusEnum.Submitted || (this.status !== ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLast && this.status !== ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited) ? [ProjectPartnerReportSummaryDTO.StatusEnum.Submitted] : [],
      ... this.status === ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLast ? [ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLast] : [],
      ... this.status === ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited ? [ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited] : [],
      ... this.status === ProjectPartnerReportSummaryDTO.StatusEnum.InControl || (this.status !== ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLast && this.status !== ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLimited) ? [ProjectPartnerReportSummaryDTO.StatusEnum.InControl] : [],
      ... this.status === ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLast ? [ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLast] : [],
      ... this.status === ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLimited ? [ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLimited] : [],
      ProjectPartnerReportSummaryDTO.StatusEnum.Certified,
    ];
  }

  isEnabled(currentChip: ProjectPartnerReportSummaryDTO.StatusEnum): boolean {
    return this.getStatusesOrdered().indexOf(currentChip) <= this.getStatusesOrdered().indexOf(this.status);
  }

}
