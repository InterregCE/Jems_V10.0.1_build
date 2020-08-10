import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input} from '@angular/core';
import {OutputProject} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-project-application-summary',
  templateUrl: './project-application-summary.component.html',
  styleUrls: ['./project-application-summary.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationSummaryComponent extends AbstractForm {
  @Input()
  project: OutputProject;
  @Input()
  summaryForm: FormGroup;
  @Input()
  disabled: boolean;

  projectSummaryErrors = {
    maxlength: 'project.summary.size.too.long'
  };

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return this.summaryForm;
  }

}
