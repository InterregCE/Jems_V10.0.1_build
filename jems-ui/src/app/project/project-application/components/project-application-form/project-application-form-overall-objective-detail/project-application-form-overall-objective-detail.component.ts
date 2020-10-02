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
import {InputProjectOverallObjective, OutputProjectDescription, OutputProgrammePriorityPolicySimple} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-project-application-form-overall-objective-detail',
  templateUrl: './project-application-form-overall-objective-detail.component.html',
  styleUrls: ['./project-application-form-overall-objective-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormOverallObjectiveDetailComponent extends ViewEditForm implements OnInit {
  Permission = Permission;

@Input()
  editable: boolean;
@Input()
  project: InputProjectOverallObjective;
@Input()
specificObjective: OutputProgrammePriorityPolicySimple;
@Output()
  updateData = new EventEmitter<InputProjectOverallObjective>();

  overallObjectiveForm: FormGroup = this.formBuilder.group({
    projectSpecificObjective: ['', []],
    projectOverallObjective: ['', Validators.maxLength(500)]
  });

  projectOverallObjectiveError = {
    maxlength: 'project.application.form.overall.objective.entered.text.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private sideNavService: SideNavService,
              private translate: TranslateService) {
    super(changeDetectorRef);
  }

  ngOnInit() {
    super.ngOnInit();
    this.changeFormState$.next(FormState.VIEW);
  }

  getForm(): FormGroup | null {
    return this.overallObjectiveForm;
  }

  onSubmit(): void {
    this.updateData.emit({
      overallObjective: this.overallObjectiveForm.controls.projectOverallObjective.value
    });
  }

  protected enterViewMode(): void {
    this.sideNavService.setAlertStatus(false);
    this.initFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
    if (this.specificObjective) {
      this.overallObjectiveForm.controls.projectSpecificObjective.setValue(this.translate.instant('programme.policy.' + this.specificObjective.programmeObjectivePolicy));
    } else {
      this.overallObjectiveForm.controls.projectSpecificObjective.markAsTouched();
      this.changeDetectorRef.detectChanges();
    }
  }

  private initFields() {
    if (this.specificObjective) {
      this.overallObjectiveForm.controls.projectSpecificObjective.setValue(this.translate.instant('programme.policy.' + this.specificObjective.programmeObjectivePolicy));
    }
    this.overallObjectiveForm.controls.projectOverallObjective.setValue(this.project?.overallObjective);
  }
}
