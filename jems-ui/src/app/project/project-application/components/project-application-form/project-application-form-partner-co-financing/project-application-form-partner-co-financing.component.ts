import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges,
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormState} from '@common/components/forms/form-state';
import {InputProjectPartnerCoFinancingWrapper, InputProjectPartnerCoFinancing, OutputProjectPartnerCoFinancing, OutputProgrammeFund} from '@cat/api';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {Permission} from '../../../../../security/permissions/permission';
import {filter, map, takeUntil} from 'rxjs/operators';
import {Numbers} from '../../../../../common/utils/numbers';

const MAX_100_NUMBER_REGEX = '^([0-9]{1,2}|100)$'
const MAX_100_REGEXP = RegExp(MAX_100_NUMBER_REGEX)

@Component({
  selector: 'app-project-application-form-partner-co-financing',
  templateUrl: './project-application-form-partner-co-financing.component.html',
  styleUrls: ['./project-application-form-partner-co-financing.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerCoFinancingComponent extends ViewEditForm implements OnInit, OnChanges {
  Numbers = Numbers;

  @Input()
  editable: boolean;
  @Input()
  finances: OutputProjectPartnerCoFinancing[];
  @Input()
  totalAmount: number;
  @Input()
  callFunds: OutputProgrammeFund[] = [];

  @Output()
  save = new EventEmitter<InputProjectPartnerCoFinancingWrapper>();
  @Output()
  cancelEdit = new EventEmitter<void>();

  fundAmount = -1
  myPercentage = -1
  myAmount = -1

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

  constructor(
    protected changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private sideNavService: SideNavService,
  ) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.coFinancingForm.get('percentage')
      ?.valueChanges
      .pipe(
        takeUntil(this.destroyed$),
        map(percentage => percentage === '' ? 0 : percentage),
        filter(percentage => MAX_100_REGEXP.test(percentage)),
      )
      .subscribe(percentage => this.performCalculation(percentage));

    this.initForm();
  }

  private initForm(): void {
    const inputValues = this.finances.find(x => !!x.fund)
    this.coFinancingForm.controls.fundId.setValue(inputValues?.fund.id);
    this.coFinancingForm.controls.percentage.setValue(inputValues?.percentage || 0);
  }

  private performCalculation(percentage: number): void {
    this.myPercentage = Numbers.sum([100, -percentage]);
    this.fundAmount = Numbers.truncateNumber(Numbers.product([this.totalAmount, (percentage / 100)]));
    this.myAmount = Numbers.sum([this.totalAmount, -this.fundAmount]);
  }

  protected enterViewMode() {
    this.sideNavService.setAlertStatus(false);
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  getForm(): FormGroup | null {
    return this.coFinancingForm;
  }

  onSubmit() {
    this.submitted = true;
    this.save.emit({ finances: [
        {
          fundId: this.coFinancingForm.controls.fundId.value,
          percentage: this.coFinancingForm.controls.percentage.value,
        } as InputProjectPartnerCoFinancing,
        {
          percentage: this.myPercentage,
        } as InputProjectPartnerCoFinancing,
      ] });
    this.changeFormState$.next(FormState.VIEW);
  }

  cancel(): void {
    this.changeFormState$.next(FormState.VIEW);
    this.cancelEdit.emit();
    this.coFinancingForm.reset();
    this.initForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.totalAmount) {
      this.cancel();
    }
  }
}
