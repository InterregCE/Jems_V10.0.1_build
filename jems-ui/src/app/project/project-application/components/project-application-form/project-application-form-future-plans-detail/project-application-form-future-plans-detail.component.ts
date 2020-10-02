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
import {Permission} from '../../../../../security/permissions/permission';
import {OutputProjectLongTermPlans, InputProjectLongTermPlans} from '@cat/api'

@Component({
  selector: 'app-project-application-form-future-plans-detail',
  templateUrl: './project-application-form-future-plans-detail.component.html',
  styleUrls: ['./project-application-form-future-plans-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormFuturePlansDetailComponent extends ViewEditForm implements OnInit {
  Permission = Permission;

  @Input()
  editable: boolean;
  @Input()
  project: OutputProjectLongTermPlans;
  @Output()
  updateData = new EventEmitter<InputProjectLongTermPlans>();

  futurePlansForm: FormGroup = this.formBuilder.group({
    ownership: ['', Validators.maxLength(5000)],
    durability: ['', Validators.maxLength(5000)],
    transferability: ['', Validators.maxLength(5000)]
  });

  ownershipErrors = {
    maxlength: 'project.application.form.future.plans.entered.text.size.too.long',
  };
  durabilityErrors = {
    maxlength: 'project.application.form.future.plans.entered.text.size.too.long',
  };
  transferabilityErrors = {
    maxlength: 'project.application.form.future.plans.entered.text.size.too.long',
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
    return this.futurePlansForm;
  }

  protected enterViewMode(): void {
    this.sideNavService.setAlertStatus(false);
    this.initFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  onSubmit(): void {
    this.updateData.emit({
      projectOwnership: this.futurePlansForm.controls.ownership.value,
      projectDurability: this.futurePlansForm.controls.durability.value,
      projectTransferability: this.futurePlansForm.controls.transferability.value
    });
  }

  private initFields() {
    this.futurePlansForm.controls.ownership.setValue(this.project?.projectOwnership);
    this.futurePlansForm.controls.durability.setValue(this.project?.projectDurability);
    this.futurePlansForm.controls.transferability.setValue(this.project?.projectTransferability);
  }

}
