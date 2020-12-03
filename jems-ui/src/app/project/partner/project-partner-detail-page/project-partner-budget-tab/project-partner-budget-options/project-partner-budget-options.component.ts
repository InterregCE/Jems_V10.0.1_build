import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Tools} from '../../../../../common/utils/tools';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {BudgetOptions} from '../../../../project-application/model/budget-options';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable, Subject} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {FlatRateSetting} from '../../../../project-application/model/flat-rate-setting';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {CallFlatRateSetting} from '../../../../project-application/model/call-flat-rate-setting';
import {
  BUDGET_OPTIONS_FORM_CONTROL_NAMES,
  BUDGET_OPTIONS_FORM_ERRORS
} from './project-partner-budget-options.constants';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-project-partner-budget-options',
  templateUrl: './project-partner-budget-options.component.html',
  styleUrls: ['./project-partner-budget-options.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetOptionsComponent implements OnInit {

  FORM_CONTROL_NAMES = BUDGET_OPTIONS_FORM_CONTROL_NAMES;
  FORM_ERRORS = BUDGET_OPTIONS_FORM_ERRORS;
  Tools = Tools;

  data$: Observable<{
    budgetOptionsForm: FormGroup
    callFlatRateSettings: CallFlatRateSetting,
    staffCostsFlatRateErrorsArgs: { [key: string]: {} },
    officeOnStaffCostFlatRateErrorsArgs: { [key: string]: {} },
    travelFlatRateErrorsArgs: { [key: string]: {} }
    isProjectEditable: boolean
  }>;

  private resetFormEvent$ = new Subject();
  private budgetOptionsForm$: Observable<FormGroup>;
  private staffCostsFlatRateErrorsArgs$: Observable<{ [key: string]: {} }>;
  private officeOnStaffCostFlatRateErrorsArgs$: Observable<{ [key: string]: {} }>;
  private travelFlatRateErrorsArgs$: Observable<{ [key: string]: {} }>;


  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private pageStore: ProjectPartnerDetailPageStore
  ) {
  }

  ngOnInit(): void {
    this.budgetOptionsForm$ = combineLatest([this.resetFormEvent$.pipe(startWith(null)), this.pageStore.budgetOptions$, this.pageStore.callFlatRatesSettings$]).pipe(
      map(([, budgetOptions, callFlatRateSettings]) => this.initForm(budgetOptions, callFlatRateSettings)),
    );
    this.staffCostsFlatRateErrorsArgs$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => this.getFlatRateErrorArgs(callFlatRateSettings.staffCostBasedOnDirectCost)),
    );
    this.officeOnStaffCostFlatRateErrorsArgs$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => this.getFlatRateErrorArgs(callFlatRateSettings.officeBasedOnStaffCost))
    );
    this.travelFlatRateErrorsArgs$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => this.getFlatRateErrorArgs(callFlatRateSettings.travelBasedOnStaffCost))
    );

    this.data$ = combineLatest([
      this.budgetOptionsForm$,
      this.pageStore.callFlatRatesSettings$,
      this.pageStore.isProjectEditable$.pipe(startWith(false)),
      this.staffCostsFlatRateErrorsArgs$,
      this.officeOnStaffCostFlatRateErrorsArgs$,
      this.travelFlatRateErrorsArgs$
    ]).pipe(
      map(([budgetOptionsForm, callFlatRateSettings, isProjectEditable, staffCostsFlatRateErrorsArgs, officeOnStaffCostFlatRateErrorsArgs, travelFlatRateErrorsArgs]) => {
        return {
          budgetOptionsForm,
          callFlatRateSettings,
          isProjectEditable,
          staffCostsFlatRateErrorsArgs,
          officeOnStaffCostFlatRateErrorsArgs,
          travelFlatRateErrorsArgs
        };
      }));
  }

  updateBudgetOptions(budgetOptions: BudgetOptions): void {
    this.pageStore.updateBudgetOptions(budgetOptions).pipe(
      tap(() => this.formService.setSuccess('project.partner.budget.options.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  formToBudgetOptions(form: FormGroup): BudgetOptions {
    const isStaffCostActive = this.getFormControl(form, BUDGET_OPTIONS_FORM_CONTROL_NAMES.isStaffCostsFlatRateBasedOnDirectCostActive)?.value;
    const isOfficeActive = this.getFormControl(form, BUDGET_OPTIONS_FORM_CONTROL_NAMES.isOfficeFlatRateBasedOnStaffCostActive)?.value;
    const isTravelActive = this.getFormControl(form, BUDGET_OPTIONS_FORM_CONTROL_NAMES.isTravelFlatRateBasedOnStaffCostActive)?.value;
    return new BudgetOptions(
      isOfficeActive ? this.getFormControl(form, BUDGET_OPTIONS_FORM_CONTROL_NAMES.officeFlatRateBasedOnStaffCost)?.value || null : null,
      isStaffCostActive ? this.getFormControl(form, BUDGET_OPTIONS_FORM_CONTROL_NAMES.staffCostsFlatRateBasedOnDirectCost)?.value || null : null,
      isTravelActive ? this.getFormControl(form, BUDGET_OPTIONS_FORM_CONTROL_NAMES.travelFlatRateBasedOnStaffCost)?.value || null : null,
    );
  }

  toggleFlatRate(form: FormGroup, checkboxFormControlName: string, valueFormControlName: string, flatRateSetting: FlatRateSetting, checked: boolean): void {
    const checkboxFormControl = this.getFormControl(form, checkboxFormControlName);
    const valueFormControl = this.getFormControl(form, valueFormControlName);
    checkboxFormControl
      .patchValue(checked ? checkboxFormControl?.value : null);
    if (checked) {
      valueFormControl.enable();
      valueFormControl.setValue(flatRateSetting.rate);
    } else {
      valueFormControl.disable();
      valueFormControl.setValue(null);
    }
  }

  getFormControl(form: FormGroup, controlName: string): FormControl {
    return form.get(controlName) as FormControl;
  }

  resetForm(): void {
    this.resetFormEvent$.next();
  }

  private initForm(budgetOptions: BudgetOptions, callFlatRateSettings: CallFlatRateSetting): FormGroup {
    const form = this.formBuilder.group({});
    if (callFlatRateSettings.staffCostBasedOnDirectCost !== null) {
      form.addControl(BUDGET_OPTIONS_FORM_CONTROL_NAMES.isStaffCostsFlatRateBasedOnDirectCostActive, this.formBuilder.control(budgetOptions.staffCostsFlatRateBasedOnDirectCost !== null));
      form.addControl(BUDGET_OPTIONS_FORM_CONTROL_NAMES.staffCostsFlatRateBasedOnDirectCost, this.formBuilder.control(
        budgetOptions.staffCostsFlatRateBasedOnDirectCost ? budgetOptions.staffCostsFlatRateBasedOnDirectCost : callFlatRateSettings.staffCostBasedOnDirectCost.rate,
        [Validators.max(callFlatRateSettings.staffCostBasedOnDirectCost.rate), Validators.min(1), Validators.required]));
    }
    if (callFlatRateSettings.officeBasedOnStaffCost !== null) {
      form.addControl(BUDGET_OPTIONS_FORM_CONTROL_NAMES.isOfficeFlatRateBasedOnStaffCostActive, this.formBuilder.control(budgetOptions.officeFlatRateBasedOnStaffCost !== null));
      form.addControl(BUDGET_OPTIONS_FORM_CONTROL_NAMES.officeFlatRateBasedOnStaffCost, this.formBuilder.control(
        budgetOptions.officeFlatRateBasedOnStaffCost ? budgetOptions.officeFlatRateBasedOnStaffCost : callFlatRateSettings.officeBasedOnStaffCost.rate,
        [Validators.max(callFlatRateSettings.officeBasedOnStaffCost.rate), Validators.min(1), Validators.required]));
    }
    if (callFlatRateSettings.travelBasedOnStaffCost !== null) {
      form.addControl(BUDGET_OPTIONS_FORM_CONTROL_NAMES.isTravelFlatRateBasedOnStaffCostActive, this.formBuilder.control(budgetOptions.travelFlatRateBasedOnStaffCost !== null));
      form.addControl(BUDGET_OPTIONS_FORM_CONTROL_NAMES.travelFlatRateBasedOnStaffCost, this.formBuilder.control(
        budgetOptions.travelFlatRateBasedOnStaffCost ? budgetOptions.travelFlatRateBasedOnStaffCost : callFlatRateSettings.travelBasedOnStaffCost.rate,
        [Validators.max(callFlatRateSettings.travelBasedOnStaffCost.rate), Validators.min(1), Validators.required]));
    }
    this.formService.init(form);
    return form;
  }

  private getFlatRateErrorArgs(flatRateSetting: FlatRateSetting | null): { [key: string]: {} } {
    return {
      max: {maxValue: flatRateSetting?.rate},
      min: {maxValue: flatRateSetting?.rate}
    };
  }

}
