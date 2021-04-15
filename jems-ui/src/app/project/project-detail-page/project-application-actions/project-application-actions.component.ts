import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input} from '@angular/core';
import {tap} from 'rxjs/operators';
import {ProjectStatusDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {Permission} from '../../../security/permissions/permission';
import {TranslateService} from '@ngx-translate/core';
import * as moment from 'moment';
import {ProjectDetailPageStore} from '../project-detail-page-store.service';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-project-application-actions',
  templateUrl: './project-application-actions.component.html',
  styleUrls: ['./project-application-actions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationActionsComponent {
  Alert = Alert;
  Permission = Permission;
  STATUS = ProjectStatusDTO.StatusEnum;

  returnableStatuses = [
    ProjectStatusDTO.StatusEnum.SUBMITTED,
    ProjectStatusDTO.StatusEnum.ELIGIBLE,
    ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS,
    ProjectStatusDTO.StatusEnum.APPROVED,
  ];

  @Input()
  projectStatus: ProjectStatusDTO.StatusEnum;
  @Input()
  revertToStatus: string;
  @Input()
  projectCallEndDate: Date;
  @Input()
  projectId: number;

  // TODO: create a component
  successMessage: boolean;
  actionPending = false;

  constructor(public translate: TranslateService,
              private projectDetailStore: ProjectDetailPageStore,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  submitProject(): void {
    this.projectDetailStore.submitApplication(this.projectId)
      .pipe(
        tap(() => this.actionPending = false),
      ).subscribe();
  }

  resubmitProject(): void {
    this.projectDetailStore.submitApplication(this.projectId)
      .pipe(
        tap(() => this.actionPending = false),
        tap(() => this.showSuccessMessage())
      ).subscribe();
  }

  returnToApplicant(): void {
    this.projectDetailStore.returnApplicationToApplicant(this.projectId)
      .pipe(
        tap(() => this.actionPending = false),
        tap(() => this.showSuccessMessage())
      ).subscribe();
  }

  revertProjectStatus(): void {
    this.projectDetailStore.revertApplicationDecision(this.projectId)
      .pipe(
        tap(() => this.actionPending = false),
      ).subscribe();
  }

  isOpen(): boolean {
    const currentDate = moment(new Date());
    return currentDate.isBefore(this.projectCallEndDate);
  }

  private showSuccessMessage(): void {
    this.successMessage = true;
    setTimeout(() => {
      this.successMessage = false;
      this.changeDetectorRef.markForCheck();
    },         4000);
  }

  getRevertConfirmation(): ConfirmDialogData {
    return {
      title: 'project.application.revert.status.dialog.title',
      message: 'project.application.revert.status.dialog.message',
      arguments: {
        from: this.translate.instant('common.label.projectapplicationstatus.' + this.projectStatus),
        to: this.translate.instant('common.label.projectapplicationstatus.' + this.revertToStatus)
      }
    };
  }
}
