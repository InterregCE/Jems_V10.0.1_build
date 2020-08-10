import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {OutputProject} from '@cat/api';
import {FormGroup} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';

@Component({
  selector: 'app-project-application-identification',
  templateUrl: './project-application-identification.component.html',
  styleUrls: ['./project-application-identification.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationIdentificationComponent extends AbstractForm implements OnInit {
  @Input()
  project: OutputProject;
  @Input()
  identificationForm: FormGroup;
  @Input()
  disabled: boolean;

  projectAcronymErrors = {
    maxlength: 'project.acronym.size.too.long',
    required: 'project.acronym.should.not.be.empty'
  };
  projectTitleErrors = {
    maxlength: 'project.title.size.too.long',
  };
  projectDurationErrors = {
    max: 'project.duration.size.max',
    min: 'project.duration.size.max',
  };

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return this.identificationForm;
  }

  ngOnInit() {
    super.ngOnInit();
    this.identificationForm.controls.projectId.setValue(this.project.id);
    this.identificationForm.controls.projectAcronym.setValue(this.project.acronym);
  }

}
