import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectDetailDTO, ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {PermissionService} from '../../../security/permissions/permission.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import StatusEnum = ProjectStatusDTO.StatusEnum;
import Permissions = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-project-application-decisions',
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
    currentVersionOfProjectStatus: ProjectStatusDTO;
    callHasTwoSteps: boolean;
    isStep2Now: boolean;
    decision: ProjectDecisionDTO;
    fundingDecisionResult: ProjectStatusDTO;
    isDecisionFinal: boolean;
    isReturnedNow: boolean;
    userCanChangeFunding: boolean;
    preFundingDecision: ProjectStatusDTO;
    isFundingDecisionPreconditionOk: boolean;
  }>;

  constructor(
    private projectStore: ProjectStore,
    private permissionService: PermissionService
  ) {
    this.data$ = combineLatest([
      this.projectStore.callHasTwoSteps$,
      this.projectStore.currentVersionOfProject$,
      this.permissionService.hasPermission([Permissions.ProjectStatusDecideApproved, Permissions.ProjectStatusDecideApprovedWithConditions, Permissions.ProjectStatusDecideNotApproved]),
    ])
      .pipe(
        map(([ callHasTwoSteps, currentVersionOfProject, userCanEditFunding]) => (
          {
            currentVersionOfProjectStatus: currentVersionOfProject.projectStatus,
            callHasTwoSteps,
            isStep2Now: currentVersionOfProject.step2Active,
            decision: this.getDecision(currentVersionOfProject),
            fundingDecisionResult: this.getDecision(currentVersionOfProject)?.finalFundingDecision || this.getDecision(currentVersionOfProject)?.preFundingDecision,
            isDecisionFinal: !!this.getDecision(currentVersionOfProject)?.finalFundingDecision?.status,
            isReturnedNow: this.isInReturnedStatus(currentVersionOfProject.projectStatus.status),
            userCanChangeFunding: userCanEditFunding,
            preFundingDecision: this.getDecision(currentVersionOfProject)?.preFundingDecision,
            isFundingDecisionPreconditionOk: Number(this.step) === 2
              ? currentVersionOfProject.secondStepDecision?.eligibilityDecision?.status === StatusEnum.ELIGIBLE && !!currentVersionOfProject.secondStepDecision?.qualityAssessment
              : currentVersionOfProject.firstStepDecision?.eligibilityDecision?.status === StatusEnum.STEP1ELIGIBLE && !!currentVersionOfProject.firstStepDecision?.qualityAssessment,
          }
        ))
      );
  }

  private getDecision(currentVersionOfProject: ProjectDetailDTO): ProjectDecisionDTO {
    return Number(this.step) === 2 ? currentVersionOfProject.secondStepDecision : currentVersionOfProject.firstStepDecision;
  }

  ngOnChanges(): void {
    this.stepStatus = new ProjectStepStatus(this.step);
  }

  isDecisionEditable(projectStatus: ProjectStatusDTO, decision: any): boolean {
    return !decision && projectStatus.status !== this.STATUS.RETURNEDTOAPPLICANT;
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

  isInReturnedStatus(status: string): boolean {
   return status === StatusEnum.RETURNEDTOAPPLICANT || status === StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS;
  }

}
