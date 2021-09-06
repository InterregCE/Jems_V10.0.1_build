import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
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
import {
  ProgrammeFundDTO,
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
  ProjectPartnerCoFinancingInputDTO,
  ProjectPartnerCoFinancingOutputDTO,
  ProjectPartnerContributionDTO
} from '@cat/api';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {FormService} from '@common/components/section/form/form.service';
import {Alert} from '@common/components/forms/alert';
import {NumberService} from '@common/services/number.service';
import {ProjectPartnerDetailPageStore} from '../project-partner-detail-page.store';
import {ProjectPartnerCoFinancingTabConstants} from './project-partner-co-financing-tab.constants';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {APPLICATION_FORM} from '@project/common/application-form-model';


const totalContributionValidator = (expectedAmount: number): ValidatorFn => (formArray: FormArray) => {
  const total = formArray.controls.map(item => item.get('amount')?.value).reduce((a, b) => NumberService.sum([a, b]), 0);
  return expectedAmount === total
    ? null
    : {total: true};
};

@UntilDestroy()
@Component({
  selector: 'app-project-partner-co-financing-tab',
  templateUrl: './project-partner-co-financing-tab.component.html',
  styleUrls: ['./project-partner-co-financing-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerCoFinancingTabComponent implements OnInit {

  constants = ProjectPartnerCoFinancingTabConstants;
  partnerContributionStatus = ProjectPartnerContributionDTO.StatusEnum;
  Alert = Alert;
  APPLICATION_FORM = APPLICATION_FORM;

  data$: Observable<{
    financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO,
    callFunds: ProgrammeFundDTO[],
    totalBudget: number,
    publicContributionSubTotal: number,
    privateContributionSubTotal: number,
    automaticPublicContributionSubTotal: number,
    contributionTotal: number,
    showTotalContributionWarning: boolean,
    partnerContributionErrorsArgs: ValidationErrors | null,
    editable: boolean
  }>;

  coFinancingForm: FormGroup;
  multipleFundsAllowed: boolean;

  private publicContributionSubTotal$: Observable<number>;
  private privateContributionSubTotal$: Observable<number>;
  private automaticPublicContributionSubTotal$: Observable<number>;
  private contributionTotal$: Observable<number>;
  private showTotalContributionWarning$: Observable<boolean>;
  private partnerContributionErrorsArgs$: Observable<{}>;

  constructor(public formService: FormService,
              private formBuilder: FormBuilder,
              private pageStore: ProjectPartnerDetailPageStore) {
  }

  get partnerAmount(): FormControl {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.partnerAmount) as FormControl;
  }

  get partnerPercentage(): FormControl {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.partnerPercentage) as FormControl;
  }

  get partnerContributions(): FormArray {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributions) as FormArray;
  }

  get finances(): FormArray {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.finances) as FormArray;
  }

  getMaxFundPercentage(fund: AbstractControl): number {
    const totalPercentageLeft = NumberService.sum(
      this.finances.controls
        .filter(control => control !== fund)
        .map(control => control.get(this.constants.FORM_CONTROL_NAMES.fundPercentage)?.value || 0)
    );
    return NumberService.minus(100, totalPercentageLeft);
  }

  ngOnInit(): void {
    this.initForm();
    this.handleResetForm();
    this.handleTotalContributionCalculations();

    this.showTotalContributionWarning$ = combineLatest([this.contributionTotal$, this.formService.dirty$.pipe(startWith(false))]).pipe(
      map(([total, dirty]) => total !== this.partnerAmount.value && !dirty)
    );

    this.partnerContributionErrorsArgs$ = this.partnerContributionsErrorArgs();

    this.data$ = combineLatest([
      this.pageStore.financingAndContribution$,
      this.pageStore.callFunds$,
      this.pageStore.totalBudget$,
      this.pageStore.multipleFundsAllowed$,
      this.privateContributionSubTotal$,
      this.publicContributionSubTotal$,
      this.automaticPublicContributionSubTotal$,
      this.contributionTotal$,
      this.showTotalContributionWarning$,
      this.partnerContributionErrorsArgs$,
      this.pageStore.isProjectEditable$
    ]).pipe(
      map(([financingAndContribution, callFunds, totalBudget, multipleFundsAllowed, privateContributionSubTotal, publicContributionSubTotal, automaticPublicContributionSubTotal, contributionTotal, showTotalContributionWarning, partnerContributionErrorsArgs, editable]: any) => {
        this.multipleFundsAllowed = multipleFundsAllowed;
        return {
          financingAndContribution,
          callFunds,
          totalBudget,
          privateContributionSubTotal,
          publicContributionSubTotal,
          automaticPublicContributionSubTotal,
          contributionTotal,
          showTotalContributionWarning,
          partnerContributionErrorsArgs,
          editable
        };
      })
    );

  }

  addNewPartnerContribution(initialValue?: ProjectPartnerContributionDTO): void {
    this.partnerContributions.push(this.formBuilder.group({
      name: [initialValue ? initialValue.name : '', [Validators.required, Validators.maxLength(255)]],
      status: [initialValue ? initialValue.status : '', [Validators.required]],
      amount: [initialValue?.amount || 0, [Validators.required, Validators.min(0)]],
      partner: [initialValue ? initialValue.partner : false]
    }));
    if (!initialValue) {
      this.coFinancingForm.markAsDirty();
    }
  }

  deletePartnerContribution(index: number): void {
    this.coFinancingForm.markAsDirty();
    this.partnerContributions.removeAt(index);
  }

  updateCoFinancingAndContributions(): void {
    this.pageStore.updateCoFinancingAndContributions(
      this.formToProjectPartnerCoFinancingAndContributionInputDTO()
    ).pipe(
      tap(() => this.formService.setSuccess('project.partner.coFinancing.contribution.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  addAdditionalFund(fund?: ProjectPartnerCoFinancingOutputDTO): void {
    this.finances.push(this.formBuilder.group({
      fundId: this.formBuilder.control(fund?.fund?.id, Validators.required),
      fundType: ProjectPartnerCoFinancingInputDTO.FundTypeEnum.MainFund,
      percentage: this.formBuilder.control(fund?.percentage || 0, Validators.required),
    }));
  }

  notSelectedFunds(allFunds: ProgrammeFundDTO[], currentFund?: AbstractControl): ProgrammeFundDTO[] {
    const selectedFundIds = this.finances.controls.map(
      control => control.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value
    );
    const currentFundId = currentFund?.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value;
    return allFunds.filter((fund: ProgrammeFundDTO) =>
      currentFundId && currentFundId === fund.id ? true : !selectedFundIds.includes(fund.id)
    );
  }

  deleteAdditionalFund(fundIndex: number, totalBudget: number): void {
    this.coFinancingForm.markAsDirty();
    this.finances.removeAt(fundIndex);
    this.financesPercentsChanged(totalBudget);
  }

  canAddFund(allFunds: ProgrammeFundDTO[], editable: boolean): boolean {
    return this.multipleFundsAllowed && editable
      && this.finances.length < this.constants.MAX_NUMBER_OF_FINANCES
      && !this.finances.controls.find(control => !control.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value)
      && !!this.notSelectedFunds(allFunds)?.length;
  }

  getFundAmount(fund: AbstractControl, totalBudget: number): number {
    const fundPercentage = fund.get(this.constants.FORM_CONTROL_NAMES.fundPercentage)?.value || 0;
    return NumberService.truncateNumber(NumberService.product([totalBudget, (fundPercentage / 100)]));
  }

  financesPercentsChanged(totalBudget: number): void {
    const financesTotalPercent = NumberService.sum(
      this.finances.controls
        .map(control => control.get(this.constants.FORM_CONTROL_NAMES.fundPercentage)?.value || 0)
    );
    const financesTotalAmount = NumberService.sum(
      this.finances.controls.map(control => this.getFundAmount(control, totalBudget))
    );

    this.partnerPercentage.setValue(NumberService.minus(100, financesTotalPercent));
    this.partnerAmount.setValue(NumberService.minus(totalBudget, financesTotalAmount));
    this.partnerContributions.setValidators([totalContributionValidator(this.partnerAmount.value), Validators.maxLength(this.constants.MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS)]);
    this.partnerContributions.updateValueAndValidity();
  }

  private resetForm(financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO, totalBudget: number): void {
    const mainFunds = financingAndContribution.finances.filter(
      x => x.fundType === ProjectPartnerCoFinancingInputDTO.FundTypeEnum.MainFund
    );
    this.finances.clear();
    mainFunds.forEach(fund => this.addAdditionalFund(fund));
    if (!mainFunds.length) {
      this.addAdditionalFund();
    }

    this.financesPercentsChanged(totalBudget);
    this.resetPartnerContributions(financingAndContribution);
  }

  private partnerContributionsErrorArgs(): Observable<{}> {
    return this.coFinancingForm.valueChanges.pipe(
      startWith(this.coFinancingForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value)),
      map(currentTotal => {
        return {
          total: {
            difference: NumberService.toLocale(
              NumberService.truncateNumber(Math.abs(NumberService.minus(this.partnerAmount.value, currentTotal)))
            )
          }
        };
      })
    );
  }

  private formToProjectPartnerCoFinancingAndContributionInputDTO(): ProjectPartnerCoFinancingAndContributionInputDTO {
    const finances = [
      ...this.finances.value,
      {
        percentage: this.partnerPercentage.value,
        fundType: ProjectPartnerCoFinancingInputDTO.FundTypeEnum.PartnerContribution,
      } as ProjectPartnerCoFinancingInputDTO,
    ];

    return {
      finances,
      partnerContributions: this.partnerContributions.value as ProjectPartnerContributionDTO[]
    } as ProjectPartnerCoFinancingAndContributionInputDTO;
  }

  private handleTotalContributionCalculations(): void {

    this.publicContributionSubTotal$ = this.coFinancingForm.valueChanges.pipe(
      startWith(this.coFinancingForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.Public)),
    );

    this.privateContributionSubTotal$ = this.coFinancingForm.valueChanges.pipe(
      startWith(this.coFinancingForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.Private)),
    );
    this.automaticPublicContributionSubTotal$ = this.coFinancingForm.valueChanges.pipe(
      startWith(this.coFinancingForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.AutomaticPublic)),
    );
    this.contributionTotal$ = this.coFinancingForm.valueChanges.pipe(
      startWith(this.coFinancingForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value)),
    );
  }

  private getPartnerContributionTotal(partnerContributions: ProjectPartnerContributionDTO[], partnerStatus?: ProjectPartnerContributionDTO.StatusEnum): number {
    return NumberService.truncateNumber(NumberService.sum(partnerContributions
      .filter(source => source.status === partnerStatus || !partnerStatus)
      .map(item => item.amount ? item.amount : 0)
    ));
  }

  private handleResetForm(): void {
    combineLatest([
      this.pageStore.financingAndContribution$,
      this.pageStore.totalBudget$,
      this.formService.reset$.pipe(startWith(null)),
    ]).pipe(
      tap(([financingAndContribution, totalBudget]) => this.resetForm(financingAndContribution, totalBudget)),
      untilDestroyed(this)
    ).subscribe();
  }

  private initForm(): void {
    this.coFinancingForm = this.formBuilder.group({
      finances: this.formBuilder.array([]),
      partnerPercentage: [0, [Validators.required]],
      partnerAmount: [0],
      partnerContributions: this.formBuilder.array([], {
        validators: [totalContributionValidator(0), Validators.maxLength(this.constants.MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS)]
      }),
    });
    this.formService.init(this.coFinancingForm, this.pageStore.isProjectEditable$);
  }

  private resetPartnerContributions(financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO): void {
    this.partnerContributions.clear();
    financingAndContribution.partnerContributions.forEach((item: ProjectPartnerContributionDTO) => {
      this.addNewPartnerContribution(item);
    });
    this.formService.resetEditable();
  }
}
