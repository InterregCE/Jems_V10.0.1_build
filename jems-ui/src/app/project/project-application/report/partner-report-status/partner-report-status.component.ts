import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ProjectPartnerReportSummaryDTO} from '@cat/api';

@Component({
  selector: 'jems-partner-report-status',
  templateUrl: './partner-report-status.component.html',
  styleUrls: ['./partner-report-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportStatusComponent implements OnChanges {

  statuses: StatusChip[] = [];

  @Input()
  status: ProjectPartnerReportSummaryDTO.StatusEnum;

  @Input()
  longVersion = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.status) {
      this.statuses = PartnerReportStatusComponent.getStatusesOrdered(this.status);
      if (!this.longVersion) {
        this.statuses = this.statuses.filter(x => x.enabled).slice(-1);
      }
    }
  }

  private static getIconFromStatus(currentChip: ProjectPartnerReportSummaryDTO.StatusEnum): string {
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

  private static getStatusesOrdered(currentStatus: ProjectPartnerReportSummaryDTO.StatusEnum): StatusChip[] {
    const isSubmissionReopened = [
      ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLast,
      ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited,
    ].includes(currentStatus);

    const isControlReopened = [
      ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLast,
      ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenInControlLimited,
    ].includes(currentStatus);

    return [
      {
        status: isSubmissionReopened ? currentStatus : ProjectPartnerReportSummaryDTO.StatusEnum.Draft,
        joiningIcon: '',
        enabled: this.isEnabled(currentStatus, 0),
        icon: this.getIconFromStatus(isSubmissionReopened ? currentStatus : ProjectPartnerReportSummaryDTO.StatusEnum.Draft),
      },
      {
        status: isControlReopened ? currentStatus : ProjectPartnerReportSummaryDTO.StatusEnum.Submitted,
        joiningIcon: isSubmissionReopened ? 'sync_alt' : 'trending_flat',
        enabled: this.isEnabled(currentStatus, 1),
        icon: this.getIconFromStatus(isControlReopened ? currentStatus : ProjectPartnerReportSummaryDTO.StatusEnum.Submitted),
      },
      {
        status: ProjectPartnerReportSummaryDTO.StatusEnum.InControl,
        joiningIcon: isControlReopened ? 'sync_alt' : 'trending_flat',
        enabled: this.isEnabled(currentStatus, 2),
        icon: this.getIconFromStatus(ProjectPartnerReportSummaryDTO.StatusEnum.InControl),
      },
      {
        status: ProjectPartnerReportSummaryDTO.StatusEnum.Certified,
        joiningIcon: 'trending_flat',
        enabled: this.isEnabled(currentStatus, 3),
        icon: this.getIconFromStatus(ProjectPartnerReportSummaryDTO.StatusEnum.Certified),
      },
    ];
  }

  private static isEnabled(
    currentChip: ProjectPartnerReportSummaryDTO.StatusEnum,
    chipIndex: number,
  ): boolean {
    switch (chipIndex) {
      case 0: return true;
      case 1: return ![
        ProjectPartnerReportSummaryDTO.StatusEnum.Draft,
        ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLast,
        ProjectPartnerReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited,
      ].includes(currentChip);
      case 2: return [
        ProjectPartnerReportSummaryDTO.StatusEnum.InControl,
        ProjectPartnerReportSummaryDTO.StatusEnum.Certified,
      ].includes(currentChip);
      case 3: return currentChip === ProjectPartnerReportSummaryDTO.StatusEnum.Certified;
      default: return false;
    }
  }

}

export interface StatusChip {
  status: ProjectPartnerReportSummaryDTO.StatusEnum;
  joiningIcon: string;
  icon: string;
  enabled: boolean;
}
