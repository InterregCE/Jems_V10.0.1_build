import {Component, Input} from '@angular/core';
import {ProjectAuditControlCorrectionDTO} from '@cat/api';

@Component({
  selector: 'jems-audit-control-correction-status',
  templateUrl: './audit-control-correction-status.component.html',
  styleUrls: ['./audit-control-correction-status.component.scss']
})
export class AuditControlCorrectionStatusComponent {

  statuses: StatusChip[] = [
    {
      status: ProjectAuditControlCorrectionDTO.StatusEnum.Ongoing,
      icon: 'policy'
    },
    {
      status: ProjectAuditControlCorrectionDTO.StatusEnum.Closed,
      icon: 'gpp_good'
    },
  ];

  @Input()
  status: ProjectAuditControlCorrectionDTO.StatusEnum;

}

export interface StatusChip {
  status: ProjectAuditControlCorrectionDTO.StatusEnum;
  icon: string;
}
