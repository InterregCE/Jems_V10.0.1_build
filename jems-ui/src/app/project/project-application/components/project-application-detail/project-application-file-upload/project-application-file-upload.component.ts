import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProjectStatus} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormGroup} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-project-application-file-upload',
  templateUrl: './project-application-file-upload.component.html',
  styleUrls: ['./project-application-file-upload.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFileUploadComponent extends AbstractForm {
  Alert = Alert;

  @Input()
  uploadPossible: boolean;
  @Input()
  fileNumber: number;
  @Input()
  projectStatus: OutputProjectStatus.StatusEnum;

  @Output()
  uploadFile: EventEmitter<File> = new EventEmitter<File>();

  uploadedFileName: string;

  constructor(protected changeDetectorRef: ChangeDetectorRef, protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);
  }

  addNewFilesForUpload($event: any): void {
    this.uploadedFileName = $event?.target?.files[0]?.name;
    this.uploadFile.emit($event.target.files[0]);
  }

  getForm(): FormGroup | null {
    return null;
  }
}
