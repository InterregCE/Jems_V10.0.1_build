import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {TranslateService} from '@ngx-translate/core';
import { Alert } from '@common/components/forms/alert';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-project-application-file-delete',
  templateUrl: './project-application-file-delete.component.html',
  styleUrls: ['./project-application-file-delete.component.scss']
})
export class ProjectApplicationFileDeleteComponent extends AbstractForm {
  Alert = Alert;

  constructor(protected changeDetectorRef: ChangeDetectorRef, protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);
  }

  getForm(): FormGroup | null {
    return null;
  }

}
