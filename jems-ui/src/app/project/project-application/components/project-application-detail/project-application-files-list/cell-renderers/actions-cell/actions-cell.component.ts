import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProjectFile, ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
import ProjectStatus = ProjectStatusDTO.StatusEnum;

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
  project: ProjectDetailDTO;
  @Input()
  fundingDecisionDefined: boolean;
  @Input()
  isAllowedToChangeApplicationFile: boolean;
  @Input()
  isAllowedToRetrieveApplicationFile: boolean;
  @Input()
  isAllowedToChangeAssessmentFile: boolean;

  @Output()
  edit = new EventEmitter<OutputProjectFile>();
  @Output()
  download = new EventEmitter<OutputProjectFile>();
  @Output()
  delete = new EventEmitter<OutputProjectFile>();

  canChangeFile(): boolean {
    return this.file.type === OutputProjectFile.TypeEnum.APPLICANTFILE
      ? this.canChangeApplicationFile()
      : this.canChangeAssessmentFile();
  }

  canDeleteFile(): boolean {
    return this.file.type === OutputProjectFile.TypeEnum.APPLICANTFILE
      ? this.canDeleteApplicationFile()
      : this.canChangeAssessmentFile();
  }

  private canChangeApplicationFile(): boolean {
    // make a difference between users with only View permission and applicants
    if (this.isAllowedToChangeApplicationFile && this.isAllowedToRetrieveApplicationFile) {
      return this.isAllowedToChangeApplicationFile;
    }
    if (this.isAllowedToRetrieveApplicationFile) {
      return false;
    }

    // the applicant user can only change files that if the project is in a specific status
    return this.project?.projectStatus.status === ProjectStatus.DRAFT
      || this.project?.projectStatus.status === ProjectStatus.STEP1DRAFT
      || this.project?.projectStatus.status === ProjectStatus.RETURNEDTOAPPLICANT;
  }

  private canChangeAssessmentFile(): boolean {
    return this.isAllowedToChangeAssessmentFile
      && (!this.fundingDecisionDefined || this.file.updated > this.project.projectStatus.updated);
  }

  private canDeleteApplicationFile(): boolean {
    // the user can only delete files that are added after a submission change
    const lastStatusChange = this.project?.projectStatus.updated;

    // make a difference between users with only View permission and applicants
    if (this.isAllowedToChangeApplicationFile && this.isAllowedToRetrieveApplicationFile) {
      return this.isAllowedToChangeApplicationFile && this.file.updated > lastStatusChange;
    }
    if (this.isAllowedToRetrieveApplicationFile) {
      return false;
    }

    return this.file.updated > lastStatusChange;
  }
}
