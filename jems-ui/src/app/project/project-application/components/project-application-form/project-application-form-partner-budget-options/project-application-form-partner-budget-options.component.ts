import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {Tools} from '../../../../../common/utils/tools';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BudgetOptions} from '../../../model/budget-options';
import {FormService} from '@common/components/section/form/form.service';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';
import {takeUntil, tap} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
import {BudgetOption} from '../../../model/budget-option';
import {InputCallFlatRateSetup} from '@cat/api';

@Component({
  selector: 'app-project-application-form-partner-budget-options',
  templateUrl: './project-application-form-partner-budget-options.component.html',
  styleUrls: ['./project-application-form-partner-budget-options.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerBudgetOptionsComponent extends BaseComponent implements OnInit, OnChanges {
  Tools = Tools;

  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  editable: boolean;
  @Input()
  budgetOptions: BudgetOption[];

  @Output()
  saveBudgetOptions = new EventEmitter<BudgetOptions>();

  officeAdministrationFlatRatePercent: number;
  officeAdministrationFlatRateFixed: boolean;
  officeAdministrationFlatRateAllowed = false;
  officeAdministrationFlatRateActive = false;
  staffCostsFlatRatePercent: number;
  staffCostsFlatRateFixed: boolean;
  staffCostsFlatRateAllowed = false;
  staffCostsFlatRateActive = false;

  optionsForm = this.formBuilder.group({
    officeAdministrationFlatRateActive: [''],
    officeAdministrationFlatRate: ['', Validators.compose([(control: AbstractControl) => Validators.max(this.officeAdministrationFlatRatePercent)(control), Validators.min(1), Validators.required])],
    staffCostsFlatRateActive: [''],
    staffCostsFlatRate: ['', Validators.compose([(control: AbstractControl) => Validators.max(this.staffCostsFlatRatePercent)(control), Validators.min(1), Validators.required])]
  });

  officeAdministrationFlatRateErrors = {
    required: 'project.partner.budget.options.flat.rate.empty',
    max: 'project.partner.budget.options.flat.rate.max',
    min: 'project.partner.budget.options.flat.rate.max',
  };

  officeAdministrationFlatRateErrorsArgs = {};

  staffCostsFlatRateErrors = {
    required: 'project.partner.budget.options.staff.costs.flat.rate.empty',
    max: 'project.partner.budget.options.staff.costs.flat.rate.max',
    min: 'project.partner.budget.options.staff.costs.flat.rate.max',
  };

  staffCostsFlatRateErrorsArgs = {};

  constructor(private formBuilder: FormBuilder,
              private formService: FormService) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.optionsForm);
    this.prepareFlatRates();
    this.resetForm();
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.partner.budget.options.save.success'))
      )
      .subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.officeAdministrationFlatRate || changes.staffCostsFlatRate) {
      this.resetForm();
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

  resetForm(): void {
    this.optionsForm.patchValue({
      officeAdministrationFlatRateActive: this.officeAdministrationFlatRateActive,
      officeAdministrationFlatRate: this.officeAdministrationFlatRateActive ? this.officeAdministrationFlatRatePercent : null,
      staffCostsFlatRateActive: this.staffCostsFlatRateActive,
      staffCostsFlatRate: this.staffCostsFlatRateActive ? this.staffCostsFlatRatePercent : null
    });
    this.enterEditMode();
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
    this.optionsForm.controls.officeAdministrationFlatRate.patchValue(checked ? this.officeAdministrationFlatRatePercent : null);
    this.enterEditMode();
  }

  toggleStaffCostsFlatRate(checked: boolean): void {
    this.optionsForm.controls.staffCostsFlatRate.patchValue(checked ? this.staffCostsFlatRatePercent : null);
    this.enterEditMode();
  }

  private prepareFlatRates(): void {
    this.budgetOptions.forEach(budgetOption => {
      if (budgetOption.key === InputCallFlatRateSetup.TypeEnum.StaffCost) {
        this.staffCostsFlatRatePercent = budgetOption.value;
        this.staffCostsFlatRateFixed = budgetOption.fixed;
        this.staffCostsFlatRateAllowed = true;
        this.staffCostsFlatRateActive = !budgetOption.isDefault;
      }
      if (budgetOption.key === InputCallFlatRateSetup.TypeEnum.OfficeOnStaff) {
        this.officeAdministrationFlatRatePercent = budgetOption.value;
        this.officeAdministrationFlatRateFixed = budgetOption.fixed;
        this.officeAdministrationFlatRateAllowed = true;
        this.officeAdministrationFlatRateActive = !budgetOption.isDefault;
      }
    });
    this.officeAdministrationFlatRateErrorsArgs = {
      max: {maxValue: this.officeAdministrationFlatRatePercent},
      min: {maxValue: this.officeAdministrationFlatRatePercent}
    };
    this.staffCostsFlatRateErrorsArgs = {
      max: {maxValue: this.staffCostsFlatRatePercent},
      min: {maxvalue: this.staffCostsFlatRatePercent}
    };
  }
}
