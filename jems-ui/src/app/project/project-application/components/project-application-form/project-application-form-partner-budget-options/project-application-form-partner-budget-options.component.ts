import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {Tools} from '../../../../../common/utils/tools';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {BudgetOptions} from '../../../model/budget-options';
import {FormState} from '@common/components/forms/form-state';

@Component({
  selector: 'app-project-application-form-partner-budget-options',
  templateUrl: './project-application-form-partner-budget-options.component.html',
  styleUrls: ['./project-application-form-partner-budget-options.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerBudgetOptionsComponent extends ViewEditForm implements OnInit, OnChanges {
  static ID = 'ProjectApplicationFormPartnerBudgetOptionsComponent';
  ProjectApplicationFormPartnerBudgetOptionsComponent = ProjectApplicationFormPartnerBudgetOptionsComponent;
  Tools = Tools;

  @Input()
  editable: boolean;
  @Input()
  officeAdministrationFlatRate: number | null;
  @Input()
  staffCostsFlatRate: number | null;

  @Output()
  saveBudgetOptions = new EventEmitter<BudgetOptions>();

  optionsForm = this.formBuilder.group({
    officeAdministrationFlatRateActive: [''],
    officeAdministrationFlatRate: ['',
                                   Validators.compose([Validators.max(15), Validators.min(1), Validators.required])
    ],
    staffCostsFlatRateActive: [''],
    staffCostsFlatRate: ['',
                         Validators.compose([Validators.max(20), Validators.min(1), Validators.required])
    ]
  });

  officeAdministrationFlatRateErrors = {
    required: 'project.partner.budget.options.flat.rate.empty',
    max: 'project.partner.budget.options.flat.rate.max',
    min: 'project.partner.budget.options.flat.rate.max',
  };

  staffCostsFlatRateErrors = {
    required: 'project.partner.budget.options.staff.costs.flat.rate.empty',
    max: 'project.partner.budget.options.staff.costs.flat.rate.max',
    min: 'project.partner.budget.options.staff.costs.flat.rate.max',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.discard();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.officeAdministrationFlatRate || changes.staffCostsFlatRate) {
      this.discard();
    }
  }

  getForm(): FormGroup | null {
    return this.optionsForm;
  }

  save(): void {
    this.saveBudgetOptions.emit(
      new BudgetOptions(
        this.optionsForm.controls.officeAdministrationFlatRate.value,
        this.optionsForm.controls.staffCostsFlatRate.value
      ));
  }

  discard(): void {
    this.optionsForm.patchValue({
      officeAdministrationFlatRateActive: Number.isInteger(this.officeAdministrationFlatRate as any),
      officeAdministrationFlatRate: this.officeAdministrationFlatRate,
      staffCostsFlatRateActive: Number.isInteger(this.staffCostsFlatRate as any),
      staffCostsFlatRate: this.staffCostsFlatRate
    });
    this.changeFormState$.next(FormState.VIEW);
  }

  protected enterEditMode(): void {
    if (!this.optionsForm.controls.officeAdministrationFlatRateActive.value) {
      this.optionsForm.controls.officeAdministrationFlatRate.disable();
    } else {
      this.optionsForm.controls.officeAdministrationFlatRate.enable();
    }

    if (!this.optionsForm.controls.staffCostsFlatRateActive.value) {
      this.optionsForm.controls.staffCostsFlatRate.disable();
    } else {
      this.optionsForm.controls.staffCostsFlatRate.enable();
    }
  }

  toggleOfficeAdministrationFlatRate(checked: boolean): void {
    this.optionsForm.controls.officeAdministrationFlatRate.patchValue(checked ? 15 : null);
    this.enterEditMode();
  }

  toggleStaffCostsFlatRate(checked: boolean): void {
    this.optionsForm.controls.staffCostsFlatRate.patchValue(checked ? 20 : null);
    this.enterEditMode();
  }
}
