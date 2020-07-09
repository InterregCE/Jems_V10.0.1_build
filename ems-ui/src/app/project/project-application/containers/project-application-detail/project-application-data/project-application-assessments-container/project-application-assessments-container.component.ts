import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Permission} from '../../../../../../security/permissions/permission';
import {InputProjectStatus, OutputProject, OutputProjectStatus} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {Alert} from '@common/components/forms/alert';
import {MatDialog} from '@angular/material/dialog';
import {FormGroup} from '@angular/forms';
import {Forms} from '../../../../../../common/utils/forms';
import {take, takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-assessments-container',
  templateUrl: './project-application-assessments-container.component.html',
  styleUrls: ['./project-application-assessments-container.component.scss']
})
export class ProjectApplicationAssessmentsContainerComponent extends AbstractForm implements OnInit {
  @Input()
  project: OutputProject;

  @Output()
  submitProjectApplication: EventEmitter<InputProjectStatus> = new EventEmitter<InputProjectStatus>();

  Alert = Alert;
  Permission = Permission;
  OutputProjectStatus = OutputProjectStatus;
  constructor(private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
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
