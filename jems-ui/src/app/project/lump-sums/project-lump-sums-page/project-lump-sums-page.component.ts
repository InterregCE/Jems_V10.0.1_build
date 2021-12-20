import {ChangeDetectionStrategy, Component, OnInit, ViewChild} from '@angular/core';
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
import {ProgrammeLumpSum} from '../../model/lump-sums/programmeLumpSum';
import {Alert} from '@common/components/forms/alert';
import {ProjectPeriod} from '../../model/ProjectPeriod';
import {TranslateService} from '@ngx-translate/core';
import {MatTable} from '@angular/material/table';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';

@UntilDestroy()
@Component({
  selector: 'app-project-lump-sums-page',
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
              private formVisibilityStatusService: FormVisibilityStatusService
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
    lumpSums: ProgrammeLumpSum[];
    periods: ProjectPeriod[];
    showAddButton: boolean;
    showGapExistsWarning: boolean;
    costIsNotSplittableError: ValidationErrors | null;
    partnerColumnsTotal: number[];
    loading: boolean;
  }>;

  private columnsToDisplay$: Observable<string[]>;
  private withConfigs$: Observable<TableConfig[]>;
  private showAddButton$: Observable<boolean>;
  private costIsNotSplittableError$: Observable<ValidationErrors | null>;
  private partnerColumnsTotal$: Observable<number[]>;
  private showGapExistsWarning$: Observable<boolean>;
  private loading = new BehaviorSubject(false);

  private static calculateRowSum(amounts: number[]): number {
    return NumberService.sum(amounts);
  }

  private static calculateGap(lumpSumCost: number, rowSum: number): number {
    return NumberService.minus(lumpSumCost, rowSum);
  }

  private getColumnsToDisplay(partners: ProjectPartner[]): string[] {
    return [
      'lumpSum',
      ...this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS) ? ['period'] : [],
      'isSplittingLumpSumAllowed', 'lumpSumCost',
      ...partners?.map(partner => partner.toPartnerNumberString()),
      'rowSum',
      'gap',
      ...this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PROJECT_LUMP_SUMS_DESCRIPTION) ? ['description'] : [],
      'actions'
    ];
  }

  private getTableConfig(partners: ProjectPartner[]): TableConfig[] {
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
      {minInRem: 3}
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

    this.columnsToDisplay$ = this.pageStore.partners$.pipe(map((partners: ProjectPartner[]) => this.getColumnsToDisplay(partners)));
    this.withConfigs$ = this.pageStore.partners$.pipe(map((partners: ProjectPartner[]) => this.getTableConfig(partners)));
    this.costIsNotSplittableError$ = this.items.valueChanges
      .pipe(startWith(null), map(() => this.items.controls.find(itemFormGroup => itemFormGroup.errors !== null)?.errors || null));
    this.partnerColumnsTotal$ = combineLatest(
      [this.formService.reset$.pipe(startWith(null)), this.items.valueChanges.pipe(startWith(null))]
    ).pipe(map(() => this.calculatePartnerColumnsTotal()));

    this.data$ = combineLatest([
      this.pageStore.projectTitle$,
      this.columnsToDisplay$,
      this.withConfigs$,
      this.pageStore.partners$,
      this.pageStore.projectCallLumpSums$,
      this.pageStore.projectPeriods$,
      this.showAddButton$,
      this.showGapExistsWarning$,
      this.costIsNotSplittableError$,
      this.partnerColumnsTotal$,
      this.loading
    ]).pipe(
      map(([projectTitle, columnsToDisplay, withConfigs, partners, lumpSums, periods, showAddButton, showGapExistsWarning, costIsNotSplittableError, partnerColumnsTotal, loading]: any) => {
        return {
          projectTitle,
          columnsToDisplay,
          withConfigs,
          partners,
          lumpSums,
          periods,
          showAddButton,
          showGapExistsWarning,
          costIsNotSplittableError,
          partnerColumnsTotal,
          loading
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

  getPeriodLabel(period: ProjectPeriod | undefined): string {
    if (!period) {
      return '';
    }
    if (period.periodNumber === 0) {
      return 'Preparation';
    }
    return this.translateService.instant('project.application.form.work.package.output.delivery.period.entry', period);
  }

  getPeriod(itemIndex: number, periods: ProjectPeriod[]): ProjectPeriod | undefined {
    const periodNumber = this.items.at(itemIndex)?.get(this.constants.FORM_CONTROL_NAMES.periodNumber)?.value;
    if (periodNumber === 0) {
      return {periodNumber: 0} as any;
    }
    return periods.find(period => period.periodNumber === periodNumber);
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
    projectCallLumpSums: ProgrammeLumpSum[],
    partners: ProjectPartner[],
    periods: ProjectPeriod[]
  ): void {
    this.items.clear();
    const periodNumbers = [this.PREPARATION_PERIOD, ...periods.map(period => period.periodNumber), this.CLOSURE_PERIOD];
    projectLumpSums.forEach(projectLumpSum => {
      const lumpSum = projectCallLumpSums.find(it => it.id === projectLumpSum.programmeLumpSumId);
      const rowSum = ProjectLumpSumsPageComponent.calculateRowSum(projectLumpSum.lumpSumContributions.map(it => it.amount));
      const item = this.formBuilder.group({
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
      });
      this.addItemToItems(item);
    });

    this.loading.next(true);
    setTimeout(() => {
      this.tableData = [...this.items.controls];
      this.loading.next(false);
    },         0);
    this.formService.resetEditable();
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
        this.getLumpSumControl(formGroup)?.value?.id,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.periodNumber)?.value,
        this.getPartnerContributionFormArray(formGroup)?.value,
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

    if (lumpSumsFormControl?.value && !lumpSumsFormControl.value.isSplittingAllowed) {
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

  private getRowSumControl(itemFormGroup: AbstractControl): FormControl {
    return itemFormGroup.get(this.constants.FORM_CONTROL_NAMES.rowSum) as FormControl;
  }

  private getItemControlByRowId(rowId: number): FormGroup {
    return this.items.controls.find(row => row?.value?.rowId === rowId) as FormGroup;
  }

  get items(): FormArray {
    return this.lumpSumsForm.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }
}
