import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ProjectReportSummaryDTO} from '@cat/api';

@Component({
  selector: 'jems-project-report-status',
  templateUrl: './project-report-status.component.html',
  styleUrls: ['./project-report-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectReportStatusComponent implements OnChanges{

  statuses: StatusChip[] = [];

  @Input()
  status: ProjectReportSummaryDTO.StatusEnum;

  @Input()
  longVersion = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.status) {
      this.statuses = ProjectReportStatusComponent.getStatusesOrdered(this.status);
      if (!this.longVersion) {
        this.statuses = this.statuses.filter(x => x.enabled).slice(-1);
      }
    }
  }

  private static getIconFromStatus(currentChip: ProjectReportSummaryDTO.StatusEnum): string {
    switch (currentChip) {
      case 'Draft':
        return 'donut_large';
      case 'ReOpenSubmittedLast':
      case 'ReOpenSubmittedLimited':
      case 'VerificationReOpenedLast':
      case 'VerificationReOpenedLimited':
      case 'ReOpenFinalized':
        return 'undo';
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

  private static getStatusesOrdered(currentStatus: ProjectReportSummaryDTO.StatusEnum): StatusChip[] {
    const isSubmissionReopened = [
      ProjectReportSummaryDTO.StatusEnum.ReOpenSubmittedLast,
      ProjectReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited,
    ].includes(currentStatus);

    const isVerificationReopened = [
      ProjectReportSummaryDTO.StatusEnum.VerificationReOpenedLast,
      ProjectReportSummaryDTO.StatusEnum.VerificationReOpenedLimited,
    ].includes(currentStatus);

    const isCertifiedReopened = [
      ProjectReportSummaryDTO.StatusEnum.ReOpenFinalized
    ].includes(currentStatus);

    return [
      {
        status: isSubmissionReopened ? currentStatus : ProjectReportSummaryDTO.StatusEnum.Draft,
        joiningIcon: '',
        enabled: this.isEnabled(currentStatus, 0),
        icon: this.getIconFromStatus(isSubmissionReopened ? currentStatus : ProjectReportSummaryDTO.StatusEnum.Draft),
      },
      {
        status: isVerificationReopened ? currentStatus : ProjectReportSummaryDTO.StatusEnum.Submitted,
        joiningIcon: isSubmissionReopened ? 'sync_alt' : 'trending_flat',
        enabled: this.isEnabled(currentStatus, 1),
        icon: this.getIconFromStatus(isVerificationReopened ? currentStatus : ProjectReportSummaryDTO.StatusEnum.Submitted),
      },
      {
        status: isCertifiedReopened ? currentStatus : ProjectReportSummaryDTO.StatusEnum.InVerification,
        joiningIcon: isVerificationReopened ? 'sync_alt' : 'trending_flat',
        enabled: this.isEnabled(currentStatus, 2),
        icon: this.getIconFromStatus(isCertifiedReopened ? currentStatus : ProjectReportSummaryDTO.StatusEnum.InVerification),
      },
      {
        status: ProjectReportSummaryDTO.StatusEnum.Finalized,
        joiningIcon: isCertifiedReopened ? 'sync_alt' : 'trending_flat',
        enabled: this.isEnabled(currentStatus, 3),
        icon: this.getIconFromStatus(ProjectReportSummaryDTO.StatusEnum.Finalized),
      },
    ];
  }

  private static isEnabled(
    currentChip: ProjectReportSummaryDTO.StatusEnum,
    chipIndex: number,
  ): boolean {
    switch (chipIndex) {
      case 0: return true;
      case 1: return ![
        ProjectReportSummaryDTO.StatusEnum.Draft,
        ProjectReportSummaryDTO.StatusEnum.ReOpenSubmittedLast,
        ProjectReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited,
      ].includes(currentChip);
      case 2: return [
        ProjectReportSummaryDTO.StatusEnum.InVerification,
        ProjectReportSummaryDTO.StatusEnum.Finalized,
        ProjectReportSummaryDTO.StatusEnum.ReOpenFinalized
      ].includes(currentChip);
      case 3: return currentChip === ProjectReportSummaryDTO.StatusEnum.Finalized;
      default: return false;
    }
  }

}

export interface StatusChip {
  status: ProjectReportSummaryDTO.StatusEnum;
  joiningIcon: string;
  icon: string;
  enabled: boolean;
}
