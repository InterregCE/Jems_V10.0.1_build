import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';
import {ProjectDetailPageStore} from '../project-detail-page-store';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-assessments',
  templateUrl: './project-application-assessments.component.html',
  styleUrls: ['./project-application-assessments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationAssessmentsComponent implements OnChanges {

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

  isAssessmentEditable(assessment: any, isProjectLatestVersion: boolean): boolean {
    return !assessment && isProjectLatestVersion && this.projectStatus.status !== ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT;
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
