import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {
  ProgrammeFundOutputDTO,
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
  ProjectPartnerCoFinancingInputDTO,
  ProjectPartnerCoFinancingOutputDTO,
  ProjectPartnerContributionDTO
} from '@cat/api';
import {filter, map, startWith, takeUntil, tap} from 'rxjs/operators';
import {Numbers} from '../../../../../common/utils/numbers';
import {BaseComponent} from '@common/components/base-component';
import {combineLatest, Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {FormService} from '@common/components/section/form/form.service';
import {Alert} from '@common/components/forms/alert';
import {Permission} from 'src/app/security/permissions/permission';

const MAX_100_NUMBER_REGEX = '^([0-9]{1,2}|100)$';
const MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS = 10;
const MAX_100_REGEXP = RegExp(MAX_100_NUMBER_REGEX);
const totalContributionValidator = (expectedAmount: number): ValidatorFn => (formArray: FormArray) => {
  const total = formArray.controls.map(item => item.get('amount')?.value).reduce((a, b) => a + b, 0);
  return expectedAmount === total
    ? null
    : {total: true};
};


@Component({
  selector: 'app-project-partner-co-financing',
  templateUrl: './project-partner-co-financing.component.html',
  styleUrls: ['./project-partner-co-financing.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerCoFinancingComponent extends BaseComponent implements OnInit, OnChanges {

  partnerContributionStatus = ProjectPartnerContributionDTO.StatusEnum;
  Alert = Alert;
  Numbers = Numbers;
  Permission = Permission;


  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;
  @Input()
  editable: boolean;
  @Input()
  financingAndContribution: ProjectPartnerCoFinancingAndContributionOutputDTO;
  @Input()
  totalAmount: number;
  @Input()
  callFunds: ProgrammeFundOutputDTO[] = [];

  @Output()
  save = new EventEmitter<ProjectPartnerCoFinancingAndContributionInputDTO>();
  @Output()
  cancelEdit = new EventEmitter<void>();

  constructor(public formService: FormService,
              private formBuilder: FormBuilder) {
    super();
  }

  coFinancingForm = this.createForm();

  publicContributionSubTotal$ = this.coFinancingForm.valueChanges.pipe(
    startWith(this.coFinancingForm.value),
    map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.Public)),
  );

  privateContributionSubTotal$ = this.coFinancingForm.valueChanges.pipe(
    startWith(this.coFinancingForm.value),
    map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.Private)),
  );
  automaticPublicContributionSubTotal$ = this.coFinancingForm.valueChanges.pipe(
    startWith(this.coFinancingForm.value),
    map(() => this.getPartnerContributionTotal(this.partnerContributions.value, this.partnerContributionStatus.AutomaticPublic)),
  );
  contributionTotal$: Observable<number> = this.coFinancingForm.valueChanges.pipe(
    startWith(this.coFinancingForm.value),
    map(() => this.getPartnerContributionTotal(this.partnerContributions.value)),
  );

  showTotalContributionWarning$ = combineLatest([this.contributionTotal$, this.formService.dirty$.pipe(startWith(false))]).pipe(
    map(([total, dirty]) => total !== this.myAmount && !dirty)
  );
  fundAmount = -1;
  myPercentage = -1;
  myAmount = -1;

  fundIdErrors = {
    required: 'project.partner.coFinancing.fundId.should.not.be.empty',
  };
  percentageErrors = {
    pattern: 'project.partner.coFinancing.percentage.invalid',
    required: 'project.partner.coFinancing.percentage.invalid',
  };
  partnerContributionNameErrors = {
    required: 'project.partner.coFinancing.contribution.origin.name.required',
  };
  partnerContributionStatusErrors = {
    required: 'project.partner.coFinancing.contribution.origin.amount.required',
  };
  partnerContributionAmountErrors = {
    required: 'project.partner.coFinancing.contribution.origin.amount.required',
    min: 'project.partner.coFinancing.contribution.origin.amount.min.invalid',
  };
  partnerContributionErrors = {
    total: 'project.partner.coFinancing.contribution.origin.total.invalid',
    maxlength: 'project.partner.coFinancing.contribution.origin.max.length',
  };

  partnerContributionErrorsArgs$: Observable<{}> = this.coFinancingForm.valueChanges.pipe(
    startWith(this.coFinancingForm.value),
    map(() => this.getPartnerContributionTotal(this.partnerContributions.value)),
    map(currentTotal => {
      return {total: {difference: Numbers.toLocale(Numbers.truncateNumber(Math.abs(Numbers.minus(this.myAmount, currentTotal))))}};
    })
  );

  ngOnInit(): void {
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.partner.budget.options.save.success'))
      )
      .subscribe();

    this.coFinancingForm.get('percentage')
      ?.valueChanges
      .pipe(
        takeUntil(this.destroyed$),
        map(percentage => percentage === '' ? 0 : percentage),
        filter(percentage => MAX_100_REGEXP.test(percentage)),
      )
      .subscribe(percentage => this.performCalculation(percentage));

    this.formService.init(this.coFinancingForm);
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.totalAmount) {
      this.cancel();
    }
    if (changes.financingAndContribution) {
      this.resetPartnerContributions();
    }
  }

  addNewPartnerContribution(initialValue?: ProjectPartnerContributionDTO): void {
    if (!initialValue) {
      this.coFinancingForm.markAsDirty();
    }
    this.partnerContributions.push(this.formBuilder.group({
      name: this.formBuilder.control(initialValue ? initialValue.name : '', [Validators.required]),
      status: this.formBuilder.control(initialValue ? initialValue.status : '', [Validators.required]),
      amount: this.formBuilder.control(initialValue ? initialValue.amount : 0, [Validators.required, Validators.min(0)]),
      isPartner: this.formBuilder.control(initialValue ? initialValue.isPartner : false)
    }));
  }

  deletePartnerContribution(index: number): void {
    this.coFinancingForm.markAsDirty();
    this.partnerContributions.removeAt(index);
  }

  resetForm(): void {
    const inputValues = this.financingAndContribution.finances.find((x: ProjectPartnerCoFinancingOutputDTO) => !!x.fund);
    this.coFinancingForm.controls.fundId.setValue(inputValues?.fund.id);
    this.coFinancingForm.controls.percentage.setValue(inputValues?.percentage || 0);
    this.resetPartnerContributions();
  }

  onSubmit(): void {
    this.save.emit({
      finances: [
        {
          fundId: this.coFinancingForm.controls.fundId.value,
          percentage: this.coFinancingForm.controls.percentage.value,
        } as ProjectPartnerCoFinancingInputDTO,
        {
          percentage: this.myPercentage,
        } as ProjectPartnerCoFinancingInputDTO,
      ],
      partnerContributions: this.partnerContributions.value as ProjectPartnerContributionDTO[]
    } as ProjectPartnerCoFinancingAndContributionInputDTO);
  }

  cancel(): void {
    this.cancelEdit.emit();
    this.resetForm();
  }


  get partnerContributions(): FormArray {
    return this.coFinancingForm.get('partnerContributions') as FormArray;
  }

  private resetPartnerContributions(): void {
    this.partnerContributions.clear();
    this.financingAndContribution.partnerContributions.forEach((item: ProjectPartnerContributionDTO) => {
      this.addNewPartnerContribution(item);
    });
  }

  private performCalculation(percentage: number): void {
    this.myPercentage = Numbers.sum([100, -percentage]);
    this.fundAmount = Numbers.truncateNumber(Numbers.product([this.totalAmount, (percentage / 100)]));
    this.myAmount = Numbers.sum([this.totalAmount, -this.fundAmount]);
    this.partnerContributions.setValidators([totalContributionValidator(this.myAmount), Validators.maxLength(MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS)]);
    this.partnerContributions.updateValueAndValidity();
  }

  private createForm(): FormGroup {
    return this.formBuilder.group({
      fundId: ['', Validators.required],
      percentage: ['', Validators.compose([
        Validators.pattern(MAX_100_NUMBER_REGEX),
        Validators.required,
      ])
      ],
      partnerContributions: this.formBuilder.array([], {
        validators: [totalContributionValidator(this.myAmount), Validators.maxLength(MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS)]
      }),
    });
  }

  private getPartnerContributionTotal(partnerContributions: ProjectPartnerContributionDTO[], partnerStatus?: ProjectPartnerContributionDTO.StatusEnum): number {
    return Numbers.truncateNumber(Numbers.sum(partnerContributions
      .filter(source => source.status === partnerStatus || !partnerStatus)
      .map(item => item.amount ? item.amount : 0)
    ));
  }

}
