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
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {Permission} from 'src/app/security/permissions/permission';
import {InputProjectPartnership, OutputProjectDescription} from '@cat/api';

@Component({
  selector: 'app-project-application-form-project-partnership-detail',
  templateUrl: './project-application-form-project-partnership-detail.component.html',
  styleUrls: ['./project-application-form-project-partnership-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectPartnershipDetailComponent extends ViewEditForm implements OnInit {
  Permission = Permission;

  @Input()
  editable: boolean;
  @Input()
  project: InputProjectPartnership;
  @Output()
  updateData = new EventEmitter<InputProjectPartnership>();

  projectPartnershipForm: FormGroup = this.formBuilder.group({
    projectPartnership: ['', Validators.maxLength(5000)]
  });

  projectPartnershipErrors = {
    maxlength: 'project.application.form.project.partnership.entered.text.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit() {
    super.ngOnInit();
    this.changeFormState$.next(FormState.VIEW);
  }

  getForm(): FormGroup | null {
    return this.projectPartnershipForm;
  }

  onSubmit(): void {
    this.updateData.emit({
      partnership: this.projectPartnershipForm.controls.projectPartnership.value
    });
  }

  protected enterViewMode(): void {
    this.sideNavService.setAlertStatus(false);
    this.initFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  private initFields() {
    this.projectPartnershipForm.controls.projectPartnership.setValue(this.project?.partnership);
  }
}
