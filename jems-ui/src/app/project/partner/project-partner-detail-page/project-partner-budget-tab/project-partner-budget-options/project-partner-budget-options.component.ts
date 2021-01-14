import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Tools} from '../../../../../common/utils/tools';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {BudgetOptions} from '../../../../project-application/model/budget-options';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {FlatRateSetting} from '../../../../project-application/model/flat-rate-setting';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {CallFlatRateSetting} from '../../../../project-application/model/call-flat-rate-setting';
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
    staffCostsFlatRateErrorsArgs: { [key: string]: {} },
    officeOnStaffCostFlatRateErrorsArgs: { [key: string]: {} },
    travelFlatRateErrorsArgs: { [key: string]: {} },
    otherFlatRateErrorsArgs: { [key: string]: {} },
    isDividerVisible: boolean,
    isAnyOptionAvailable: boolean,
    isOtherCostsOnStaffCostsFlatRateDisabled: boolean
    areFlatRatesInFirstCategoryDisabled: boolean
  }>;

  private staffCostsFlatRateErrorsArgs$: Observable<{ [key: string]: {} }>;
  private officeOnStaffCostFlatRateErrorsArgs$: Observable<{ [key: string]: {} }>;
  private travelFlatRateErrorsArgs$: Observable<{ [key: string]: {} }>;
  private otherFlatRateErrorsArgs$: Observable<{ [key: string]: {} }>;
  private isAnyOptionAvailable$: Observable<boolean>;
  private isDividerVisible$: Observable<boolean>;
  private isOtherCostsOnStaffCostsFlatRateDisabled$: Observable<boolean>;
  private areFlatRatesInFirstCategoryDisabled$: Observable<boolean>;


  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private pageStore: ProjectPartnerDetailPageStore
  ) {
  }

  ngOnInit(): void {
    this.initForm();
    combineLatest([this.formService.reset$.pipe(startWith(null)), this.pageStore.budgetOptions$, this.pageStore.callFlatRatesSettings$]).pipe(
      map(([, budgetOptions, callFlatRateSettings]) => this.resetForm(budgetOptions, callFlatRateSettings)),
      untilDestroyed(this)
    ).subscribe();

    this.staffCostsFlatRateErrorsArgs$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => this.getFlatRateErrorArgs(callFlatRateSettings.staffCostFlatRateSetup)),
    );
    this.officeOnStaffCostFlatRateErrorsArgs$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => this.getFlatRateErrorArgs(callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup))
    );
    this.travelFlatRateErrorsArgs$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => this.getFlatRateErrorArgs(callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup))
    );
    this.otherFlatRateErrorsArgs$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(callFlatRateSettings => this.getFlatRateErrorArgs(callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup))
    );
    this.isDividerVisible$ = this.pageStore.callFlatRatesSettings$.pipe(
      map(flatRateSetting => flatRateSetting.otherCostsOnStaffCostsFlatRateSetup !== null && (flatRateSetting.travelAndAccommodationOnStaffCostsFlatRateSetup !== null || flatRateSetting.officeAndAdministrationOnStaffCostsFlatRateSetup !== null || flatRateSetting.staffCostFlatRateSetup !== null))
    );
    this.isAnyOptionAvailable$ = this.pageStore.callFlatRatesSettings$.pipe(map(callFlatRateSetting => callFlatRateSetting.staffCostFlatRateSetup !== null || callFlatRateSetting.officeAndAdministrationOnStaffCostsFlatRateSetup !== null || callFlatRateSetting.travelAndAccommodationOnStaffCostsFlatRateSetup !== null || callFlatRateSetting.otherCostsOnStaffCostsFlatRateSetup !== null || callFlatRateSetting.officeAndAdministrationOnOtherCostsFlatRateSetup !== null));

    this.isOtherCostsOnStaffCostsFlatRateDisabled$ = combineLatest([this.pageStore.isProjectEditable$.pipe(startWith(false)), this.budgetOptionForm.valueChanges.pipe(startWith(null))]).pipe(
      map(([isProjectEditable]) =>
        !isProjectEditable ||
        this.isOfficeAndAdministrationOnStaffCostsFlatRateActive?.value ||
        this.isStaffCostsFlatRateActive?.value ||
        this.isTravelAndAccommodationOnStaffCostsFlatRateActive?.value
      )
    );
    this.areFlatRatesInFirstCategoryDisabled$ = combineLatest([this.pageStore.isProjectEditable$.pipe(startWith(false)), this.budgetOptionForm.valueChanges.pipe(startWith(null))]).pipe(
      map(([isProjectEditable]) =>
        !isProjectEditable || this.isOtherCostsOnStaffCostsFlatRateActive?.value
      ));

    this.data$ = combineLatest([
      this.pageStore.callFlatRatesSettings$,
      this.staffCostsFlatRateErrorsArgs$,
      this.officeOnStaffCostFlatRateErrorsArgs$,
      this.travelFlatRateErrorsArgs$,
      this.otherFlatRateErrorsArgs$,
      this.isDividerVisible$,
      this.isAnyOptionAvailable$,
      this.isOtherCostsOnStaffCostsFlatRateDisabled$,
      this.areFlatRatesInFirstCategoryDisabled$
    ]).pipe(
      map(([callFlatRateSettings, staffCostsFlatRateErrorsArgs, officeOnStaffCostFlatRateErrorsArgs, travelFlatRateErrorsArgs, otherFlatRateErrorsArgs, isDividerVisible, isAnyOptionAvailable, isOtherCostsOnStaffCostsFlatRateDisabled, areFlatRatesInFirstCategoryDisabled]: any) => {
        return {
          callFlatRateSettings,
          staffCostsFlatRateErrorsArgs,
          officeOnStaffCostFlatRateErrorsArgs,
          travelFlatRateErrorsArgs,
          otherFlatRateErrorsArgs,
          isDividerVisible,
          isAnyOptionAvailable,
          isOtherCostsOnStaffCostsFlatRateDisabled,
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
    const isOfficeActive = this.isOfficeAndAdministrationOnStaffCostsFlatRateActive?.value;
    const isTravelActive = this.isTravelAndAccommodationOnStaffCostsFlatRateActive?.value;
    const isOtherActive = this.isOtherCostsOnStaffCostsFlatRateActive?.value;
    return new BudgetOptions(
      isOfficeActive ? this.officeAndAdministrationOnStaffCostsFlatRate?.value || null : null,
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

  private resetForm(budgetOptions: BudgetOptions, callFlatRateSettings: CallFlatRateSetting): void {
    this.removeAllControls();
    if (callFlatRateSettings.staffCostFlatRateSetup !== null) {
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.isStaffCostsFlatRateActive, this.formBuilder.control(budgetOptions.staffCostsFlatRate !== null));
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.staffCostsFlatRate, this.formBuilder.control(
        budgetOptions.staffCostsFlatRate ? budgetOptions.staffCostsFlatRate : callFlatRateSettings.staffCostFlatRateSetup.rate,
        [Validators.max(callFlatRateSettings.staffCostFlatRateSetup.rate), Validators.min(1), Validators.required]));
    }
    if (callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup !== null) {
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.isOfficeAndAdministrationOnStaffCostsFlatRateActive, this.formBuilder.control(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate !== null));
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.officeAndAdministrationOnStaffCostsFlatRate, this.formBuilder.control(
        budgetOptions.officeAndAdministrationOnStaffCostsFlatRate ? budgetOptions.officeAndAdministrationOnStaffCostsFlatRate : callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup.rate,
        [Validators.max(callFlatRateSettings.officeAndAdministrationOnStaffCostsFlatRateSetup.rate), Validators.min(1), Validators.required]));
    }
    if (callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup !== null) {
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.isTravelAndAccommodationOnStaffCostsFlatRateActive, this.formBuilder.control(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate !== null));
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.travelAndAccommodationOnStaffCostsFlatRate, this.formBuilder.control(
        budgetOptions.travelAndAccommodationOnStaffCostsFlatRate ? budgetOptions.travelAndAccommodationOnStaffCostsFlatRate : callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup.rate,
        [Validators.max(callFlatRateSettings.travelAndAccommodationOnStaffCostsFlatRateSetup.rate), Validators.min(1), Validators.required]));
    }
    if (callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup !== null) {
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.isOtherCostsOnStaffCostsFlatRateActive, this.formBuilder.control(budgetOptions.otherCostsOnStaffCostsFlatRate !== null));
      this.budgetOptionForm.addControl(this.constants.FORM_CONTROL_NAMES.otherCostsOnStaffCostsFlatRate, this.formBuilder.control(
        budgetOptions.otherCostsOnStaffCostsFlatRate ? budgetOptions.otherCostsOnStaffCostsFlatRate : callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup.rate,
        [Validators.max(callFlatRateSettings.otherCostsOnStaffCostsFlatRateSetup.rate), Validators.min(1), Validators.required]));
    }
    this.formService.resetEditable();
  }

  private removeAllControls(): void {
    Object.keys(this.budgetOptionForm.controls).forEach(name => {
      this.budgetOptionForm.removeControl(name);
    });
  }

  private getFlatRateErrorArgs(flatRateSetting: FlatRateSetting | null): { [key: string]: {} } {
    return {
      max: {maxValue: flatRateSetting?.rate},
      min: {maxValue: flatRateSetting?.rate}
    };
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
