import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-project-application-file-upload',
  templateUrl: './project-application-file-upload.component.html',
  styleUrls: ['./project-application-file-upload.component.scss']
})
export class ProjectApplicationFileUploadComponent {
  @Input()
  fileNumber: number;
  @Input()
  statusMessages: string[];
  @Output()
  uploadFile: EventEmitter<File> = new EventEmitter<File>();

  addNewFilesForUpload($event: any): void {
    this.uploadFile.emit($event.target.files[0]);
  }
}
