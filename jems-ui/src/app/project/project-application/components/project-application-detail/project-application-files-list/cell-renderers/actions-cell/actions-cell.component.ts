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
  @Input()
  isThisUserOwner: boolean;

  @Output()
  edit = new EventEmitter<OutputProjectFile>();
  @Output()
  download = new EventEmitter<OutputProjectFile>();
  @Output()
  delete = new EventEmitter<OutputProjectFile>();

  private static isApplicationOpen(status: ProjectStatusDTO.StatusEnum): boolean {
    return status === ProjectStatus.DRAFT
      || status === ProjectStatus.STEP1DRAFT
      || status === ProjectStatus.RETURNEDTOAPPLICANT;
  }

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
    return this.isAllowedToChangeApplicationFile
      || (this.isThisUserOwner && ActionsCellComponent.isApplicationOpen(this.project?.projectStatus.status));
  }

  private canChangeAssessmentFile(): boolean {
    return this.isAllowedToChangeAssessmentFile
      && (!this.fundingDecisionDefined || this.file.updated > this.project.projectStatus.updated);
  }

  private canDeleteApplicationFile(): boolean {
    // the user can only delete files that are added after a last status change
    const lastStatusChange = this.project?.projectStatus.updated;
    const fileIsNotLocked = this.file.updated > lastStatusChange;

    const userIsAbleToDelete = this.isAllowedToChangeApplicationFile
      || (this.isThisUserOwner && ActionsCellComponent.isApplicationOpen(this.project?.projectStatus.status));
    return fileIsNotLocked && userIsAbleToDelete;
  }

}
