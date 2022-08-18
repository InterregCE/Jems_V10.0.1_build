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
  CallFundRateDTO,
  ProgrammeFundDTO,
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
  ProjectPartnerCoFinancingInputDTO,
  ProjectPartnerCoFinancingOutputDTO,
  ProjectPartnerContributionDTO
} from '@cat/api';
import {catchError, filter, map, startWith, tap} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {FormService} from '@common/components/section/form/form.service';
import {Alert} from '@common/components/forms/alert';
import {NumberService} from '@common/services/number.service';
import {ProjectPartnerDetailPageStore} from '../project-partner-detail-page.store';
import {ProjectPartnerCoFinancingTabConstants} from '../project-partner-co-financing-tab/project-partner-co-financing-tab.constants';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';


const totalContributionValidator = (expectedAmount: number): ValidatorFn => (formArray: FormArray) => {
  const total = formArray.controls.map(item => item.get('amount')?.value).reduce((a, b) => NumberService.sum([a, b]), 0);
  return expectedAmount === total
    ? null
    : {total: true};
};

@UntilDestroy()
@Component({
  selector: 'jems-project-partner-co-financing-spf-tab',
  templateUrl: './project-partner-co-financing-spf-tab.component.html',
  styleUrls: ['./project-partner-co-financing-spf-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerCoFinancingSpfTabComponent implements OnInit {

  constants = ProjectPartnerCoFinancingTabConstants;
  partnerContributionStatus = ProjectPartnerContributionDTO.StatusEnum;
  Alert = Alert;
  APPLICATION_FORM = APPLICATION_FORM;

  data$: Observable<{
    coFinancingSpf: ProjectPartnerCoFinancingAndContributionOutputDTO;
    callFunds: Map<number, CallFundRateDTO>;
    totalSpfBudget: number;
    publicContributionSubTotal: number;
    privateContributionSubTotal: number;
    automaticPublicContributionSubTotal: number;
    contributionTotal: number;
    showTotalContributionWarning: boolean;
    partnerContributionErrorsArgs: ValidationErrors | null;
    editable: boolean;
  }>;

  coFinancingSpfForm: FormGroup;
  multipleFundsAllowed: boolean;

  private publicContributionSubTotal$: Observable<number>;
  private privateContributionSubTotal$: Observable<number>;
  private automaticPublicContributionSubTotal$: Observable<number>;
  private contributionTotal$: Observable<number>;
  private showTotalContributionWarning$: Observable<boolean>;
  private partnerContributionErrorsArgs$: Observable<{}>;

  constructor(public formService: FormService,
              private formBuilder: FormBuilder,
              private pageStore: ProjectPartnerDetailPageStore,
              private router: RoutingService,
              private activatedRoute: ActivatedRoute,
              private visibilityStatusService: FormVisibilityStatusService
  ) {
    visibilityStatusService.isVisible$((APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING)).pipe(
      untilDestroyed(this),
      filter(isVisible => !isVisible),
      tap(() => this.router.navigate(['../identity'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
    ).subscribe();
  }

  get partnerAmount(): FormControl {
    return this.coFinancingSpfForm.get(this.constants.FORM_CONTROL_NAMES.partnerAmount) as FormControl;
  }

  get partnerPercentage(): FormControl {
    return this.coFinancingSpfForm.get(this.constants.FORM_CONTROL_NAMES.partnerPercentage) as FormControl;
  }

  get partnerContributions(): FormArray {
    return this.coFinancingSpfForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributions) as FormArray;
  }

  get finances(): FormArray {
    return this.coFinancingSpfForm.get(this.constants.FORM_CONTROL_NAMES.finances) as FormArray;
  }

  ngOnInit(): void {
    this.initForm();
    this.handleResetForm();
    this.handleTotalContributionCalculations();

    this.showTotalContributionWarning$ = combineLatest([this.contributionTotal$, this.formService.dirty$.pipe(startWith(false))])
      .pipe(map(([total, dirty]) => total !== this.partnerAmount.value && !dirty));

    this.partnerContributionErrorsArgs$ = this.partnerContributionsErrorArgs();

    this.data$ = combineLatest([
      this.pageStore.coFinancingSpf$,
      this.pageStore.callFunds$,
      this.pageStore.totalSpfBudget$,
      this.pageStore.multipleFundsAllowed$,
      this.privateContributionSubTotal$,
      this.publicContributionSubTotal$,
      this.automaticPublicContributionSubTotal$,
      this.contributionTotal$,
      this.showTotalContributionWarning$,
      this.partnerContributionErrorsArgs$,
      this.pageStore.isProjectEditable$
    ]).pipe(
      map((
        [
          coFinancingSpf,
          callFunds,
          totalSpfBudget,
          multipleFundsAllowed,
          privateContributionSubTotal,
          publicContributionSubTotal,
          automaticPublicContributionSubTotal,
          contributionTotal,
          showTotalContributionWarning,
          partnerContributionErrorsArgs,
          editable
        ]: any) => {
        this.multipleFundsAllowed = multipleFundsAllowed;
        return {
          coFinancingSpf,
          callFunds,
          totalSpfBudget,
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
      name: [initialValue ? initialValue.name : '', [Validators.required, Validators.pattern(/(?!^\s+$)^.*$/m), Validators.maxLength(255)]],
      status: [initialValue ? initialValue.status : null, [Validators.required]],
      amount: [initialValue?.amount || 0, [Validators.required, Validators.min(0)]],
      partner: [initialValue ? initialValue.partner : false]
    }));
    if (!initialValue) {
      this.coFinancingSpfForm.markAsDirty();
    }
  }

  deletePartnerContribution(index: number): void {
    this.coFinancingSpfForm.markAsDirty();
    this.partnerContributions.removeAt(index);
  }

  updateCoFinancingSpf(): void {
    this.pageStore.updateCoFinancingSpf(
      this.formToProjectPartnerCoFinancingAndContributionInputDTO()
    ).pipe(
      tap(() => this.formService.setSuccess('project.partner.coFinancing.contribution.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  addAdditionalFund(callFunds: Map<number, CallFundRateDTO>, fund?: ProjectPartnerCoFinancingOutputDTO): void {
    const fundControl = this.formBuilder.group({
      fundId: this.formBuilder.control(fund?.fund?.id, Validators.required),
      fundType: ProjectPartnerCoFinancingInputDTO.FundTypeEnum.MainFund,
      percentage: this.formBuilder.control(fund?.percentage || 0, Validators.required),
    });
    this.finances.push(fundControl);
    if (!callFunds.get(fund?.fund.id as number)?.adjustable) {
      fundControl.get(this.constants.FORM_CONTROL_NAMES.fundPercentage)?.disable();
    }
  }

  notSelectedFunds(callFunds: Map<number, CallFundRateDTO>, currentFund?: AbstractControl): ProgrammeFundDTO[] {
    const selectedFundIds = this.finances.controls.map(
      control => control.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value
    );
    const currentFundId = currentFund?.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value;
    return [...callFunds.values()]
      .filter(fund => currentFundId === fund?.programmeFund?.id ? true : !selectedFundIds.includes(fund.programmeFund.id))
      .map(fund => fund.programmeFund);
  }

  deleteAdditionalFund(fundIndex: number, totalBudget: number): void {
    this.coFinancingSpfForm.markAsDirty();
    this.finances.removeAt(fundIndex);
    this.financesPercentsChanged(totalBudget);
  }

  canAddFund(callFunds: Map<number, CallFundRateDTO>, editable: boolean): boolean {
    return this.multipleFundsAllowed && editable
      && this.finances.length < this.constants.MAX_NUMBER_OF_FINANCES
      && !this.finances.controls.find(control => !control.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value)
      && !!this.notSelectedFunds(callFunds)?.length;
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

  callFundChanged(fund: AbstractControl, callFunds: Map<number, CallFundRateDTO>): void {
    const currentFundId = fund?.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value;
    const fundRate = callFunds.get(currentFundId);

    setTimeout(() => {
      // timeout needed in order for the currency mask max option to be updated first
      const funRateControl = fund.get(this.constants.FORM_CONTROL_NAMES.fundPercentage);
      funRateControl?.setValue(fundRate?.rate || 0);
      if (!fundRate?.adjustable) {
        funRateControl?.disable();
      } else {
        funRateControl?.enable();
      }
    },         100);
  }

  getMaxRate(fund: AbstractControl, callFunds: Map<number, CallFundRateDTO>): number {
    return callFunds.get(fund.get(this.constants.FORM_CONTROL_NAMES.fundId)?.value)?.rate || 100;
  }

  private resetForm(coFinancingSpf: ProjectPartnerCoFinancingAndContributionOutputDTO,
                    totalSpfBudget: number,
                    callFunds: Map<number, CallFundRateDTO>,
                    isProjectEditable: boolean): void {
    const mainFunds = coFinancingSpf.finances.filter(
      x => x.fundType === ProjectPartnerCoFinancingInputDTO.FundTypeEnum.MainFund
    );
    this.finances.clear();
    mainFunds.forEach(fund => this.addAdditionalFund(callFunds, fund));
    if (!mainFunds.length) {
      this.addAdditionalFund(callFunds);
    }

    this.financesPercentsChanged(totalSpfBudget);
    this.resetPartnerContributions(coFinancingSpf);

    if (!isProjectEditable) {
      this.formService.resetEditable();
    }
  }

  private partnerContributionsErrorArgs(): Observable<{}> {
    return this.coFinancingSpfForm.valueChanges.pipe(
      startWith(this.coFinancingSpfForm.value),
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
      ...this.finances.getRawValue(), // raw value because of fixed (disabled) fund rates
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

    this.publicContributionSubTotal$ = this.coFinancingSpfForm.valueChanges.pipe(
      startWith(this.coFinancingSpfForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.Public)),
    );

    this.privateContributionSubTotal$ = this.coFinancingSpfForm.valueChanges.pipe(
      startWith(this.coFinancingSpfForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.Private)),
    );
    this.automaticPublicContributionSubTotal$ = this.coFinancingSpfForm.valueChanges.pipe(
      startWith(this.coFinancingSpfForm.value),
      map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.AutomaticPublic)),
    );
    this.contributionTotal$ = this.coFinancingSpfForm.valueChanges.pipe(
      startWith(this.coFinancingSpfForm.value),
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
      this.pageStore.coFinancingSpf$,
      this.pageStore.totalSpfBudget$,
      this.pageStore.callFunds$,
      this.pageStore.isProjectEditable$,
      this.formService.reset$.pipe(startWith(null)),
    ]).pipe(
      tap(([coFinancingSpf, totalSpfBudget, callFunds, isProjectEditable]) =>
        this.resetForm(coFinancingSpf, totalSpfBudget, callFunds, isProjectEditable)
      ),
      untilDestroyed(this)
    ).subscribe();
  }

  private initForm(): void {
    this.coFinancingSpfForm = this.formBuilder.group({
      finances: this.formBuilder.array([]),
      partnerPercentage: [0, [Validators.required, Validators.min(0)]],
      partnerAmount: [0],
      partnerContributions: this.formBuilder.array([], {
        validators: [totalContributionValidator(0), Validators.maxLength(this.constants.MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS)]
      }),
    });
    this.formService.init(this.coFinancingSpfForm, this.pageStore.isProjectEditable$);
  }

  private resetPartnerContributions(financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO): void {
    this.partnerContributions.clear();
    financingAndContribution.partnerContributions.forEach((item: ProjectPartnerContributionDTO) => {
      this.addNewPartnerContribution(item);
    });
  }
}
