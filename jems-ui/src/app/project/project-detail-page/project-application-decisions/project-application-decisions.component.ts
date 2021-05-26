import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';
import {combineLatest, Observable} from 'rxjs';
import {ProjectDetailPageStore} from '../project-detail-page-store';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-decisions',
  templateUrl: './project-application-decisions.component.html',
  styleUrls: ['./project-application-decisions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDecisionsComponent implements OnChanges {
  STATUS = ProjectStatusDTO.StatusEnum;

  @Input()
  step: number;
  @Input()
  decisions: ProjectDecisionDTO;
  @Input()
  projectStatus: ProjectStatusDTO;

  stepStatus: ProjectStepStatus;

  data$: Observable<{
    isProjectLatestVersion: boolean,
    callHasTwoSteps: boolean
  }>;

  constructor(private projectDetailPageStore: ProjectDetailPageStore) {
    this.data$ = combineLatest([
      this.projectDetailPageStore.isProjectLatestVersion$,
      this.projectDetailPageStore.callHasTwoSteps$
    ])
      .pipe(
        map(([isProjectLatestVersion, callHasTwoSteps]) => ({isProjectLatestVersion, callHasTwoSteps}))
      );
  }

  ngOnChanges(): void {
    this.stepStatus = new ProjectStepStatus(this.step);
  }

  updateOrViewFundingLabel(): string {
    return this.decisions?.fundingDecision?.status === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS
    && this.projectStatus.status !== this.STATUS.RETURNEDTOAPPLICANT
      ? 'project.assessment.fundingDecision.assessment.update'
      : 'project.assessment.fundingDecision.assessment.view';
  }

  fundingDecisionEnabled(): boolean {
    return (this.decisions?.eligibilityDecision?.status === this.STATUS.ELIGIBLE
      || this.decisions?.eligibilityDecision?.status === this.STATUS.STEP1ELIGIBLE)
      && !!this.decisions?.qualityAssessment;
  }

  isDecisionEditable(decision: any, isProjectLatestVersion: boolean): boolean {
    return !decision && isProjectLatestVersion && this.projectStatus.status !== this.STATUS.RETURNEDTOAPPLICANT;
  }

  isPanelVisible(callHasTwoSteps: boolean): boolean {
    const isDraft = this.projectStatus.status === ProjectStatusDTO.StatusEnum.DRAFT;
    const isStep1Draft = this.projectStatus.status === ProjectStatusDTO.StatusEnum.STEP1DRAFT;
    if (callHasTwoSteps) {
      return !isStep1Draft && !(this.step === 2 && isDraft);
    } else {
      return !isDraft && !isStep1Draft;
    }
  }
}
