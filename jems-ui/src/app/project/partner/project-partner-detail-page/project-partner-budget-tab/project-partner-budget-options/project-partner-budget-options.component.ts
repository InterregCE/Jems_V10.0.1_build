import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Tools} from '../../../../../common/utils/tools';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {BudgetOptions} from '../../../../model/budget/budget-options';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {FlatRateSetting} from '../../../../model/flat-rate-setting';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {CallFlatRateSetting} from '../../../../model/call-flat-rate-setting';
import {ProjectPartnerBudgetOptionsConstants} from './project-partner-budget-options.constants';
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

  constants = ProjectPartnerBudgetOptionsConstants;
  Tools = Tools;

  budgetOptionForm: FormGroup;
  data$: Observable<{
    callFlatRateSettings: CallFlatRateSetting,
    flatRateErrorsArgs: {
      staff: { [key: string]: {} }
      officeOnStaff: { [key: string]: {} }
      officeOnDirect: { [key: string]: {} }
      travel: { [key: string]: {} }
      other: { [key: string]: {} }
    },
    isDividerVisible: boolean,
    isAnyOptionAvailable: boolean,
    isOtherCostFlatRateDisabled: boolean
    areFlatRatesInFirstCategoryDisabled: boolean
  }>;

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private pageStore: ProjectPartnerDetailPageStore
  ) {
  }

  ngOnInit(): void {
    this.initForm();
    this.handleResetForm();

    this.data$ = combineLatest([
      this.pageStore.callFlatRatesSettings$,
      this.flatRateErrorsArgs(),
      this.isDividerVisible(),
      this.isAnyOptionAvailable(),
      this.isOtherCostFlatRateDisabled(),
      this.areFlatRatesInFirstCategoryDisabled()
    ]).pipe(
      map(([callFlatRateSettings, flatRateErrorsArgs, isDividerVisible, isAnyOptionAvailable, isOtherCostFlatRateDisabled, areFlatRatesInFirstCategoryDisabled]: any) => {
        return {
          callFlatRateSettings,
          flatRateErrorsArgs,
          isDividerVisible,
          isAnyOptionAvailable,
          isOtherCostFlatRateDisabled,
          areFlatRatesInFirstCategoryDisabled
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

  formToBudgetOptions(): BudgetOptions {
    const isStaffCostActive = this.isStaffCostsFlatRateActive?.value;
    const isOfficeOnStaffActive = this.isOfficeAndAdministrationOnStaffCostsFlatRateActive?.value;
    const isOfficeOnDirectActive = this.isOfficeAndAdministrationOnDirectCostsFlatRateActive?.value;
    const isTravelActive = this.isTravelAndAccommodationOnStaffCostsFlatRateActive?.value;
    const isOtherActive = this.isOtherCostsOnStaffCostsFlatRateActive?.value;
    return new BudgetOptions(
      isOfficeOnStaffActive ? this.officeAndAdministrationOnStaffCostsFlatRate?.value || null : null,
      isOfficeOnDirectActive ? this.officeAndAdministrationOnDirectCostsFlatRate?.value || null : null,
      isStaffCostActive ? this.staffCostsFlatRate?.value || null : null,
      isTravelActive ? this.travelAndAccommodationOnStaffCostsFlatRate?.value || null : null,
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


  private initForm(): void {
    this.budgetOptionForm = this.formBuilder.group({});
    this.formService.init(this.budgetOptionForm, this.pageStore.isProjectEditable$);
  }

  private handleResetForm(): void {
    combineLatest([this.formService.reset$.pipe(startWith(null)), this.pageStore.budgetOptions$, this.pageStore.callFlatRatesSettings$]).pipe(
      map(([, budgetOptions, callFlatRateSettings]) => this.resetForm(budgetOptions, callFlatRateSettings)),
      untilDestroyed(this)
    ).subscribe();
  }

  private resetForm(budgetOptions: BudgetOptions, callFlatRateSettings: CallFlatRateSetting): void {
    this.removeAllControls();
    if (callFlatRateSettings.staffCostFlatRateSetup !== null) {
      this.addControlForCheckBox(this.constants.FORM_CONTROL_NAMES.isStaffCostsFlatRateActive, budgetOptions.staffCostsFlatRate !== null);
      this.addControlForFlatRate(this.constants.FORM_CONTROL_NAMES.staffCostsFlatRate, budgetOptions.staffCostsFlatRate, callFlatRateSettings.staffCostFlatRateSetup.rate);
    }
    if (callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup !== null) {
      this.addControlForCheckBox(this.constants.FORM_CONTROL_NAMES.isOfficeAndAdministrationOnStaffCostsFlatRateActive, budgetOptions.officeAndAdministrationOnStaffCostsFlatRate !== null);
      this.addControlForFlatRate(this.constants.FORM_CONTROL_NAMES.officeAndAdministrationOnStaffCostsFlatRate, budgetOptions.officeAndAdministrationOnStaffCostsFlatRate, callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup.rate);
    }
    if (callFlatRateSettings.officeAndAdministrationOnDirectCostsFlatRateSetup !== null) {
      this.addControlForCheckBox(this.constants.FORM_CONTROL_NAMES.isOfficeAndAdministrationOnDirectCostsFlatRateActive, budgetOptions.officeAndAdministrationOnDirectCostsFlatRate !== null);
      this.addControlForFlatRate(this.constants.FORM_CONTROL_NAMES.officeAndAdministrationOnDirectCostsFlatRate, budgetOptions.officeAndAdministrationOnDirectCostsFlatRate, callFlatRateSettings.officeAndAdministrationOnDirectCostsFlatRateSetup.rate);
    }
    if (callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup !== null) {
      this.addControlForCheckBox(this.constants.FORM_CONTROL_NAMES.isTravelAndAccommodationOnStaffCostsFlatRateActive, budgetOptions.travelAndAccommodationOnStaffCostsFlatRate !== null);
      this.addControlForFlatRate(this.constants.FORM_CONTROL_NAMES.travelAndAccommodationOnStaffCostsFlatRate, budgetOptions.travelAndAccommodationOnStaffCostsFlatRate, callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup.rate);
    }
    if (callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup !== null) {
      this.addControlForCheckBox(this.constants.FORM_CONTROL_NAMES.isOtherCostsOnStaffCostsFlatRateActive, budgetOptions.otherCostsOnStaffCostsFlatRate !== null);
      this.addControlForFlatRate(this.constants.FORM_CONTROL_NAMES.otherCostsOnStaffCostsFlatRate, budgetOptions.otherCostsOnStaffCostsFlatRate, callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup.rate);
    }
    this.formService.resetEditable();
  }

  private addControlForCheckBox(name: string, value: boolean): void {
    this.budgetOptionForm.addControl(name, this.formBuilder.control(value));
  }

  private addControlForFlatRate(name: string, value: number | null, maxAndDefaultValue: number): void {
    this.budgetOptionForm.addControl(name, this.formBuilder.control(
      value || maxAndDefaultValue, [Validators.max(maxAndDefaultValue), Validators.min(1), Validators.required]));
  }

  private removeAllControls(): void {
    Object.keys(this.budgetOptionForm.controls).forEach(name => {
      this.budgetOptionForm.removeControl(name);
    });
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

  private areFlatRatesInFirstCategoryDisabled(): Observable<boolean> {
    return combineLatest([this.pageStore.isProjectEditable$, this.budgetOptionForm.valueChanges.pipe(startWith(null))]).pipe(
      map(([isProjectEditable]) =>
        !isProjectEditable || this.isOtherCostsOnStaffCostsFlatRateActive?.value
      ));
  }

  private isOtherCostFlatRateDisabled(): Observable<boolean> {
    return combineLatest([this.pageStore.isProjectEditable$, this.budgetOptionForm.valueChanges.pipe(startWith(null))]).pipe(
      map(([isProjectEditable]) =>
        !isProjectEditable ||
        this.isOfficeAndAdministrationOnStaffCostsFlatRateActive?.value ||
        this.isOfficeAndAdministrationOnDirectCostsFlatRateActive?.value ||
        this.isStaffCostsFlatRateActive?.value ||
        this.isTravelAndAccommodationOnStaffCostsFlatRateActive?.value
      )
    );
  }


  get staffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.staffCostsFlatRate) as FormControl;
  }

  get isStaffCostsFlatRateActive(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isStaffCostsFlatRateActive) as FormControl;
  }

  get officeAndAdministrationOnStaffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.officeAndAdministrationOnStaffCostsFlatRate) as FormControl;
  }

  get isOfficeAndAdministrationOnStaffCostsFlatRateActive(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isOfficeAndAdministrationOnStaffCostsFlatRateActive) as FormControl;
  }

  get officeAndAdministrationOnDirectCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.officeAndAdministrationOnDirectCostsFlatRate) as FormControl;
  }

  get isOfficeAndAdministrationOnDirectCostsFlatRateActive(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isOfficeAndAdministrationOnDirectCostsFlatRateActive) as FormControl;
  }

  get travelAndAccommodationOnStaffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.travelAndAccommodationOnStaffCostsFlatRate) as FormControl;
  }

  get isTravelAndAccommodationOnStaffCostsFlatRateActive(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isTravelAndAccommodationOnStaffCostsFlatRateActive) as FormControl;
  }

  get otherCostsOnStaffCostsFlatRate(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.otherCostsOnStaffCostsFlatRate) as FormControl;
  }

  get isOtherCostsOnStaffCostsFlatRateActive(): FormControl {
    return this.budgetOptionForm.get(this.constants.FORM_CONTROL_NAMES.isOtherCostsOnStaffCostsFlatRateActive) as FormControl;
  }

}
