import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {ProjectLumpSumsStore} from './project-lump-sums-store.service';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {ProjectLumSumsConstants} from './project-lum-sums.constants';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {NumberService} from '@common/services/number.service';
import {ProjectLumpSum} from '../../model/lump-sums/projectLumpSum';
import {ProjectPartner} from '../../model/ProjectPartner';
import {PartnerContribution} from '../../model/lump-sums/partnerContribution';
import {HttpErrorResponse} from '@angular/common/http';
import {Alert} from '@common/components/forms/alert';
import {TranslateService} from '@ngx-translate/core';
import {MatTable} from '@angular/material/table';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProgrammeLumpSumDTO, ProjectPeriodDTO} from '@cat/api';
import {
  ContractMonitoringExtensionStore
} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.store';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectUtil} from '@project/common/project-util';

@UntilDestroy()
@Component({
  selector: 'jems-project-lump-sums-page',
  templateUrl: './project-lump-sums-page.component.html',
  styleUrls: ['./project-lump-sums-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class ProjectLumpSumsPageComponent implements OnInit {

  constructor(public pageStore: ProjectLumpSumsStore,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private translateService: TranslateService,
              private formVisibilityStatusService: FormVisibilityStatusService,
              public projectStore: ProjectStore
  ) {
  }

  APPLICATION_FORM = APPLICATION_FORM;
  PREPARATION_PERIOD = 0;
  CLOSURE_PERIOD = 255;

  @ViewChild('table') table: MatTable<any>;
  rowId = 0;
  constants = ProjectLumSumsConstants;
  Alert = Alert;

  lumpSumsForm: FormGroup;
  tableData: AbstractControl[] = [];

  data$: Observable<{
    projectTitle: string;
    columnsToDisplay: string[];
    withConfigs: TableConfig[];
    partners: ProjectPartner[];
    lumpSums: ProgrammeLumpSumDTO[];
    periods: ProjectPeriodDTO[];
    showAddButton: boolean;
    showGapExistsWarning: boolean;
    showPeriodMissingWarning: boolean;
    costIsNotSplittableError: ValidationErrors | null;
    partnerColumnsTotal: number[];
    sumColumnTotal: number;
    loading: boolean;
  }>;
  tableConfiguration$: Observable<{
    columnsToDisplay: string[];
    withConfigs: TableConfig[];
  }>;

  private showAddButton$: Observable<boolean>;
  private costIsNotSplittableError$: Observable<ValidationErrors | null>;
  private partnerColumnsTotal$: Observable<number[]>;
  private sumColumnTotal$: Observable<number>;
  private showGapExistsWarning$: Observable<boolean>;
  private showPeriodMissingWarning$: Observable<boolean>;
  private loading = new BehaviorSubject(false);

  private static calculateRowSum(amounts: number[]): number {
    return NumberService.sum(amounts);
  }

  private static calculateGap(lumpSumCost: number, rowSum: number): number {
    return NumberService.minus(lumpSumCost, rowSum);
  }

  private getColumnsToDisplay(partners: ProjectPartner[], isProjectEditable: boolean): string[] {
    return [
      'lumpSum',
      ...this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS) ? ['period'] : [],
      'isSplittingLumpSumAllowed', 'lumpSumCost',
      ...partners?.map(partner => partner.id + ''),
      'rowSum',
      'gap',
      ...this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PROJECT_LUMP_SUMS_DESCRIPTION) ? ['description'] : [],
      ...isProjectEditable ? ['actions'] : []
    ];
  }

  private getTableConfig(partners: ProjectPartner[], isProjectEditable: boolean): TableConfig[] {
    return [
      {minInRem: 8},
      ...this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS) ? [{minInRem: 5}] : [],
      {minInRem: 4}, {minInRem: 8},
      ...partners?.map(() => {
        return {minInRem: 8};
      }),
      {minInRem: 8},
      {minInRem: 8},
      ...this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PROJECT_LUMP_SUMS_DESCRIPTION) ? [{minInRem: 12}] : [],
      ...isProjectEditable ? [{minInRem: 3}] : []
    ];
  }

  ngOnInit(): void {
    this.initForm();

    this.handelFormReset();

    this.formService.reset$.pipe(untilDestroyed(this), tap(() => this.loading.next(true))).subscribe();
    this.showGapExistsWarning$ = combineLatest([this.items.valueChanges.pipe(startWith(null)), this.formService.reset$.pipe(startWith(null))]).pipe(
      map(() => this.items.controls.some(control => this.isGapExistsInRow(control))),
    );
    this.showAddButton$ = combineLatest([this.items.valueChanges.pipe(startWith(null)), this.pageStore.projectLumpSums$]).pipe(
      map(([, projectLumpSums]) => (projectLumpSums.length === 0 && this.items?.length === 0)),
    );
    this.showPeriodMissingWarning$ = combineLatest([this.items.valueChanges.pipe(startWith(null)), this.formService.reset$.pipe(startWith(null))]).pipe(
      map(() => this.items.controls.some(control => this.isPeriodMissingInRow(control))),
    );
    this.tableConfiguration$ = combineLatest([this.pageStore.partners$, this.pageStore.isProjectEditable$]).pipe(
      map(([partners, isProjectEditable]) => ({
          columnsToDisplay: this.getColumnsToDisplay(partners, isProjectEditable),
          withConfigs: this.getTableConfig(partners, isProjectEditable)
        })
      )
    );
    this.costIsNotSplittableError$ = this.items.valueChanges
      .pipe(startWith(null), map(() => this.items.controls.find(itemFormGroup => itemFormGroup.errors !== null)?.errors || null));
    this.partnerColumnsTotal$ = combineLatest(
      [this.formService.reset$.pipe(startWith(null)), this.items.valueChanges.pipe(startWith(null))]
    ).pipe(map(() => this.calculatePartnerColumnsTotal()));
    this.sumColumnTotal$ = this.partnerColumnsTotal$.pipe(
      map(partnerColumnsTotal => partnerColumnsTotal.reduce((acc, curr) => acc + curr, 0))
    );

    this.data$ = combineLatest([
      this.pageStore.projectTitle$,
      this.pageStore.partners$,
      this.pageStore.projectCallLumpSums$,
      this.pageStore.projectPeriods$,
      this.showAddButton$,
      this.showGapExistsWarning$,
      this.showPeriodMissingWarning$,
      this.costIsNotSplittableError$,
      this.partnerColumnsTotal$,
      this.sumColumnTotal$,
      this.loading,
      this.tableConfiguration$,
    ]).pipe(
      map(([projectTitle, partners, lumpSums, periods, showAddButton, showGapExistsWarning, showPeriodMissingWarning, costIsNotSplittableError, partnerColumnsTotal, sumColumnTotal, loading, tableConfiguration]: any) => {
        return {
          projectTitle,
          partners,
          lumpSums,
          periods,
          showAddButton,
          showGapExistsWarning,
          showPeriodMissingWarning,
          costIsNotSplittableError,
          partnerColumnsTotal,
          sumColumnTotal,
          loading,
          ...tableConfiguration
        };
      })
    );
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.tableData = [...this.items.controls];
    this.formService.setDirty(true);
  }

  addNewItem(partners: ProjectPartner[]): void {
    const item = this.formBuilder.group({
      rowId: this.newRowId(),
      id: null,
      lumpSum: [null, Validators.required],
      periodNumber: [null],
      partnersContribution: this.formBuilder.array(partners.map(partner => this.formBuilder.group({
        partnerId: partner.id,
        amount: 0
      }))),
      rowSum: [0],
      gap: [0],
      readyForPayment: [false],
      comment: [''],
      fastTrack: [false]
    });
    this.addItemToItems(item);
    this.tableData = [...this.items.controls];
    this.formService.setDirty(true);
  }

  updateLumpSums(): void {
    this.loading.next(true);
    this.pageStore.updateProjectLumpSums(this.formToProjectLumpSums())
      .pipe(
        tap(() => this.formService.setSuccess('project.application.form.section.part.e.lump.sums.save.success')),
        catchError((error: HttpErrorResponse) => {
          this.loading.next(false);
          return this.formService.setError(error);
        }),
        untilDestroyed(this)
      ).subscribe();
  }

  isGapExistsInRow(itemGroupControl: AbstractControl): boolean {
    return this.getGapControl(itemGroupControl)?.value !== 0;
  }

  isLumpSumSelectedForRow(control: FormGroup): boolean {
    return this.getLumpSumControl(control)?.value;
  }

  isPeriodMissingInRow(itemGroupControl: AbstractControl): boolean {
    return this.getPeriodControl(itemGroupControl)?.value === null;
  }

  getPeriodLabel(period: ProjectPeriodDTO | undefined): string {
    if (!period) {
      return '';
    }
    if (period.number === 0) {
      return 'Preparation';
    }
    return this.translateService.instant(ProjectUtil.getPeriodKey(period.startDate), ProjectUtil.getPeriodArguments(period));
  }

  getPeriod(itemIndex: number, periods: ProjectPeriodDTO[]): ProjectPeriodDTO | undefined {
    const periodNumber = this.items.at(itemIndex)?.get(this.constants.FORM_CONTROL_NAMES.periodNumber)?.value;
    if (periodNumber === 0) {
      return {number: 0} as any;
    }
    return periods.find(period => period.number === periodNumber);
  }

  private calculatePartnerColumnsTotal(): number[] {
    if (!this.items || this.items.controls.length === 0) {
      return [];
    }

    const totals: number[] = [];
    this.items.controls.forEach(control => {
      const partnersContribution = this.getPartnerContributionFormArray(control);
      const amounts = (partnersContribution?.value || []).map((it: PartnerContribution) => it.amount) as number[];
      amounts.forEach((amount, index) => {
        totals[index] = NumberService.sum([totals[index] || 0, amount]);
      });
    });

    return totals.map(it => NumberService.truncateNumber(it));

  }

  private initForm(): void {
    this.lumpSumsForm = this.formBuilder.group({
      items: this.formBuilder.array([], Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS))
    });
    this.formService.init(this.lumpSumsForm, combineLatest([this.pageStore.isProjectEditable$, this.loading]).pipe(map(([isProjectEditable, isLoading]) => isProjectEditable && !isLoading)));
  }

  private handelFormReset(): void {
    combineLatest([
      this.formService.reset$.pipe(startWith(null)),
      this.pageStore.projectLumpSums$,
      this.pageStore.projectCallLumpSums$,
      this.pageStore.partners$,
      this.pageStore.projectPeriods$
    ]).pipe(
      tap(([, projectLumpSums, projectCallLumpSums, partners, periods]) =>
        this.resetForm(projectLumpSums, projectCallLumpSums, partners, periods)),
      untilDestroyed(this)
    ).subscribe();
  }

  private resetForm(
    projectLumpSums: ProjectLumpSum[],
    projectCallLumpSums: ProgrammeLumpSumDTO[],
    partners: ProjectPartner[],
    periods: ProjectPeriodDTO[]
  ): void {
    this.items.clear();
    const periodNumbers = [this.PREPARATION_PERIOD, ...periods.map(period => period.number), this.CLOSURE_PERIOD];
    projectLumpSums.forEach(projectLumpSum => {
      const lumpSum = projectCallLumpSums.find(it => it.id === projectLumpSum.programmeLumpSumId);
      const rowSum = ProjectLumpSumsPageComponent.calculateRowSum(projectLumpSum.lumpSumContributions.map(it => it.amount));
      const item = this.formBuilder.group({
        orderNr: projectLumpSum.orderNr,
        rowId: this.newRowId(),
        id: null,
        lumpSum: [lumpSum, Validators.required],
        periodNumber: periodNumbers.includes(projectLumpSum.period) ? projectLumpSum.period : null,
        partnersContribution: this.formBuilder.array(partners.map(partner => this.formBuilder.group({
          partnerId: partner.id,
          amount: projectLumpSum.lumpSumContributions.find(contribution => contribution.partnerId === partner.id)?.amount || 0
        }))),
        rowSum: [rowSum],
        gap: [ProjectLumpSumsPageComponent.calculateGap(lumpSum?.cost || 0, rowSum)],
        readyForPayment: [projectLumpSum.readyForPayment],
        comment: [projectLumpSum.comment],
        fastTrack: [projectLumpSum.fastTrack],
        paymentEnabledDate: projectLumpSum.paymentEnabledDate,
        lastApprovedVersionBeforeReadyForPayment: projectLumpSum.lastApprovedVersionBeforeReadyForPayment
      });
      this.addItemToItems(item);
    });

    this.loading.next(true);
    setTimeout(() => {
      this.tableData = [...this.items.controls];
      this.loading.next(false);
    }, 0);
    this.formService.resetEditable();
    setTimeout(() => {
      this.items.controls.forEach((formGroup: FormGroup) => {
        if (formGroup.get(this.constants.FORM_CONTROL_NAMES.readyForPayment)?.value === true) {
          this.getLumpSumControl(formGroup)?.disable();
          formGroup.get(this.constants.FORM_CONTROL_NAMES.periodNumber)?.disable();
          this.getPartnerContributionFormArray(formGroup)?.controls.forEach((partner: FormGroup) => partner.disable());
          formGroup.get(this.constants.FORM_CONTROL_NAMES.comment)?.disable();
          formGroup.get(this.constants.FORM_CONTROL_NAMES.readyForPayment)?.disable();
          formGroup.get(this.constants.FORM_CONTROL_NAMES.fastTrack)?.disable();
        }
      });
      }, 0);
  }

  private addItemToItems(item: FormGroup): void {
    item.setValidators(this.partnersContributionValidator);
    this.items.push(item);
    item.valueChanges.pipe(untilDestroyed(this)).subscribe(itemValues => {
      this.setGapAndRowSum(itemValues.rowId);
    });
  }

  private formToProjectLumpSums(): ProjectLumpSum[] {
    return this.items.controls.map((formGroup: FormGroup) => {
      return new ProjectLumpSum(
        formGroup.get(this.constants.FORM_CONTROL_NAMES.orderNr)?.value,
        this.getLumpSumControl(formGroup)?.value?.id,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.periodNumber)?.value,
        this.getPartnerContributionFormArray(formGroup)?.value,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.comment)?.value,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.readyForPayment)?.value,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.fastTrack)?.value,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.paymentEnabledDate)?.value,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.lastApprovedVersionBeforeReadyForPayment)?.value,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.installmentsAlreadyCreated)?.value,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.linkedToEcPaymentId)?.value,
      );
    });
  }

  private setGapAndRowSum(rowId: number): void {
    const itemFormGroup = this.getItemControlByRowId(rowId);
    const rowSumValue = ProjectLumpSumsPageComponent
      .calculateRowSum(this.getPartnerContributionFormArray(itemFormGroup)?.value?.map((it: PartnerContribution) => it.amount) as number[]);
    const gapValue = ProjectLumpSumsPageComponent.calculateGap(this.getLumpSumControl(itemFormGroup)?.value?.cost || 0, rowSumValue);
    this.getRowSumControl(itemFormGroup)?.setValue(NumberService.truncateNumber(rowSumValue), {emitEvent: false});
    this.getGapControl(itemFormGroup)?.setValue(NumberService.truncateNumber(gapValue), {emitEvent: false});
  }

  private partnersContributionValidator: ValidatorFn = (itemFormGroup: FormGroup): ValidationErrors | null => {
    const partnersContributionFormArray = this.getPartnerContributionFormArray(itemFormGroup);
    const lumpSumsFormControl = this.getLumpSumControl(itemFormGroup);

    partnersContributionFormArray.controls.forEach(control => {
      this.getAmountControl(control)?.setErrors(null);
    });

    if (lumpSumsFormControl?.value && !lumpSumsFormControl.value.splittingAllowed) {
      const amounts = partnersContributionFormArray?.controls?.map(control => this.getAmountControl(control)?.value) || [];
      const positiveAmountIndexes = amounts.map((amount, index) => amount > 0 ? index : -1).filter(index => index !== -1);
      if (positiveAmountIndexes.length > 1) {
        positiveAmountIndexes.forEach(index => {
          this.getAmountControl(partnersContributionFormArray.controls[index])?.setErrors({amount: true});
        });
        return {notSplittable: true};
      }
    }
    return null;
  };

  private newRowId(): number {
    ++this.rowId;
    return this.rowId;
  }

  private getLumpSumControl(itemFormGroup: AbstractControl): FormControl {
    return itemFormGroup?.get(this.constants.FORM_CONTROL_NAMES.lumpSum) as FormControl;
  }

  private getPartnerContributionFormArray(itemFormGroup: AbstractControl): FormArray {
    return itemFormGroup?.get(this.constants.FORM_CONTROL_NAMES.partnersContribution) as FormArray;
  }

  private getAmountControl(partnerContributionFormGroup: AbstractControl): FormControl {
    return partnerContributionFormGroup?.get(this.constants.FORM_CONTROL_NAMES.amount) as FormControl;
  }

  private getGapControl(itemFormGroup: AbstractControl): FormControl {
    return itemFormGroup.get(this.constants.FORM_CONTROL_NAMES.gap) as FormControl;
  }

  private getPeriodControl(itemFormGroup: AbstractControl): FormControl {
    return itemFormGroup.get(this.constants.FORM_CONTROL_NAMES.periodNumber) as FormControl;
  }

  private getRowSumControl(itemFormGroup: AbstractControl): FormControl {
    return itemFormGroup.get(this.constants.FORM_CONTROL_NAMES.rowSum) as FormControl;
  }

  private getItemControlByRowId(rowId: number): FormGroup {
    return this.items.controls.find(row => row?.value?.rowId === rowId) as FormGroup;
  }

  get items(): FormArray {
    return this.lumpSumsForm.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  compareLumpSums(l1: ProgrammeLumpSumDTO, l2: ProgrammeLumpSumDTO) {
    return l1.id === l2.id;
  }
}
