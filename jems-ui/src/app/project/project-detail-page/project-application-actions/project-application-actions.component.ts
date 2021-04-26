import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input} from '@angular/core';
import {tap} from 'rxjs/operators';
import {ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {map, tap} from 'rxjs/operators';
import {ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {Permission} from '../../../security/permissions/permission';
import {TranslateService} from '@ngx-translate/core';
import * as moment from 'moment';
import {ProjectDetailPageStore} from '../project-detail-page-store.service';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {combineLatest, Observable} from 'rxjs';

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
    projectCallEndDate: Date,
    startStepTwoAvailable: boolean,
    returnToApplicantAvailable: boolean,
    revertToStatus: string | null,
  }>;

  // TODO: create a component
  successMessage: boolean;
  actionPending = false;

  constructor(public translate: TranslateService,
              private projectDetailStore: ProjectDetailPageStore,
              private changeDetectorRef: ChangeDetectorRef) {
    this.data$ = combineLatest([
      this.projectDetailStore.project$,
      this.projectDetailStore.callHasTwoSteps$,
      this.projectDetailStore.revertToStatus$,
    ]).pipe(
      map(([project, callHasTwoSteps, revertToStatus]) => ({
        projectStatus: project.projectStatus.status,
        projectId: project.id,
        projectCallEndDate: project.callSettings?.endDate,
        startStepTwoAvailable: this.startStepTwoAvailable(project.projectStatus.status, callHasTwoSteps, project.step2Active),
        returnToApplicantAvailable: this.returnToApplicantAvailable(project.projectStatus.status, callHasTwoSteps, project.step2Active),
        revertToStatus,
      }))
    );
  }

  submitProject(projectId: number): void {
    this.projectDetailStore.submitApplication(projectId)
      .pipe(
        tap(() => this.actionPending = false),
      ).subscribe();
  }

  resubmitProject(projectId: number): void {
    this.projectDetailStore.submitApplication(projectId)
      .pipe(
        tap(() => this.actionPending = false),
        tap(() => this.showSuccessMessage())
      ).subscribe();
  }

  returnToApplicant(projectId: number): void {
    this.projectDetailStore.returnApplicationToApplicant(projectId)
      .pipe(
        tap(() => this.actionPending = false),
        tap(() => this.showSuccessMessage())
      ).subscribe();
  }

  revertProjectStatus(projectId: number): void {
    this.projectDetailStore.revertApplicationDecision(projectId)
      .pipe(
        tap(() => this.actionPending = false),
      ).subscribe();
  }

  startStepTwo(projectId: number): void {
    this.projectDetailStore.returnApplicationToDraft(projectId)
      .pipe(
        tap(() => this.actionPending = false),
      ).subscribe();
  }

  isOpen(projectCallEndDate: Date): boolean {
    const currentDate = moment(new Date());
    return currentDate.isBefore(projectCallEndDate);
  }

  private showSuccessMessage(): void {
    this.successMessage = true;
    setTimeout(() => {
      this.successMessage = false;
      this.changeDetectorRef.markForCheck();
    }, 4000);
  }

  getRevertConfirmation(projectStatus: ProjectStatusDTO.StatusEnum, revertToStatus: string): ConfirmDialogData {
    return {
      title: 'project.application.revert.status.dialog.title',
      message: 'project.application.revert.status.dialog.message',
      arguments: {
        from: this.translate.instant('common.label.projectapplicationstatus.' + projectStatus),
        to: this.translate.instant('common.label.projectapplicationstatus.' + revertToStatus)
      }
    };
  }

  private startStepTwoAvailable(status: ProjectStatusDTO.StatusEnum, callHasTwoSteps: boolean,
                                projectInSecondStep: boolean): boolean {
    if (!callHasTwoSteps || projectInSecondStep) {
      return false;
    }
    return status === ProjectStatusDTO.StatusEnum.STEP1APPROVED
      || status === ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS;
  }

  private returnToApplicantAvailable(status: ProjectStatusDTO.StatusEnum,
                                     callHasTwoSteps: boolean, projectInSecondStep: boolean): boolean {
    const returnableStatuses = [
      ProjectStatusDTO.StatusEnum.SUBMITTED,
      ProjectStatusDTO.StatusEnum.ELIGIBLE,
      ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS,
      ProjectStatusDTO.StatusEnum.APPROVED,
    ];

    if (callHasTwoSteps && !projectInSecondStep) {
      return false;
    }

    return returnableStatuses.includes(status);
  }

}
