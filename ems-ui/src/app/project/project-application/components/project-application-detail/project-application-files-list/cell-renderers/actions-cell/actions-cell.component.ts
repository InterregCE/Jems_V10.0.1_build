import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProject, OutputProjectFile} from '@cat/api';
import {Permission} from '../../../../../../../security/permissions/permission';

@Component({
  selector: 'app-actions-cell',
  templateUrl: './actions-cell.component.html',
  styleUrls: ['./actions-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ActionsCellComponent {
  @Input()
  file: OutputProjectFile;
  @Input()
  project: OutputProject;
  @Input()
  permission: Permission;

  @Output()
  edit = new EventEmitter<OutputProjectFile>()
  @Output()
  download = new EventEmitter<OutputProjectFile>();
  @Output()
  delete = new EventEmitter<OutputProjectFile>();

  canChangeFile(): boolean {
    if (this.permission === Permission.ADMINISTRATOR) {
      return true;
    }

    return this.file.type === OutputProjectFile.TypeEnum.APPLICANTFILE
      ? this.canChangeApplicationFile()
      : this.canChangeAssessmentFile();
  }

  private canChangeApplicationFile(): boolean {
    if (this.permission === Permission.PROGRAMME_USER) {
      return false;
    }

    // the applicant user can only change/delete files that are added after a submission change
    const lastSubmissionDate = this.project?.lastResubmission?.updated
      || this.project?.firstSubmission?.updated
      || this.project?.projectStatus.updated;

    return this.file.updated > lastSubmissionDate;
  }

  private canChangeAssessmentFile(): boolean {
    return this.permission !== Permission.APPLICANT_USER
      && (!this.project.fundingDecision || this.file.updated > this.project.projectStatus.updated);
  }
}
