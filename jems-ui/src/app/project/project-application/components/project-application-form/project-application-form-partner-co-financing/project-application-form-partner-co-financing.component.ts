import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges,
} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
  ProjectPartnerCoFinancingOutputDTO,
  ProjectPartnerCoFinancingInputDTO,
  ProgrammeFundOutputDTO
} from '@cat/api';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {Permission} from '../../../../../security/permissions/permission';
import {filter, map, takeUntil, tap} from 'rxjs/operators';
import {Numbers} from '../../../../../common/utils/numbers';
import {BaseComponent} from '@common/components/base-component';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {FormService} from '@common/components/section/form/form.service';

const MAX_100_NUMBER_REGEX = '^([0-9]{1,2}|100)$';
const MAX_100_REGEXP = RegExp(MAX_100_NUMBER_REGEX);

@Component({
  selector: 'app-project-application-form-partner-co-financing',
  templateUrl: './project-application-form-partner-co-financing.component.html',
  styleUrls: ['./project-application-form-partner-co-financing.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerCoFinancingComponent extends BaseComponent implements OnInit, OnChanges {
  Numbers = Numbers;

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

  fundAmount = -1;
  myPercentage = -1;
  myAmount = -1;

  Permission = Permission;

  coFinancingForm = this.formBuilder.group({
    fundId: ['', Validators.required],
    percentage: ['', Validators.compose([
      Validators.pattern(MAX_100_NUMBER_REGEX),
      Validators.required,
    ])
    ],
  });

  fundIdErrors = {
    required: 'project.partner.coFinancing.fundId.should.not.be.empty',
  };
  percentageErrors = {
    pattern: 'project.partner.coFinancing.percentage.invalid',
    required: 'project.partner.coFinancing.percentage.invalid',
  };

  constructor(private formService: FormService,
              private formBuilder: FormBuilder,
              private sideNavService: SideNavService) {
    super();
  }

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

  resetForm(): void {
    const inputValues = this.financingAndContribution.finances.find((x: ProjectPartnerCoFinancingOutputDTO) => !!x.fund);
    this.coFinancingForm.controls.fundId.setValue(inputValues?.fund.id);
    this.coFinancingForm.controls.percentage.setValue(inputValues?.percentage || 0);
  }

  private performCalculation(percentage: number): void {
    this.myPercentage = Numbers.sum([100, -percentage]);
    this.fundAmount = Numbers.truncateNumber(Numbers.product([this.totalAmount, (percentage / 100)]));
    this.myAmount = Numbers.sum([this.totalAmount, -this.fundAmount]);
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
      ]
    } as ProjectPartnerCoFinancingAndContributionInputDTO);
  }

  cancel(): void {
    this.cancelEdit.emit();
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.totalAmount) {
      this.cancel();
    }
  }
}
