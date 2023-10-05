import {Component, Input} from '@angular/core';
import {AuditControlDTO, ProjectReportSummaryDTO} from '@cat/api';

@Component({
  selector: 'jems-audit-control-status',
  templateUrl: './audit-control-status.component.html',
  styleUrls: ['./audit-control-status.component.scss']
})
export class AuditControlStatusComponent {

  statuses: StatusChip[] = [
    {
      status: AuditControlDTO.StatusEnum.Ongoing,
      icon: 'policy'
    },
    {
      status: AuditControlDTO.StatusEnum.Closed,
      icon: 'gpp_good'
    },
  ];

  @Input()
  status: ProjectReportSummaryDTO.StatusEnum;

}

export interface StatusChip {
  status: AuditControlDTO.StatusEnum;
  icon: string;
}
