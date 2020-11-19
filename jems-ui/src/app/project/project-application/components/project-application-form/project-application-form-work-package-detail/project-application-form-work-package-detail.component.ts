import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputWorkPackageCreate, InputWorkPackageUpdate, OutputWorkPackage} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {takeUntil, tap} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-form-work-package-detail',
  templateUrl: './project-application-form-work-package-detail.component.html',
  styleUrls: ['./project-application-form-work-package-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackageDetailComponent extends BaseComponent implements OnInit, OnChanges {

  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  workPackage: OutputWorkPackage;
  @Input()
  editable: boolean;
  @Input()
  projectId: number;
  @Output()
  updateData = new EventEmitter<InputWorkPackageUpdate>();
  @Output()
  createData = new EventEmitter<InputWorkPackageCreate>();
  @Output()
  cancel = new EventEmitter<void>();

  workPackageNumber: number;

  workPackageForm: FormGroup = this.formBuilder.group({
    workPackageNumber: [''],
    workPackageTitle: ['', Validators.maxLength(100)],
    workPackageSpecificObjective: ['', Validators.maxLength(250)],
    workPackageTargetAudience: ['', Validators.maxLength(500)],
  });

  workPackageTitleErrors = {
    maxlength: 'workpackage.title.size.too.long',
  };
  workPackageSpecificObjectiveErrors = {
    maxlength: 'workpackage.specific.objective.size.too.long'
  };
  workPackageTargetAudienceErrors = {
    maxlength: 'workpackage.target.audience.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService) {
    super();
  }

  ngOnInit(): void {
    this.workPackageNumber = this.workPackage?.number;
    this.workPackageForm.controls.workPackageNumber.disable();
    this.resetForm();

    this.formService.init(this.workPackageForm);
    this.formService.setCreation(!this.workPackage.id);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.application.form.workpackage.save.success'))
      )
      .subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.workPackage) {
      this.workPackageNumber = this.workPackage?.number;
      this.resetForm();
    }
  }

  onSubmit(): void {
    const workPackage = {
      name: this.workPackageForm.controls.workPackageTitle.value,
      specificObjective: this.workPackageForm.controls.workPackageSpecificObjective.value,
      objectiveAndAudience: this.workPackageForm.controls.workPackageTargetAudience.value,
    };
    if (!this.workPackage.id) {
      this.createData.emit(workPackage);
      return;
    }
    this.updateData.emit({
      ...workPackage,
      id: this.workPackage.id
    });
  }

  onCancel(): void {
    if (!this.workPackage.id) {
      this.cancel.emit();
    }
    this.resetForm();
  }

  private resetForm(): void {
    this.workPackageForm.controls.workPackageNumber.setValue(this.workPackage?.number || this.workPackageNumber);
    this.workPackageForm.controls.workPackageTitle.setValue(this.workPackage?.name);
    this.workPackageForm.controls.workPackageSpecificObjective.setValue(this.workPackage?.specificObjective);
    this.workPackageForm.controls.workPackageTargetAudience.setValue(this.workPackage?.objectiveAndAudience);
  }

}
