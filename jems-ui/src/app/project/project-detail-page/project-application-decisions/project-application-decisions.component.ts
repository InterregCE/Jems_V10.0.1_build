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
    userCanChangeFunding: boolean,
    preFundingDecision: ProjectStatusDTO,
    isFundingDecisionPreconditionOk: boolean,
  }>;

  constructor(
    private projectStore: ProjectStore,
    private permissionService: PermissionService,
  ) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionIsLatest$,
      this.projectStore.callHasTwoSteps$,
      this.projectStore.project$,
      this.permissionService.hasPermission([Permissions.ProjectStatusDecideApproved, Permissions.ProjectStatusDecideApprovedWithConditions, Permissions.ProjectStatusDecideNotApproved]),
    ])
      .pipe(
        map(([isProjectLatestVersion, callHasTwoSteps, project, userCanEditFunding]) => (
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
            isReturnedNow: this.isInReturnedStatus(project.projectStatus.status),
            userCanChangeFunding: userCanEditFunding,
            preFundingDecision: this.getDecision(project)?.preFundingDecision,
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

  isInReturnedStatus(status: string): boolean {
   return status === StatusEnum.RETURNEDTOAPPLICANT || status === StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS;
  }

}
