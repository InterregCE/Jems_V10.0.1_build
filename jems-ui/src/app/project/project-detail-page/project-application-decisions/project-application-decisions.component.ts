import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';
import {combineLatest, Observable} from 'rxjs';
import {ProjectDetailPageStore} from '../project-detail-page-store';
import {map} from 'rxjs/operators';
import StatusEnum = ProjectStatusDTO.StatusEnum;

@Component({
  selector: 'app-project-application-decisions',
  templateUrl: './project-application-decisions.component.html',
  styleUrls: ['./project-application-decisions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDecisionsComponent implements OnChanges {
  STATUS = StatusEnum;

  @Input()
  step: number;
  @Input()
  decisions: ProjectDecisionDTO;

  stepStatus: ProjectStepStatus;

  data$: Observable<{
    projectStatus: ProjectStatusDTO,
    isProjectLatestVersion: boolean,
    callHasTwoSteps: boolean,
    isStep2Now: boolean,
    fundingDecisionResult: ProjectStatusDTO,
    isDecisionFinal: boolean,
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
            fundingDecisionResult: this.extractFundingDecisionResult(project.projectStatus),
            isDecisionFinal: [
              StatusEnum.STEP1APPROVED,
              StatusEnum.STEP1APPROVEDWITHCONDITIONS,
              StatusEnum.STEP1NOTAPPROVED,
              StatusEnum.APPROVED,
              StatusEnum.NOTAPPROVED,
            ].includes(this.extractFundingDecisionResult(project.projectStatus)?.status),
          }
        ))
      );
  }

  ngOnChanges(): void {
    this.stepStatus = new ProjectStepStatus(this.step);
  }

  fundingDecisionEnabled(): boolean {
    return (this.decisions?.eligibilityDecision?.status === this.STATUS.ELIGIBLE
      || this.decisions?.eligibilityDecision?.status === this.STATUS.STEP1ELIGIBLE)
      && !!this.decisions?.qualityAssessment;
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

  private extractFundingDecisionResult(projectStatus: ProjectStatusDTO): ProjectStatusDTO {
    if ((projectStatus.status === StatusEnum.APPROVED || projectStatus.status === StatusEnum.NOTAPPROVED) && Number(this.step) === 2) {
      return projectStatus;
    }
    return this.decisions?.fundingDecision;
  }
}
