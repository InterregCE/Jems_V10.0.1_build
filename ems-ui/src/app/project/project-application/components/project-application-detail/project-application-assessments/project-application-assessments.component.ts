import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {Permission} from '../../../../../security/permissions/permission';
import {InputProjectStatus, OutputProjectStatus, OutputUser} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {Alert} from '@common/components/forms/alert';
import {MatDialog} from '@angular/material/dialog';
import {FormGroup} from '@angular/forms';
import {Forms} from '../../../../../common/utils/forms';
import {filter, map, take, takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-assessments',
  templateUrl: './project-application-assessments.component.html',
  styleUrls: ['./project-application-assessments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationAssessmentsComponent extends AbstractForm {
  Alert = Alert;
  Permission = Permission;
  OutputProjectStatus = OutputProjectStatus;

  @Input()
  submittingUser: OutputUser;
  @Input()
  projectStatus: OutputProjectStatus.StatusEnum;

  @Output()
  changeStatus = new EventEmitter<InputProjectStatus.StatusEnum>();

  constructor(private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return null;
  }

  submitProject(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.detail.submit.dialog.title',
      'project.detail.submit.dialog.message',
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(answer => !!answer),
      map(() => this.changeStatus.emit(InputProjectStatus.StatusEnum.SUBMITTED))
    ).subscribe();
  }

  resubmitProject(): void {
    Forms.confirmDialog(
      this.dialog,
      'Re-submit Project',
      'Are you sure, you want to re-submit the application? Operation cannot be reversed.',
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(answer => !!answer),
      map(() => this.changeStatus.emit(InputProjectStatus.StatusEnum.RESUBMITTED))
    ).subscribe();
  }

  returnToApplicant(): void {
    Forms.confirmDialog(
      this.dialog,
      'Return To Applicant',
      'Are you sure you want to return the application back to the applicant?',
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(answer => !!answer),
      map(() => this.changeStatus.emit(InputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT))
    ).subscribe();
  }
}
