import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormState} from '@common/components/forms/form-state';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {OutputWorkPackage, InputWorkPackageUpdate, InputWorkPackageCreate} from '@cat/api'
import {Permission} from '../../../../../security/permissions/permission';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-project-application-form-work-package-detail',
  templateUrl: './project-application-form-work-package-detail.component.html',
  styleUrls: ['./project-application-form-work-package-detail.component.scss']
})
export class ProjectApplicationFormWorkPackageDetailComponent extends ViewEditForm implements OnInit {
  Permission = Permission;

  @Input()
  workPackage: OutputWorkPackage;
  @Input()
  workPackageNumber: number;
  @Input()
  editable: boolean;
  @Input()
  projectId: number;
  @Output()
  updateData = new EventEmitter<InputWorkPackageUpdate>();
  @Output()
  createDate = new EventEmitter<InputWorkPackageCreate>();

  workPackageForm: FormGroup = this.formBuilder.group({
    workPackageNumber: [''],
    workPackageTitle: ['', Validators.compose([
      Validators.maxLength(100),
      Validators.required])
    ],
    workPackageSpecificObjective: ['', Validators.maxLength(250)],
    workPackageTargetAudience: ['', Validators.maxLength(500)],
  });

  workPackageTitleErrors = {
    maxlength: 'work.package.title.size.too.long',
  };
  workPackageSpecificObjectiveErrors = {
    maxlength: 'work.package.specific.objective.size.too.long'
  }
  workPackageTargetAudienceErrors = {
    maxlength: 'work.package.target.audience.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private activatedRoute: ActivatedRoute,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.editable) {
      this.changeFormState$.next(FormState.EDIT);
    } else {
      this.enterViewMode();
    }
  }

  protected enterViewMode(): void {
    this.sideNavService.setAlertStatus(false);
    this.initFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
    this.workPackageForm.controls.workPackageNumber.disable();
  }

  getForm(): FormGroup | null {
    return this.workPackageForm;
  }

  onSubmit(): void {
    const workPackage = {
      name:  this.workPackageForm.controls.workPackageTitle.value,
      projectSpecificObjective:  this.workPackageForm.controls.workPackageSpecificObjective.value,
      objectiveAndAudience:  this.workPackageForm.controls.workPackageTargetAudience.value,
    }
    if (!this.workPackage.id) {
      this.createDate.emit({
        ...workPackage,
        number: this.workPackageForm.controls.workPackageNumber.value,
        projectId: this.projectId
      });
      return;
    }
    this.updateData.emit({
      ...workPackage,
      id: this.workPackage.id
    });
  }

  private initFields() {
    this.workPackageForm.controls.workPackageNumber.setValue(this.workPackage?.number || this.workPackageNumber);
    this.workPackageForm.controls.workPackageTitle.setValue(this.workPackage?.name);
    this.workPackageForm.controls.workPackageSpecificObjective.setValue(this.workPackage?.projectSpecificObjective);
    this.workPackageForm.controls.workPackageTargetAudience.setValue(this.workPackage?.objectiveAndAudience);
  }

}
