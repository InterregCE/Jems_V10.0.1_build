import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectDetailDTO, ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';
import {combineLatest, Observable} from 'rxjs';
import {ProjectDetailPageStore} from '../project-detail-page-store';
import {map} from 'rxjs/operators';
import StatusEnum = ProjectStatusDTO.StatusEnum;
import Permissions = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-project-application-decisions',
  templateUrl: './project-application-decisions.component.html',
  styleUrls: ['./project-application-decisions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDecisionsComponent implements OnChanges {
  STATUS = StatusEnum;
  Permissions = Permissions;

  @Input()
  step: number;

  stepStatus: ProjectStepStatus;

  data$: Observable<{
    projectStatus: ProjectStatusDTO,
    isProjectLatestVersion: boolean,
    callHasTwoSteps: boolean,
    isStep2Now: boolean,
    decision: ProjectDecisionDTO,
    fundingDecisionResult: ProjectStatusDTO,
    isDecisionFinal: boolean,
    isReturnedNow: boolean,
    isFundingDecisionPreconditionOk: boolean,
  }>;

  constructor(private projectDetailPageStore: ProjectDetailPageStore) {
    this.data$ = combineLatest([
      this.projectDetailPageStore.isProjectLatestVersion$,
      this.projectDetailPageStore.callHasTwoSteps$,
      this.projectDetailPageStore.project$,
    ])
      .pipe(
        map(([isProjectLatestVersion, callHasTwoSteps, project]) => (
          {
            projectStatus: project.projectStatus,
            isProjectLatestVersion,
            callHasTwoSteps,
            isStep2Now: [
              StatusEnum.DRAFT,
              StatusEnum.SUBMITTED,
              StatusEnum.RETURNEDTOAPPLICANT,
              StatusEnum.ELIGIBLE,
              StatusEnum.INELIGIBLE,
              StatusEnum.APPROVED,
              StatusEnum.APPROVEDWITHCONDITIONS,
              StatusEnum.NOTAPPROVED,
            ].includes(project.projectStatus.status),
            decision: this.getDecision(project),
            fundingDecisionResult: this.getDecision(project)?.finalFundingDecision || this.getDecision(project)?.preFundingDecision,
            isDecisionFinal: !!this.getDecision(project)?.finalFundingDecision?.status,
            isReturnedNow: project.projectStatus.status === StatusEnum.RETURNEDTOAPPLICANT,
            isFundingDecisionPreconditionOk: Number(this.step) === 2
              ? project.secondStepDecision?.eligibilityDecision?.status === StatusEnum.ELIGIBLE && !!project.secondStepDecision?.qualityAssessment
              : project.firstStepDecision?.eligibilityDecision?.status === StatusEnum.STEP1ELIGIBLE && !!project.firstStepDecision?.qualityAssessment,
          }
        ))
      );
  }

  private getDecision(project: ProjectDetailDTO): ProjectDecisionDTO {
    return Number(this.step) === 2 ? project.secondStepDecision : project.firstStepDecision;
  }

  ngOnChanges(): void {
    this.stepStatus = new ProjectStepStatus(this.step);
  }

  isDecisionEditable(projectStatus: ProjectStatusDTO, decision: any, isProjectLatestVersion: boolean): boolean {
    return !decision && isProjectLatestVersion && projectStatus.status !== this.STATUS.RETURNEDTOAPPLICANT;
  }

  isPanelVisible(projectStatus: ProjectStatusDTO, callHasTwoSteps: boolean): boolean {
    const isDraft = projectStatus.status === ProjectStatusDTO.StatusEnum.DRAFT;
    const isStep1Draft = projectStatus.status === ProjectStatusDTO.StatusEnum.STEP1DRAFT;
    if (callHasTwoSteps) {
      return !isStep1Draft && !(this.step === 2 && isDraft);
    } else {
      return !isDraft && !isStep1Draft;
    }
  }

}
