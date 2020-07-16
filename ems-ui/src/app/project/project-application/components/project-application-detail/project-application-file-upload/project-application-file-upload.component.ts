import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProjectStatus} from '@cat/api'
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-file-upload',
  templateUrl: './project-application-file-upload.component.html',
  styleUrls: ['./project-application-file-upload.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFileUploadComponent {
  OutputProjectStatus = OutputProjectStatus;
  Permission = Permission;

  @Input()
  fileNumber: number;
  @Input()
  statusMessages: string[];
  @Input()
  projectStatus: OutputProjectStatus.StatusEnum;
  @Input()
  permission: Permission;

  @Output()
  uploadFile: EventEmitter<File> = new EventEmitter<File>();

  addNewFilesForUpload($event: any): void {
    this.uploadFile.emit($event.target.files[0]);
  }
}
