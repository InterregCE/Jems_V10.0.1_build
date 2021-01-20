import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectLumpSumsPageStore} from './project-lump-sums-page.store';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap, withLatestFrom} from 'rxjs/operators';
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
import {WidthConfig} from '../../../common/directives/table-config/WidthConfig';
import {ProjectLumSumsConstants} from './project-lum-sums.constants';
import {MatTableDataSource} from '@angular/material/table';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {NumberService} from '../../../common/services/number.service';
import {ProjectLumpSum} from '../../model/lump-sums/projectLumpSum';
import {ProjectPartner} from '../../model/ProjectPartner';
import {PartnerContribution} from '../../model/lump-sums/partnerContribution';
import {HttpErrorResponse} from '@angular/common/http';
import {ProgrammeLumpSum} from '../../model/lump-sums/programmeLumpSum';

@UntilDestroy()
@Component({
  selector: 'app-project-lump-sums-page',
  templateUrl: './project-lump-sums-page.component.html',
  styleUrls: ['./project-lump-sums-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class ProjectLumpSumsPageComponent implements OnInit {

  constants = ProjectLumSumsConstants;

  lumpSumsForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;

  data$: Observable<{
    projectAcronym: string
    columnsToDisplay: string[],
    withConfigs: WidthConfig[],
    partners: ProjectPartner[],
    lumpSums: ProgrammeLumpSum[],
    periods: number[],
    showAddButton: number,
    costIsNotSplittableError: ValidationErrors | null,
    partnerColumnsTotal: number[]
  }>;

  private columnsToDisplay$: Observable<string[]>;
  private withConfigs$: Observable<WidthConfig[]>;
  private showAddButton$: Observable<boolean>;
  private costIsNotSplittableError$: Observable<ValidationErrors | null>;
  private partnerColumnsTotal$: Observable<number[]>;

  constructor(public pageStore: ProjectLumpSumsPageStore, private formBuilder: FormBuilder, private formService: FormService) {
  }

  ngOnInit(): void {
    this.initForm();

    this.handelFormReset();

    this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);

    this.items.valueChanges.pipe(untilDestroyed(this)).subscribe(() => {
      this.dataSource.data = this.items.controls;
    });

    this.showAddButton$ = combineLatest([this.items.valueChanges.pipe(startWith(null)), this.pageStore.projectLumpSums$]).pipe(
      map(([, projectLumpSums]) => (projectLumpSums.length === 0 && this.items?.length === 0)),
    );

    this.columnsToDisplay$ = this.pageStore.partners$.pipe(map((partners: ProjectPartner[]) => this.getColumnsToDisplay(partners)));
    this.withConfigs$ = this.pageStore.partners$.pipe(map((partners: ProjectPartner[]) => this.getWithConfigs(partners)));
    this.costIsNotSplittableError$ = this.items.valueChanges.pipe(startWith(null), map(() => this.items.controls.find(itemFormGroup => itemFormGroup.errors !== null)?.errors || null));
    this.partnerColumnsTotal$ = combineLatest([this.formService.reset$.pipe(startWith(null)), this.items.valueChanges.pipe(startWith(null))]).pipe(map(() => this.calculatePartnerColumnsTotal()));

    this.data$ = combineLatest([
      this.pageStore.projectAcronym$,
      this.columnsToDisplay$,
      this.withConfigs$,
      this.pageStore.partners$,
      this.pageStore.projectCallLumpSums$,
      this.pageStore.projectPeriodNumbers$,
      this.showAddButton$,
      this.costIsNotSplittableError$,
      this.partnerColumnsTotal$
    ]).pipe(
      map(([projectAcronym, columnsToDisplay, withConfigs, partners, lumpSums, periods, showAddButton, costIsNotSplittableError, partnerColumnsTotal]: any) => {
        return {
          projectAcronym,
          columnsToDisplay,
          withConfigs,
          partners,
          lumpSums,
          periods,
          showAddButton,
          costIsNotSplittableError,
          partnerColumnsTotal
        };
      })
    );
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.formService.setDirty(true);
  }

  addNewItem(partners: ProjectPartner[]): void {
    const item = this.formBuilder.group({
      rowId: Math.random() * 1000,
      id: null,
      lumpSum: [null, Validators.required],
      periodId: [null],
      partnersContribution: this.formBuilder.array(partners.map(partner => this.formBuilder.group({
        partnerId: partner.id,
        amount: 0
      }))),
      rowSum: [0],
      gap: [0],
    });
    this.addItemToItems(item);
    this.formService.setDirty(true);
  }

  updateLumpSums(): void {
    this.pageStore.updateProjectLumpSums(this.formToProjectLumpSums())
      .pipe(
        tap(() => this.formService.setSuccess('project.application.form.section.part.e.lump.sums.save.success')),
        catchError((error: HttpErrorResponse) => this.formService.setError(error)),
        untilDestroyed(this)
      ).subscribe();
  }

  isGapExists(itemGroupControl: AbstractControl): boolean {
    return this.getGapControl(itemGroupControl)?.value !== 0;
  }

  isLumpSumSelectedForRow(control: FormGroup): boolean {
    return this.getLumpSumControl(control)?.value;
  }

  private calculatePartnerColumnsTotal(): number[] {
    if (!this.items || this.items.controls.length === 0) { return []; }

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
    this.formService.init(this.lumpSumsForm, this.pageStore.isProjectEditable$);
  }

  private handelFormReset(): void {
    combineLatest([
      this.formService.reset$.pipe(startWith(null)),
      this.pageStore.projectLumpSums$
    ]).pipe(
      withLatestFrom(this.pageStore.projectCallLumpSums$, this.pageStore.partners$),
      tap(([[, projectLumpSums], projectCallLumpSums, partners]) => this.resetForm(projectLumpSums, projectCallLumpSums, partners)),
      untilDestroyed(this)
    ).subscribe();
  }

  private resetForm(projectLumpSums: ProjectLumpSum[], projectCallLumpSums: ProgrammeLumpSum[], partners: ProjectPartner[]): void {
    this.items.clear();
    projectLumpSums.forEach(projectLumpSum => {
      const item = this.formBuilder.group({
        rowId: Math.random() * 1000,
        id: projectLumpSum.id,
        lumpSum: [projectCallLumpSums.find(it => it.id === projectLumpSum.programmeLumpSumId), Validators.required],
        periodId: [projectLumpSum.period],
        partnersContribution: this.formBuilder.array(partners.map(partner => this.formBuilder.group({
          partnerId: partner.id,
          amount: projectLumpSum.lumpSumContributions.find(contribution => contribution.partnerId === partner.id)?.amount || 0
        }))),
        rowSum: [0],
        gap: [0],
      });
      this.addItemToItems(item);
    });
  }

  private addItemToItems(item: FormGroup): void {
    item.setValidators(this.partnersContributionValidator);
    this.items.push(item);
    item.valueChanges.pipe(startWith(item.value), untilDestroyed(this)).subscribe(itemValues => {
      this.setGapAndRowSum(itemValues.rowId);
    });
  }

  private formToProjectLumpSums(): ProjectLumpSum[] {
    return this.items.controls.map((formGroup: FormGroup) => {
      return new ProjectLumpSum(
        formGroup.get(this.constants.FORM_CONTROL_NAMES.id)?.value,
        this.getLumpSumControl(formGroup)?.value?.id,
        formGroup.get(this.constants.FORM_CONTROL_NAMES.periodId)?.value,
        this.getPartnerContributionFormArray(formGroup)?.value,
      );
    });
  }

  private getColumnsToDisplay(partners: ProjectPartner[]): string[] {
    let columnsToDisplay = ['lumpSum', 'period', 'isSplittingLumpSumAllowed', 'lumpSumCost'];
    columnsToDisplay = columnsToDisplay.concat(partners?.map(partner => partner.sortNumber === 1 ? 'LP1' : 'PP'.concat(partner.sortNumber.toString())));
    columnsToDisplay = columnsToDisplay.concat(['rowSum', 'gap', 'description', 'actions']);
    return columnsToDisplay;
  }

  private getWithConfigs(partners: ProjectPartner[]): WidthConfig[] {
    let widthConfigs = [{minInRem: 8}, {minInRem: 5}, {minInRem: 4}, {minInRem: 8}];
    widthConfigs = widthConfigs.concat(partners?.map(() => {
      return {minInRem: 8};
    }));
    widthConfigs = widthConfigs.concat({minInRem: 8}, {minInRem: 8}, {minInRem: 12}, {minInRem: 3});
    return widthConfigs;
  }

  private setGapAndRowSum(rowId: number): void {
    const itemFormGroup = this.getItemControlByRowId(rowId);
    const amounts = this.getPartnerContributionFormArray(itemFormGroup)?.value?.map((it: PartnerContribution) => it.amount) as number[];
    const cost = this.getLumpSumControl(itemFormGroup)?.value?.cost || 0;
    const rowSumValue = NumberService.sum(amounts);
    const gapValue = NumberService.minus(cost, rowSumValue);
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
