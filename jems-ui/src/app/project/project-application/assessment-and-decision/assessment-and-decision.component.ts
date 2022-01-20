import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {Permission} from '../../../security/permissions/permission';
import {ProjectDetailDTO, ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {Alert} from '@common/components/forms/alert';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {AssessmentAndDecisionStore} from '@project/project-application/assessment-and-decision/assessment-and-decision-store.service';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';

@Component({
  selector: 'jems-assessment-and-decision',
  templateUrl: './assessment-and-decision.component.html',
  styleUrls: ['./assessment-and-decision.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AssessmentAndDecisionComponent {

  Alert = Alert;
  Permission = Permission;
  PermissionsEnum = UserRoleDTO.PermissionsEnum;
  STATUS = ProjectStatusDTO.StatusEnum;
  fileManagementSection = {type: FileCategoryTypeEnum.ASSESSMENT} as CategoryInfo;

  data$: Observable<{
    currentVersionOfProject: ProjectDetailDTO;
    currentVersionOfProjectTitle: string;
    currentVersionOfProjectStatus: ProjectStatusDTO.StatusEnum;
    projectId: number;
    startStepTwoAvailable: boolean;
    returnToApplicantAvailable: boolean;
    revertToStatus: string | null;
    callHasTwoSteps: boolean;
  }>;

  // TODO: create a component
  successMessage: boolean;
  error$ = new BehaviorSubject<APIError | null>(null);
  actionPending = false;

  constructor(public translate: TranslateService,
              private assessmentAndDecisionStore: AssessmentAndDecisionStore,
              private projectStore: ProjectStore,
              private changeDetectorRef: ChangeDetectorRef) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionOfProject$,
      this.projectStore.currentVersionOfProjectTitle$,
      this.projectStore.callHasTwoSteps$,
      this.assessmentAndDecisionStore.revertToStatus$
    ]).pipe(
      map(([currentVersionOfProject, currentVersionOfProjectTitle, callHasTwoSteps, revertToStatus]) => ({
        currentVersionOfProject,
        currentVersionOfProjectTitle,
        currentVersionOfProjectStatus: currentVersionOfProject.projectStatus.status,
        projectId: currentVersionOfProject.id,
        startStepTwoAvailable: this.startStepTwoAvailable(currentVersionOfProject.projectStatus.status, callHasTwoSteps, currentVersionOfProject.step2Active),
        returnToApplicantAvailable: this.returnToApplicantAvailable(currentVersionOfProject.projectStatus.status, callHasTwoSteps, currentVersionOfProject.step2Active),
        revertToStatus,
        callHasTwoSteps
      }))
    );
  }

  returnToApplicant(projectId: number): void {
    this.actionPending = true;
    this.assessmentAndDecisionStore.returnApplicationToApplicant(projectId)
      .pipe(
        finalize(() => this.actionPending = false),
        tap(() => this.showSuccessMessage())
      ).subscribe();
  }

  returnToApplicantForConditions(projectId: number): void {
    this.actionPending = true;
    this.assessmentAndDecisionStore.returnApplicationToApplicantForConditions(projectId)
      .pipe(
        finalize(() => this.actionPending = false),
        tap(() => this.showSuccessMessage())
      ).subscribe();
  }

  revertProjectStatus(projectId: number): void {
    this.actionPending = true;
    this.assessmentAndDecisionStore.revertApplicationDecision(projectId)
      .pipe(
        finalize(() => this.actionPending = false),
      ).subscribe();
  }

  startStepTwo(projectId: number): void {
    this.actionPending = true;
    this.assessmentAndDecisionStore.returnApplicationToDraft(projectId)
      .pipe(
        tap(() => this.actionPending = false),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  getRevertConfirmation(projectStatus: ProjectStatusDTO.StatusEnum, revertToStatus: string): ConfirmDialogData {
    return {
      title: 'project.application.revert.status.dialog.title',
      message: {
        i18nKey: 'project.application.revert.status.dialog.message',
        i18nArguments: {
          from: this.translate.instant('common.label.projectapplicationstatus.' + projectStatus),
          to: this.translate.instant('common.label.projectapplicationstatus.' + revertToStatus)
        }
      }
    };
  }

  private showSuccessMessage(): void {
    this.successMessage = true;
    setTimeout(() => {
      this.successMessage = false;
      this.changeDetectorRef.markForCheck();
    },         4000);
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },         4000);
    return of(null);
  }

  private startStepTwoAvailable(status: ProjectStatusDTO.StatusEnum,
                                callHasTwoSteps: boolean,
                                projectInSecondStep: boolean): boolean {
    if (!callHasTwoSteps || projectInSecondStep) {
      return false;
    }
    return status === ProjectStatusDTO.StatusEnum.STEP1APPROVED
      || status === ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS;
  }

  private returnToApplicantAvailable(status: ProjectStatusDTO.StatusEnum,
                                     callHasTwoSteps: boolean,
                                     projectInSecondStep: boolean): boolean {
    const returnableStatuses = [
      ProjectStatusDTO.StatusEnum.SUBMITTED,
      ProjectStatusDTO.StatusEnum.ELIGIBLE,
      ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS,
      ProjectStatusDTO.StatusEnum.CONDITIONSSUBMITTED
    ];

    if (callHasTwoSteps && !projectInSecondStep) {
      return false;
    }

    return returnableStatuses.includes(status);
  }

}
