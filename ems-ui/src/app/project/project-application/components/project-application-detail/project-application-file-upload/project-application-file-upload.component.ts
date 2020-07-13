import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProjectStatus} from '@cat/api'

@Component({
  selector: 'app-project-application-file-upload',
  templateUrl: './project-application-file-upload.component.html',
  styleUrls: ['./project-application-file-upload.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFileUploadComponent {
  OutputProjectStatus = OutputProjectStatus;

  @Input()
  fileNumber: number;
  @Input()
  statusMessages: string[];
  @Input()
  projectStatus: OutputProjectStatus.StatusEnum;
  @Output()
  uploadFile: EventEmitter<File> = new EventEmitter<File>();

  addNewFilesForUpload($event: any): void {
    this.uploadFile.emit($event.target.files[0]);
  }
}
