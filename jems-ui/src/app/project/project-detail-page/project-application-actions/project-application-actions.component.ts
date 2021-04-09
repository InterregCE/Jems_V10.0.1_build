import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {filter, switchMap, take, tap} from 'rxjs/operators';
import {Forms} from '../../../common/utils/forms';
import {ProjectStatusDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {Permission} from '../../../security/permissions/permission';
import {TranslateService} from '@ngx-translate/core';
import * as moment from 'moment';
import {ProjectDetailPageStore} from '../project-detail-page-store.service';

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

  constructor(public translate: TranslateService,
              private projectDetailStore: ProjectDetailPageStore,
              private changeDetectorRef: ChangeDetectorRef,
              private dialog: MatDialog) {
  }

  submitProject(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.detail.submit.dialog.title',
      'project.detail.submit.dialog.message',
    ).pipe(
      take(1),
      filter(answer => !!answer),
      switchMap(() => this.projectDetailStore.submitApplication(this.projectId))
    ).subscribe();
  }

  resubmitProject(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.detail.resubmit.dialog.title',
      'project.detail.resubmit.dialog.message',
    ).pipe(
      take(1),
      filter(answer => !!answer),
      switchMap(() => this.projectDetailStore.submitApplication(this.projectId)),
      tap(() => this.showSuccessMessage())
    ).subscribe();
  }

  returnToApplicant(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.detail.return.dialog.title',
      'project.detail.return.dialog.message',
    ).pipe(
      take(1),
      filter(answer => !!answer),
      switchMap(() => this.projectDetailStore.returnApplicationToApplicant(this.projectId)),
      tap(() => this.showSuccessMessage())
    ).subscribe();
  }

  revertProjectStatus(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.application.revert.status.dialog.title',
      'project.application.revert.status.dialog.message',
      {
        from: this.translate.instant('common.label.projectapplicationstatus.' + this.projectStatus),
        to: this.translate.instant('common.label.projectapplicationstatus.' + this.revertToStatus)
      }
    ).pipe(
      take(1),
      filter(answer => !!answer),
      switchMap(() => this.projectDetailStore.revertApplicationDecision(this.projectId))
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
}
