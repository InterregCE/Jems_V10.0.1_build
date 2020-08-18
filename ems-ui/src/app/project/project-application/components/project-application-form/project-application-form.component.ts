import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProjectData, OutputProject} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form',
  templateUrl: './project-application-form.component.html',
  styleUrls: ['./project-application-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormComponent extends ViewEditForm implements OnInit {
  Permission = Permission;

  @Input()
  project: OutputProject;
  @Input()
  editable: boolean;

  @Output()
  updateData = new EventEmitter<InputProjectData>();

  applicationForm: FormGroup = this.formBuilder.group({
    projectId: [''],
    projectAcronym: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.required])
    ],
    projectTitle: ['', Validators.maxLength(250)],
    projectDuration: ['', Validators.compose([
      Validators.max(999),
      Validators.min(1)
    ])],
    projectSummary: ['', Validators.maxLength(2000)]
  });

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
  projectSummaryErrors = {
    maxlength: 'project.summary.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit() {
    super.ngOnInit();
    this.enterViewMode();
  }

  protected enterViewMode(): void {
    this.applicationForm.controls.projectId.setValue(this.project.id);
    this.applicationForm.controls.projectAcronym.setValue(this.project.acronym);
    this.applicationForm.controls.projectTitle.setValue(this.project?.projectData?.title);
    this.applicationForm.controls.projectDuration.setValue(this.project?.projectData?.duration);
    this.applicationForm.controls.projectSummary.setValue(this.project?.projectData?.introProgrammeLanguage);
  }

  protected enterEditMode(): void {
    this.applicationForm.controls.projectId.disable();
  }

  getForm(): FormGroup | null {
    return this.applicationForm;
  }

  onSubmit(): void {
    this.updateData.emit({
      acronym: this.applicationForm.controls.projectAcronym.value,
      title: this.applicationForm.controls.projectTitle.value,
      duration: this.applicationForm.controls.projectDuration.value,
      introProgrammeLanguage: this.applicationForm.controls.projectSummary.value,
      intro: ''
    });
  }
}
