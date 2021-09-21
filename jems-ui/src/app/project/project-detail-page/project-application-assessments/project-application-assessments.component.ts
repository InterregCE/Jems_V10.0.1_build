import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import Permissions = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-project-application-assessments',
  templateUrl: './project-application-assessments.component.html',
  styleUrls: ['./project-application-assessments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationAssessmentsComponent implements OnChanges {
  Permissions = Permissions;

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

  constructor(private projectStore: ProjectStore) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionIsLatest$,
      this.projectStore.callHasTwoSteps$
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
