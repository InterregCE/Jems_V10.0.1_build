import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {Permission} from '../../../../../security/permissions/permission';
import {InputProjectStatus, OutputProjectStatus, OutputUser} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {Alert} from '@common/components/forms/alert';
import {MatDialog} from '@angular/material/dialog';
import {FormGroup} from '@angular/forms';
import {Forms} from '../../../../../common/utils/forms';
import {take, takeUntil} from 'rxjs/operators';

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
  submitProjectApplication: EventEmitter<InputProjectStatus> = new EventEmitter<InputProjectStatus>();

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
      takeUntil(this.destroyed$)
    ).subscribe(clickedYes => {
      if (clickedYes) {
        this.submitProjectApplication.emit({status: InputProjectStatus.StatusEnum.SUBMITTED} as InputProjectStatus);
      }
    });
  }
}
