import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {
  ProgrammeFundOutputDTO,
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
import {NumberService} from '../../../../common/services/number.service';
import {ProjectPartnerDetailPageStore} from '../project-partner-detail-page.store';
import {ProjectPartnerCoFinancingTapConstants} from './project-partner-co-financing-tap.constants';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';


const totalContributionValidator = (expectedAmount: number): ValidatorFn => (formArray: FormArray) => {
  const total = formArray.controls.map(item => item.get('amount')?.value).reduce((a, b) => a + b, 0);
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

  constants = ProjectPartnerCoFinancingTapConstants;
  partnerContributionStatus = ProjectPartnerContributionDTO.StatusEnum;
  Alert = Alert;

  data$: Observable<{
    financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO,
    callFunds: ProgrammeFundOutputDTO[],
    totalBudget: number,
    publicContributionSubTotal: number,
    privateContributionSubTotal: number,
    automaticPublicContributionSubTotal: number,
    contributionTotal: number,
    showTotalContributionWarning: boolean,
    partnerContributionErrorsArgs: ValidationErrors | null
  }>;

  coFinancingForm: FormGroup;

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

  ngOnInit(): void {

    this.initForm();

    this.handleResetForm();

    this.handleTotalContributionCalculations();

    this.handleCoFinancingCalculations();

    this.showTotalContributionWarning$ = combineLatest([this.contributionTotal$, this.formService.dirty$.pipe(startWith(false))]).pipe(
      map(([total, dirty]) => total !== this.partnerAmount.value && !dirty)
    );

    this.partnerContributionErrorsArgs$ = this.partnerContributionsErrorArgs();

    this.data$ = combineLatest([
      this.pageStore.financingAndContribution$,
      this.pageStore.callFunds$,
      this.pageStore.totalBudget$,
      this.privateContributionSubTotal$,
      this.publicContributionSubTotal$,
      this.automaticPublicContributionSubTotal$,
      this.contributionTotal$,
      this.showTotalContributionWarning$,
      this.partnerContributionErrorsArgs$,
    ]).pipe(
      map(([financingAndContribution, callFunds, totalBudget, privateContributionSubTotal, publicContributionSubTotal, automaticPublicContributionSubTotal, contributionTotal, showTotalContributionWarning, partnerContributionErrorsArgs]: any) => {
        return {
          financingAndContribution,
          callFunds,
          totalBudget,
          privateContributionSubTotal,
          publicContributionSubTotal,
          automaticPublicContributionSubTotal,
          contributionTotal,
          showTotalContributionWarning,
          partnerContributionErrorsArgs
        };
      }));

  }

  addNewPartnerContribution(initialValue?: ProjectPartnerContributionDTO): void {
    this.partnerContributions.push(this.formBuilder.group({
      name: [initialValue ? initialValue.name : '', [Validators.required]],
      status: [initialValue ? initialValue.status : '', [Validators.required]],
      amount: [initialValue ? initialValue.amount : 0, [Validators.required, Validators.min(0)]],
      isPartner: [initialValue ? initialValue.isPartner : false]
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

  private handleCoFinancingCalculations(): void {
    combineLatest([this.pageStore.totalBudget$, this.fundPercentage.valueChanges])
      .pipe(
        tap(([totalBudget, percentage]) => this.updateCoFinancingCalculations(totalBudget, percentage)),
        untilDestroyed(this)
      ).subscribe();
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
    return {
      finances: [
        {
          fundId: this.fundId.value,
          percentage: this.fundPercentage.value,
        } as ProjectPartnerCoFinancingInputDTO,
        {
          percentage: this.partnerPercentage.value,
        } as ProjectPartnerCoFinancingInputDTO,
      ],
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

  private updateCoFinancingCalculations(totalBudget: number, percentage: number): void {
    this.partnerPercentage.setValue(NumberService.minus(100, percentage));
    this.fundAmount.setValue(NumberService.truncateNumber(NumberService.product([totalBudget, (percentage / 100)])));
    this.partnerAmount.setValue(NumberService.minus(totalBudget, this.fundAmount.value));
    this.partnerContributions.setValidators([totalContributionValidator(this.partnerAmount.value), Validators.maxLength(this.constants.MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS)]);
    this.partnerContributions.updateValueAndValidity();
  }

  private getPartnerContributionTotal(partnerContributions: ProjectPartnerContributionDTO[], partnerStatus?: ProjectPartnerContributionDTO.StatusEnum): number {
    return NumberService.truncateNumber(NumberService.sum(partnerContributions
      .filter(source => source.status === partnerStatus || !partnerStatus)
      .map(item => item.amount ? item.amount : 0)
    ));
  }

  private initForm(): void {
    this.coFinancingForm = this.formBuilder.group({
      fundId: [null, Validators.required],
      fundAmount: [0],
      fundPercentage: [0, [Validators.pattern(this.constants.MAX_100_NUMBER_REGEX), Validators.required]],
      partnerPercentage: [0, [Validators.pattern(this.constants.MAX_100_NUMBER_REGEX), Validators.required]],
      partnerAmount: [0],
      partnerContributions: this.formBuilder.array([], {
        validators: [totalContributionValidator(0), Validators.maxLength(this.constants.MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS)]
      }),
    });
    this.formService.init(this.coFinancingForm, this.pageStore.isProjectEditable$);
  }

  private handleResetForm(): void {
    combineLatest([this.formService.reset$.pipe(startWith(null)), this.pageStore.financingAndContribution$]).pipe(
      tap(([, financingAndContribution]) => this.resetForm(financingAndContribution)),
      untilDestroyed(this)
    ).subscribe();
  }

  private resetForm(financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO): void {
    const inputValues = financingAndContribution.finances.find((x: ProjectPartnerCoFinancingOutputDTO) => !!x.fund);
    this.fundId.setValue(inputValues?.fund.id);
    this.fundPercentage.setValue(inputValues?.percentage || 0);
    this.resetPartnerContributions(financingAndContribution);
  }

  private resetPartnerContributions(financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO): void {
    this.partnerContributions.clear();
    financingAndContribution.partnerContributions.forEach((item: ProjectPartnerContributionDTO) => {
      this.addNewPartnerContribution(item);
    });
    this.formService.resetEditable();
  }


  get fundId(): FormControl {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.fundId) as FormControl;
  }

  get fundAmount(): FormControl {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.fundAmount) as FormControl;
  }

  get partnerAmount(): FormControl {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.partnerAmount) as FormControl;
  }

  get partnerPercentage(): FormControl {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.partnerPercentage) as FormControl;
  }

  get fundPercentage(): FormControl {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.fundPercentage) as FormControl;
  }

  get partnerContributions(): FormArray {
    return this.coFinancingForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributions) as FormArray;
  }
}
