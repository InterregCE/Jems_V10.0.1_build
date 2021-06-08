import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {Permission} from '../../../security/permissions/permission';
import {TranslateService} from '@ngx-translate/core';
import * as moment from 'moment';
import {ProjectDetailPageStore} from '../project-detail-page-store';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '../../../common/models/APIError';

@Component({
  selector: 'app-project-application-actions',
  templateUrl: './project-application-actions.component.html',
  styleUrls: ['./project-application-actions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationActionsComponent {
  Alert = Alert;
  // tslint:disable-next-line:variable-name
  UserRole = Permission;
  // tslint:disable-next-line:variable-name
  Permissions = UserRoleDTO.PermissionsEnum;
  STATUS = ProjectStatusDTO.StatusEnum;

  data$: Observable<{
    projectStatus: ProjectStatusDTO.StatusEnum,
    projectId: number,
    projectCallEndDateStep1: Date,
    projectCallEndDate: Date,
    startStepTwoAvailable: boolean,
    returnToApplicantAvailable: boolean,
    revertToStatus: string | null,
    isThisUserOwner: boolean,
    hasPreConditionCheckSucceed: boolean
    isProjectLatestVersion: boolean
  }>;

  // TODO: create a component
  successMessage: boolean;
  error$ = new BehaviorSubject<APIError | null>(null);
  actionPending = false;
  preConditionCheckInProgress = false;

  constructor(public translate: TranslateService,
              private projectDetailStore: ProjectDetailPageStore,
              private changeDetectorRef: ChangeDetectorRef) {
    this.data$ = combineLatest([
      this.projectDetailStore.project$,
      this.projectDetailStore.callHasTwoSteps$,
      this.projectDetailStore.revertToStatus$,
      this.projectDetailStore.isThisUserOwner$,
      this.projectDetailStore.preConditionCheckResult$.pipe(map(it => it ? it.submissionAllowed : false)),
      this.projectDetailStore.isProjectLatestVersion$
    ]).pipe(
      map(([project, callHasTwoSteps, revertToStatus, isThisUserOwner, hasPreConditionCheckSucceed, isProjectLatestVersion]) => ({
        projectStatus: project.projectStatus.status,
        projectId: project.id,
        projectCallEndDate: project.callSettings?.endDate,
        projectCallEndDateStep1: project.callSettings?.endDateStep1,
        startStepTwoAvailable: this.startStepTwoAvailable(project.projectStatus.status, callHasTwoSteps, project.step2Active, isProjectLatestVersion),
        returnToApplicantAvailable: this.returnToApplicantAvailable(project.projectStatus.status, callHasTwoSteps, project.step2Active, isProjectLatestVersion),
        revertToStatus,
        isThisUserOwner,
        hasPreConditionCheckSucceed,
        isProjectLatestVersion
      }))
    );
  }

  preConditionCheck(projectId: number): void {
    this.preConditionCheckInProgress = true;
    this.projectDetailStore.preConditionCheck(projectId).pipe(
      catchError((error) => this.showErrorMessage(error.error)),
      finalize(() => this.preConditionCheckInProgress = false)
    ).subscribe();
  }

  submitProject(projectId: number): void {
    this.actionPending = true;
    this.projectDetailStore.submitApplication(projectId)
      .pipe(
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  resubmitProject(projectId: number): void {
    this.actionPending = true;
    this.projectDetailStore.submitApplication(projectId)
      .pipe(
        tap(() => this.showSuccessMessage()),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  returnToApplicant(projectId: number): void {
    this.actionPending = true;
    this.projectDetailStore.returnApplicationToApplicant(projectId)
      .pipe(
        finalize(() => this.actionPending = false),
        tap(() => this.showSuccessMessage())
      ).subscribe();
  }

  revertProjectStatus(projectId: number): void {
    this.actionPending = true;
    this.projectDetailStore.revertApplicationDecision(projectId)
      .pipe(
        finalize(() => this.actionPending = false),
      ).subscribe();
  }

  startStepTwo(projectId: number): void {
    this.actionPending = true;
    this.projectDetailStore.returnApplicationToDraft(projectId)
      .pipe(
        tap(() => this.actionPending = false),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  isSubmitDisabled(projectCallEndDate: Date, hasPreConditionCheckSucceed: boolean, isProjectLatestVersion: boolean, projectStatus: ProjectStatusDTO.StatusEnum): boolean {
    if (!isProjectLatestVersion) {
      return true;
    }
    const currentDate = moment(new Date());
    return !(currentDate.isBefore(projectCallEndDate) && (hasPreConditionCheckSucceed || projectStatus === this.STATUS.STEP1DRAFT));
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
                                projectInSecondStep: boolean,
                                isProjectLatestVersion: boolean): boolean {
    if (!isProjectLatestVersion || !callHasTwoSteps || projectInSecondStep) {
      return false;
    }
    return status === ProjectStatusDTO.StatusEnum.STEP1APPROVED
      || status === ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS;
  }

  private returnToApplicantAvailable(status: ProjectStatusDTO.StatusEnum,
                                     callHasTwoSteps: boolean,
                                     projectInSecondStep: boolean,
                                     isProjectLatestVersion: boolean): boolean {
    const returnableStatuses = [
      ProjectStatusDTO.StatusEnum.SUBMITTED,
      ProjectStatusDTO.StatusEnum.ELIGIBLE,
      ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS,
      ProjectStatusDTO.StatusEnum.APPROVED,
    ];

    if (!isProjectLatestVersion || (callHasTwoSteps && !projectInSecondStep)) {
      return false;
    }

    return returnableStatuses.includes(status);
  }

}
