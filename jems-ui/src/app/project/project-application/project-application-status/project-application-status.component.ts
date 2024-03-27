import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';

@Component({
  selector: 'jems-project-application-status',
  templateUrl: './project-application-status.component.html',
  styleUrls: ['./project-application-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectApplicationStatusComponent implements OnChanges {

  statuses: StatusChip[] = [];

  @Input()
  projectDetail: ProjectDetailDTO;

  @Input()
  status: ProjectStatusDTO.StatusEnum;

  @Input()
  longVersion = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.status) {
      this.statuses = this.getStatusesOrdered(this.projectDetail);
      if (!this.longVersion) {
        this.statuses = this.statuses.filter(x => x.enabled).slice(-1);
      }
    }
  }

  private getIconFromStatus(currentChip: ProjectStatusDTO.StatusEnum): string {
    const iconForStatus = new Map<ProjectStatusDTO.StatusEnum, string>([
        [ProjectStatusDTO.StatusEnum.DRAFT, 'donut_large'],
        [ProjectStatusDTO.StatusEnum.STEP1DRAFT, 'donut_large'],
        [ProjectStatusDTO.StatusEnum.SUBMITTED, 'send'],
        [ProjectStatusDTO.StatusEnum.STEP1SUBMITTED, 'send'],
        [ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED, 'send'],
        [ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED, 'send'],
        [ProjectStatusDTO.StatusEnum.CONDITIONSSUBMITTED, 'send'],
        [ProjectStatusDTO.StatusEnum.ELIGIBLE, 'check'],
        [ProjectStatusDTO.StatusEnum.STEP1ELIGIBLE, 'check'],
        [ProjectStatusDTO.StatusEnum.INELIGIBLE, 'clear'],
        [ProjectStatusDTO.StatusEnum.STEP1INELIGIBLE, 'clear'],
        [ProjectStatusDTO.StatusEnum.NOTAPPROVED, 'clear'],
        [ProjectStatusDTO.StatusEnum.STEP1NOTAPPROVED, 'clear'],
        [ProjectStatusDTO.StatusEnum.APPROVED, 'fact_check'],
        [ProjectStatusDTO.StatusEnum.STEP1APPROVED, 'fact_check'],
        [ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS, 'pending'],
        [ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS, 'pending'],
        [ProjectStatusDTO.StatusEnum.INMODIFICATION, 'undo'],
        [ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING, 'undo'],
        [ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT, 'undo'],
        [ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS, 'undo'],
        [ProjectStatusDTO.StatusEnum.CONTRACTED, 'approval'],
        [ProjectStatusDTO.StatusEnum.CLOSED, 'hail']
      ]);
      const icon = iconForStatus.get(currentChip);
      return icon ? icon : 'donut_large';
  }

  private getStatusesOrdered(projectDetail: ProjectDetailDTO): StatusChip[] {

    const currentStatus: ProjectStatusDTO.StatusEnum = projectDetail.projectStatus.status;
    let flow: StatusChip[] = [];

    // 2-step call
    if (projectDetail.callSettings.endDateStep1) {
      flow = this.addStatusesForStep1(projectDetail, flow, currentStatus);
    }

    // normal flow
    flow.push(
      {
        status: ProjectStatusDTO.StatusEnum.DRAFT,
        joiningIcon: 'trending_flat',
        enabled: false,
        icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.DRAFT),
      });

    // returned to applicant?
    if (currentStatus == ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT) {
      this.addStatusesForReturnedToApplicant(projectDetail, flow, currentStatus);
    }
    else {
      flow.push({
          status: ProjectStatusDTO.StatusEnum.SUBMITTED,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.SUBMITTED),
        },
        {
          status: (currentStatus === ProjectStatusDTO.StatusEnum.INELIGIBLE) ? ProjectStatusDTO.StatusEnum.INELIGIBLE : ProjectStatusDTO.StatusEnum.ELIGIBLE,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus((currentStatus === ProjectStatusDTO.StatusEnum.INELIGIBLE) ? ProjectStatusDTO.StatusEnum.INELIGIBLE : ProjectStatusDTO.StatusEnum.ELIGIBLE),
        });
    }

    // approval status
    if (currentStatus == ProjectStatusDTO.StatusEnum.NOTAPPROVED) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.NOTAPPROVED,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.NOTAPPROVED),
        });
    }
    else if (currentStatus == ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS
             || currentStatus == ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS
             || currentStatus == ProjectStatusDTO.StatusEnum.CONDITIONSSUBMITTED
             || projectDetail.secondStepDecision?.finalFundingDecision?.status == ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS),
        });
    }
    else {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.APPROVED,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.APPROVED),
        });
    }

    // returned for conditions?
    if (currentStatus == ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS,
          joiningIcon: 'sync_alt',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS),
        });
    }
    if (currentStatus == ProjectStatusDTO.StatusEnum.CONDITIONSSUBMITTED) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.CONDITIONSSUBMITTED,
          joiningIcon: 'sync_alt',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.CONDITIONSSUBMITTED),
        });
    }

    // in modification precontracting?
    if (currentStatus == ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING,
          joiningIcon: 'sync_alt',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING),
        });
    }
    if (currentStatus == ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED,
          joiningIcon: 'sync_alt',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED),
        });
    }

    flow.push(
      {
        status: ProjectStatusDTO.StatusEnum.CONTRACTED,
        joiningIcon: 'trending_flat',
        enabled: false,
        icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.CONTRACTED),
      });

    // in modification?
    if (currentStatus == ProjectStatusDTO.StatusEnum.INMODIFICATION) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.INMODIFICATION,
          joiningIcon: 'sync_alt',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.INMODIFICATION),
        });
    }
    if (currentStatus == ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED,
          joiningIcon: 'sync_alt',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED),
        });
    }

    flow.push(
      {
        status: ProjectStatusDTO.StatusEnum.CLOSED,
        joiningIcon: 'trending_flat',
        enabled: false,
        icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.CLOSED),
      });

    return this.setCorrectActiveStates(flow, currentStatus);
  }

  private addStatusesForReturnedToApplicant(projectDetail: ProjectDetailDTO, flow: StatusChip[], currentStatus: ProjectStatusDTO.StatusEnum): StatusChip[] {
    if (projectDetail.secondStepDecision?.eligibilityDecision) { // returned from eligible
      flow.push({
        status: ProjectStatusDTO.StatusEnum.SUBMITTED,
        joiningIcon: 'trending_flat',
        enabled: false,
        icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.SUBMITTED),
      });
      flow.push({
        status: ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT,
        joiningIcon: 'sync_alt',
        enabled: false,
        icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT),
      });
    } else { // returned from submitted
      flow.push({
          status: ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT,
          joiningIcon: 'sync_alt',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT),
        },
        {
          status: (currentStatus === ProjectStatusDTO.StatusEnum.INELIGIBLE) ? ProjectStatusDTO.StatusEnum.INELIGIBLE : ProjectStatusDTO.StatusEnum.ELIGIBLE,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus((currentStatus === ProjectStatusDTO.StatusEnum.INELIGIBLE) ? ProjectStatusDTO.StatusEnum.INELIGIBLE : ProjectStatusDTO.StatusEnum.ELIGIBLE),
        });
    }

    return flow;
  }

  private addStatusesForStep1(projectDetail: ProjectDetailDTO, flow: StatusChip[], currentStatus: ProjectStatusDTO.StatusEnum): StatusChip[] {
    flow.push(
      {
        status: ProjectStatusDTO.StatusEnum.STEP1DRAFT,
        joiningIcon: 'trending_flat',
        enabled: false,
        icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.STEP1DRAFT),
      });

    flow.push({
        status: ProjectStatusDTO.StatusEnum.STEP1SUBMITTED,
        joiningIcon: 'trending_flat',
        enabled: false,
        icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.STEP1SUBMITTED),
      },
      {
        status: (currentStatus === ProjectStatusDTO.StatusEnum.STEP1INELIGIBLE) ? ProjectStatusDTO.StatusEnum.STEP1INELIGIBLE : ProjectStatusDTO.StatusEnum.STEP1ELIGIBLE,
        joiningIcon: 'trending_flat',
        enabled: false,
        icon: this.getIconFromStatus((currentStatus === ProjectStatusDTO.StatusEnum.STEP1INELIGIBLE) ? ProjectStatusDTO.StatusEnum.STEP1INELIGIBLE : ProjectStatusDTO.StatusEnum.STEP1ELIGIBLE),
    });

    // approval status
    if (currentStatus == ProjectStatusDTO.StatusEnum.STEP1NOTAPPROVED) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.STEP1NOTAPPROVED,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.STEP1NOTAPPROVED),
        });
    }
    else if (currentStatus == ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS ||
             projectDetail.firstStepDecision?.finalFundingDecision?.status == ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS) {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS),
        });
    }
    else {
      flow.push(
        {
          status: ProjectStatusDTO.StatusEnum.STEP1APPROVED,
          joiningIcon: 'trending_flat',
          enabled: false,
          icon: this.getIconFromStatus(ProjectStatusDTO.StatusEnum.STEP1APPROVED),
        });
    }

    return flow;
  }

  private setCorrectActiveStates(flow: StatusChip[], currentStatus: ProjectStatusDTO.StatusEnum): StatusChip[] {
    const currentStatusIndex = flow.map(chip => chip.status).indexOf(currentStatus);
    flow.forEach((chip, index) => {
        chip.enabled = (index <= currentStatusIndex);
    });
    return flow;
  }


}


export interface StatusChip {
  status: ProjectStatusDTO.StatusEnum;
  joiningIcon: string;
  icon: string;
  enabled: boolean;
}
