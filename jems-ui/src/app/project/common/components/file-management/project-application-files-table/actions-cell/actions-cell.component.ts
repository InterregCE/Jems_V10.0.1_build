import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProjectFileMetadataDTO, ProjectStatusDTO} from '@cat/api';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {ProjectUtil} from '@project/common/project-util';

@Component({
  selector: 'app-actions-cell',
  templateUrl: './actions-cell.component.html',
  styleUrls: ['./actions-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ActionsCellComponent {
  @Input()
  file: ProjectFileMetadataDTO;
  @Input()
  type: FileCategoryTypeEnum;
  @Input()
  projectStatus: ProjectStatusDTO;

  @Input()
  isAllowedToChangeApplicationFile: boolean;
  @Input()
  isAllowedToChangeAssessmentFile: boolean;
  @Input()
  isThisUserOwner: boolean;

  @Output()
  edit = new EventEmitter<number>();
  @Output()
  download = new EventEmitter<number>();
  @Output()
  delete = new EventEmitter<ProjectFileMetadataDTO>();

  canChangeFile(): boolean {
    if (this.type === FileCategoryTypeEnum.ALL) {
      return false;
    }
    return this.type === FileCategoryTypeEnum.ASSESSMENT
      ? this.canChangeAssessmentFile() : this.canChangeApplicationFile();
  }

  canDeleteFile(): boolean {
    if (this.type === FileCategoryTypeEnum.ALL) {
      return false;
    }
    return this.type === FileCategoryTypeEnum.ASSESSMENT
      ? this.canChangeAssessmentFile() : this.canDeleteApplicationFile();
  }

  private canChangeApplicationFile(): boolean {
    return this.isAllowedToChangeApplicationFile
      || (this.isThisUserOwner && ProjectUtil.isOpenForModifications(this.projectStatus));
  }

  private canChangeAssessmentFile(): boolean {
    return this.isAllowedToChangeAssessmentFile;
  }

  private canDeleteApplicationFile(): boolean {
    // the user can only delete files that are added after a last status change
    const lastStatusChange = this.projectStatus?.updated;
    const fileIsNotLocked = this.file.uploadedAt > lastStatusChange;

    const userIsAbleToDelete = this.isAllowedToChangeApplicationFile
      || (this.isThisUserOwner && ProjectUtil.isOpenForModifications(this.projectStatus));
    return fileIsNotLocked && userIsAbleToDelete;
  }

}
