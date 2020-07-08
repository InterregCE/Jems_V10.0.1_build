import {Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProject} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-data',
  templateUrl: './project-application-data.component.html',
  styleUrls: ['./project-application-data.component.scss']
})
export class ProjectApplicationDataComponent {

  @Input()
  project = {} as OutputProject;
  @Input()
  fileNumber: number;
  @Input()
  statusMessages: string[];
  @Input()
  Permission = Permission;
  @Output()
  uploadFile: EventEmitter<File> = new EventEmitter<File>();

  addNewFilesForUpload($event: any) {
    this.uploadFile.emit($event.target.files[0]);
  }
}
