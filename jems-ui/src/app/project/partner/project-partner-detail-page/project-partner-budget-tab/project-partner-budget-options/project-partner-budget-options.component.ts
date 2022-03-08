import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Tools} from '@common/utils/tools';
import {FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {BudgetOptions} from '@project/model/budget/budget-options';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {FlatRateSetting} from '@project/model/flat-rate-setting';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {CallFlatRateSetting} from '@project/model/call-flat-rate-setting';
import {ProjectPartnerBudgetOptionsConstants} from './project-partner-budget-options.constants';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProjectPartnerBudgetTabService} from '../project-partner-budget-tab.service';
import {PartnerBudgetTables} from '@project/model/budget/partner-budget-tables';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';
import {Alert} from '@common/components/forms/alert';

const flatRateValidator: (control: FormControl) => ValidatorFn = (checkBoxControl: FormControl) => (valueControl: FormControl): ValidationErrors | null => {
  if (checkBoxControl?.value && valueControl === null) {
    return {required: true};
  }
  return null;
};

@UntilDestroy()
@Component({
  selector: 'jems-project-partner-budget-options',
  templateUrl: './project-partner-budget-options.component.html',
  styleUrls: ['./project-partner-budget-options.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetOptionsComponent implements OnInit {

  constants = ProjectPartnerBudgetOptionsConstants;
  Tools = Tools;
  Alert = Alert;

  budgetOptionForm: FormGroup;
  data$: Observable<{
    callFlatRateSettings: CallFlatRateSetting;
    flatRateErrorsArgs: {
      staff: { [key: string]: {} };
      officeOnStaff: { [key: string]: {} };
      officeOnDirect: { [key: string]: {} };
      travel: { [key: string]: {} };
      other: { [key: string]: {} };
    };
    isDividerVisible: boolean;
    isAnyOptionAvailable: boolean;
    budgets: PartnerBudgetTables;
  }>;

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private tabService: ProjectPartnerBudgetTabService,
              public pageStore: ProjectPartnerDetailPageStore) {
  }


  ngOnInit(): void {
    this.initForm();
    this.handleResetForm();
    this.handleBudgetOptionsState();
    this.formService.reset();

    this.data$ = combineLatest([
      this.pageStore.callFlatRatesSettings$,
      this.flatRateErrorsArgs(),
      this.isDividerVisible(),
      this.isAnyOptionAvailable(),
      this.pageStore.budgets$
    ]).pipe(
      map(([callFlatRateSettings, flatRateErrorsArgs, isDividerVisible, isAnyOptionAvailable, budgets]: any) => {
        return {
          callFlatRateSettings,
          flatRateErrorsArgs,
          isDividerVisible,
          isAnyOptionAvailable,
          budgets
        };
      }));
  }

  updateBudgetOptions(budgetOptions: BudgetOptions): void {
    this.formService.setDirty(true);
    this.doUpdateBudgetOptions(budgetOptions);
  }

  realCostsConfirmationConfig(options: BudgetOptions, tables: PartnerBudgetTables): ConfirmDialogData | null {
    return this.wouldSelectedFlatRatesAffectRealCosts(options, tables)
      ? {
        title: 'project.partner.budget.options.changes.warning.title',
        message: 'project.partner.budget.options.changes.warning.message'
      }
      : null;
  }

  formToBudgetOptions(callFlatRateSettings: CallFlatRateSetting): BudgetOptions {
    const isStaffCostActive = callFlatRateSettings.staffCostFlatRateSetup !== null && this.isStaffCostsFlatRateSelected?.value;
    const isOfficeOnStaffActive = callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup !== null && this.isOfficeOnStaffSelected?.value;
    const isOfficeOnDirectActive = callFlatRateSettings.officeAndAdministrationOnDirectCostsFlatRateSetup !== null && this.isOfficeOnDirectCostsSelected?.value;
    const isTravelActive = callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup !== null && this.isTravelOnStaffCostsSelected?.value;
    const isOtherActive = callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup !== null && this.isOtherCostsOnStaffCostsSelected?.value;
    return new BudgetOptions(
      isOfficeOnStaffActive ? this.officeOnStaffCostsFlatRate?.value || null : null,
      isOfficeOnDirectActive ? this.officeOnDirectCostsFlatRate?.value || null : null,
      isStaffCostActive ? this.staffCostsFlatRate?.value || null : null,
      isTravelActive ? this.travelOnStaffCostsFlatRate?.value || null : null,
      isOtherActive ? this.otherCostsOnStaffCostsFlatRate?.value || null : null,
    );
  }

  toggleFlatRate(checkboxFormControl: FormControl, valueFormControl: FormControl, flatRateSetting: FlatRateSetting, checked: boolean): void {
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

  doUpdateBudgetOptions(budgetOptions: BudgetOptions): void {
    this.pageStore.updateBudgetOptions(budgetOptions).pipe(
      tap(() => this.formService.setDirty(false)),
      tap(() => this.formService.setSuccess('project.partner.budget.options.save.success')),
      catchError((error: HttpErrorResponse) => {
        this.formService.setDirty(false);
        return this.formService.setError(error);
      }),
      untilDestroyed(this)
    ).subscribe();
  }

  private wouldSelectedFlatRatesAffectRealCosts(budgetOptions: BudgetOptions, budgets: PartnerBudgetTables): boolean {
    if (budgetOptions.staffCostsFlatRate !== null && budgets.staffCosts.entries.length > 0) {
      return true;
    }
    if (budgetOptions.travelAndAccommodationOnStaffCostsFlatRate !== null && budgets.travelCosts.entries.length > 0) {
      return true;
    }
    return budgetOptions.otherCostsOnStaffCostsFlatRate !== null &&
      ((budgets.travelCosts.entries.length > 0) ||
        (budgets.infrastructureCosts.entries.length > 0) ||
        (budgets.equipmentCosts.entries.length > 0) ||
        (budgets.externalCosts.entries.length > 0) ||
        (budgets.unitCosts.entries.length > 0));

  }

  private initForm(): void {
    this.budgetOptionForm = this.formBuilder.group({
      StaffCost: [null],
      isStaffCostSelected: [false],
      OfficeOnStaff: [null],
      isOfficeOnStaffSelected: [false],
      OfficeOnOther: [null],
      isOfficeOnOtherSelected: [false],
      TravelOnStaff: [null],
      isTravelOnStaffSelected: [false],
      OtherOnStaff: [null],
      isOtherOnStaffSelected: [false],
    });
    this.formService.init(this.budgetOptionForm, this.pageStore.isProjectEditable$);
    this.tabService.trackBudgetOptionsFormState(this.formService);
  }

  private handleResetForm(): void {
    combineLatest([this.formService.reset$.pipe(startWith(null)), this.pageStore.budgetOptions$, this.pageStore.callFlatRatesSettings$]).pipe(
      map(([, budgetOptions, callFlatRateSettings]) => this.resetForm(budgetOptions, callFlatRateSettings)),
      untilDestroyed(this)
    ).subscribe();
  }

  private resetForm(budgetOptions: BudgetOptions, callFlatRateSettings: CallFlatRateSetting): void {
    if (callFlatRateSettings.staffCostFlatRateSetup !== null) {
      this.isStaffCostsFlatRateSelected.setValue(budgetOptions.staffCostsFlatRate !== null);
      this.staffCostsFlatRate.setValue(budgetOptions.staffCostsFlatRate || callFlatRateSettings?.staffCostFlatRateSetup?.rate);
      this.staffCostsFlatRate.setValidators([Validators.max(callFlatRateSettings?.staffCostFlatRateSetup?.rate), Validators.min(1), flatRateValidator(this.isStaffCostsFlatRateSelected)]);
    }
    if (callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup !== null) {
      this.isOfficeOnStaffSelected.setValue(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate !== null);
      this.officeOnStaffCostsFlatRate.setValue(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate || callFlatRateSettings?.officeAndAdministrationOnStaffCostsFlatRateSetup?.rate);
      this.officeOnStaffCostsFlatRate.setValidators([Validators.max(callFlatRateSettings?.officeAndAdministrationOnStaffCostsFlatRateSetup?.rate), Validators.min(1), flatRateValidator(this.isOfficeOnStaffSelected)]);
    }

    if (callFlatRateSettings.officeAndAdministrationOnDirectCostsFlatRateSetup !== null) {
      this.isOfficeOnDirectCostsSelected.setValue(budgetOptions.officeAndAdministrationOnDirectCostsFlatRate !== null);
      this.officeOnDirectCostsFlatRate.setValue(budgetOptions.officeAndAdministrationOnDirectCostsFlatRate || callFlatRateSettings?.officeAndAdministrationOnDirectCostsFlatRateSetup?.rate);
      this.officeOnDirectCostsFlatRate.setValidators([Validators.max(callFlatRateSettings?.officeAndAdministrationOnDirectCostsFlatRateSetup?.rate), Validators.min(1), flatRateValidator(this.isOfficeOnDirectCostsSelected)]);
    }

    if (callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup !== null) {
      this.isTravelOnStaffCostsSelected.setValue(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate !== null);
      this.travelOnStaffCostsFlatRate.setValue(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate || callFlatRateSettings?.travelAndAccommodationOnStaffCostsFlatRateSetup?.rate);
      this.travelOnStaffCostsFlatRate.setValidators([Validators.max(callFlatRateSettings?.travelAndAccommodationOnStaffCostsFlatRateSetup?.rate), Validators.min(1), flatRateValidator(this.isTravelOnStaffCostsSelected)]);
    }

    if (callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup !== null) {
      this.isOtherCostsOnStaffCostsSelected.patchValue(budgetOptions.otherCostsOnStaffCostsFlatRate !== null);
      this.otherCostsOnStaffCostsFlatRate.setValue(budgetOptions.otherCostsOnStaffCostsFlatRate || callFlatRateSettings?.otherCostsOnStaffCostsFlatRateSetup?.rate);
      this.otherCostsOnStaffCostsFlatRate.setValidators([Validators.max(callFlatRateSettings?.otherCostsOnStaffCostsFlatRateSetup?.rate), Validators.min(1), flatRateValidator(this.isOtherCostsOnStaffCostsSelected)]);
    }
  }

  private flatRateErrorsArgs(): Observable<{ [key: string]: {} }> {
    return this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => {
        return {
          staff: this.getFlatRateErrorArgs(callFlatRateSettings.staffCostFlatRateSetup),
          officeOnStaff: this.getFlatRateErrorArgs(callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup),
          officeOnDirect: this.getFlatRateErrorArgs(callFlatRateSettings.officeAndAdministrationOnDirectCostsFlatRateSetup),
          travel: this.getFlatRateErrorArgs(callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup),
          other: this.getFlatRateErrorArgs(callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup)
        };
      }),
    );

  }

  private getFlatRateErrorArgs(flatRateSetting: FlatRateSetting | null): { [key: string]: {} } {
    return {
      max: {maxValue: flatRateSetting?.rate},
      min: {maxValue: flatRateSetting?.rate}
    };
  }

  private isDividerVisible(): Observable<boolean> {
    return this.pageStore.callFlatRatesSettings$.pipe(
      map(flatRateSetting => flatRateSetting.otherCostsOnStaffCostsFlatRateSetup !== null && (flatRateSetting.travelAndAccommodationOnStaffCostsFlatRateSetup !== null || flatRateSetting.officeAndAdministrationOnStaffCostsFlatRateSetup !== null || flatRateSetting.officeAndAdministrationOnDirectCostsFlatRateSetup !== null || flatRateSetting.staffCostFlatRateSetup !== null))
    );
  }

  private isAnyOptionAvailable(): Observable<boolean> {
    return this.pageStore.callFlatRatesSettings$.pipe(map(callFlatRateSetting => callFlatRateSetting.staffCostFlatRateSetup !== null || callFlatRateSetting.officeAndAdministrationOnStaffCostsFlatRateSetup !== null || callFlatRateSetting.officeAndAdministrationOnDirectCostsFlatRateSetup !== null || callFlatRateSetting.travelAndAccommodationOnStaffCostsFlatRateSetup !== null || callFlatRateSetting.otherCostsOnStaffCostsFlatRateSetup !== null));
  }

  private handleBudgetOptionsState(): void {
    this.handleStaffCostsAndTravelCostsOptionsState();
    this.handleOtherCostsOptionState();
    this.handleOfficeOnStaffCostsOptionState();
    this.handleOfficeOnDirectCostsOptionState();
  }

  private handleStaffCostsAndTravelCostsOptionsState(): void {
    combineLatest([
      this.tabService.isBudgetFormInEditMode$.pipe(startWith(false)),
      this.pageStore.isProjectEditable$,
      this.isOtherCostsOnStaffCostsSelected.valueChanges.pipe(startWith(null)),
      this.pageStore.canChangeContractedFlatRates$,
      this.formService.reset$.pipe(startWith(null))
    ]).pipe(
      tap(([isBudgetFormInEditMode, isProjectEditable, isOtherCostSelected, canChangeContractedFlatRates]) => {
        const isDisabled = isBudgetFormInEditMode || !isProjectEditable || isOtherCostSelected;
        this.setControlsState(isDisabled, [this.isStaffCostsFlatRateSelected, this.staffCostsFlatRate, this.isTravelOnStaffCostsSelected, this.travelOnStaffCostsFlatRate]);
        if (!canChangeContractedFlatRates) {
          this.disableCheckboxes();
        }
      }),
      untilDestroyed(this)
    ).subscribe();
  }

  private handleOtherCostsOptionState(): void {
    combineLatest([
      this.tabService.isBudgetFormInEditMode$.pipe(startWith(false)),
      this.pageStore.isProjectEditable$,
      this.isStaffCostsFlatRateSelected.valueChanges.pipe(startWith(null)),
      this.isOfficeOnDirectCostsSelected.valueChanges.pipe(startWith(null)),
      this.isOfficeOnStaffSelected.valueChanges.pipe(startWith(null)),
      this.isTravelOnStaffCostsSelected.valueChanges.pipe(startWith(null)),
      this.pageStore.canChangeContractedFlatRates$,
      this.formService.reset$.pipe(startWith(null))
    ]).pipe(
      tap(([isBudgetFormInEditMode, isProjectEditable, isStaffCostsFlatRateSelected, isOfficeOnDirectCostsSelected, isOfficeOnStaffSelected, isTravelOnStaffCostsSelected, canChangeContractedFlatRates]) => {
        const isDisabled = isBudgetFormInEditMode || !isProjectEditable || isStaffCostsFlatRateSelected || isOfficeOnDirectCostsSelected || isOfficeOnStaffSelected || isTravelOnStaffCostsSelected;
        this.setControlsState(isDisabled, [this.isOtherCostsOnStaffCostsSelected, this.otherCostsOnStaffCostsFlatRate]);
        if (!canChangeContractedFlatRates) {
          this.disableCheckboxes();
        }
      })
    ).subscribe();
  }

  private handleOfficeOnStaffCostsOptionState(): void {
    combineLatest([
      this.tabService.isBudgetFormInEditMode$.pipe(startWith(false)),
      this.pageStore.isProjectEditable$,
      this.isOtherCostsOnStaffCostsSelected.valueChanges.pipe(startWith(null)),
      this.isOfficeOnDirectCostsSelected.valueChanges.pipe(startWith(null)),
      this.pageStore.canChangeContractedFlatRates$,
      this.formService.reset$.pipe(startWith(null))
    ]).pipe(
      tap(([isBudgetFormInEditMode, isProjectEditable, isOtherCostSelected, isOfficeOnDirectCostSelected, canChangeContractedFlatRates]) => {
        const isDisabled = isBudgetFormInEditMode || !isProjectEditable || isOtherCostSelected || isOfficeOnDirectCostSelected;
        this.setControlsState(isDisabled, [this.isOfficeOnStaffSelected, this.officeOnStaffCostsFlatRate]);
        if (!canChangeContractedFlatRates) {
          this.disableCheckboxes();
        }
      }),
      untilDestroyed(this)
    ).subscribe();
  }

  private handleOfficeOnDirectCostsOptionState(): void {
    combineLatest([
      this.tabService.isBudgetFormInEditMode$.pipe(startWith(false)),
      this.pageStore.isProjectEditable$,
      this.isOtherCostsOnStaffCostsSelected.valueChanges.pipe(startWith(null)),
      this.isOfficeOnStaffSelected.valueChanges.pipe(startWith(null)),
      this.pageStore.canChangeContractedFlatRates$,
      this.formService.reset$.pipe(startWith(null)),
    ]).pipe(
      tap(([isBudgetFormInEditMode, isProjectEditable, isOtherCostSelected, isOfficeOnStaffCostSelected, canChangeContractedFlatRates]) => {
        const isDisabled = isBudgetFormInEditMode || !isProjectEditable || isOtherCostSelected || isOfficeOnStaffCostSelected;
        this.setControlsState(isDisabled, [this.isOfficeOnDirectCostsSelected, this.officeOnDirectCostsFlatRate]);
        if (!canChangeContractedFlatRates) {
          this.disableCheckboxes();
        }
      }),
      untilDestroyed(this)
    ).subscribe();
  }

  private setControlsState(isDisabled: boolean, controls: FormControl[]): void {
    if (isDisabled) {
      this.disableControls(controls);
    } else {
      this.enableControls(controls);
    }
  }

  private enableControls(controls: FormControl[]): void {
    for (const control of controls) {
      control.enable({emitEvent: false});
    }
  }

  private disableControls(controls: FormControl[]): void {
    for (const control of controls) {
      control.disable({emitEvent: false});
    }
  }

  private disableCheckboxes(): void {
    this.setControlsState(true, [
      this.isStaffCostsFlatRateSelected,
      this.isTravelOnStaffCostsSelected,
      this.isOtherCostsOnStaffCostsSelected,
      this.isOfficeOnStaffSelected,
      this.isOfficeOnDirectCostsSelected
    ]);
  }

  get staffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.staffCostsFlatRate) as FormControl;
  }

  get isStaffCostsFlatRateSelected(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isStaffCostsFlatRateSelected) as FormControl;
  }

  get officeOnStaffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.officeOnStaffCostsFlatRate) as FormControl;
  }

  get isOfficeOnStaffSelected(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isOfficeOnStaffCostsFlatRateSelected) as FormControl;
  }

  get officeOnDirectCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.officeOnDirectCostsFlatRate) as FormControl;
  }

  get isOfficeOnDirectCostsSelected(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isOfficeOnDirectCostsFlatRateSelected) as FormControl;
  }

  get travelOnStaffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.travelAndAccommodationOnStaffCostsFlatRate) as FormControl;
  }

  get isTravelOnStaffCostsSelected(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isTravelAndAccommodationOnStaffCostsFlatRateSelected) as FormControl;
  }

  get otherCostsOnStaffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.otherCostsOnStaffCostsFlatRate) as FormControl;
  }

  get isOtherCostsOnStaffCostsSelected(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isOtherCostsOnStaffCostsFlatRateSelected) as FormControl;
  }

}
