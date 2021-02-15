import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {OutputCall, OutputProgrammeStrategy, ProgrammeFundOutputDTO} from '@cat/api';
import {Forms} from '../../../common/utils/forms';
import {catchError, filter, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {CallPriorityCheckbox} from '../../containers/model/call-priority-checkbox';
import {Tools} from '../../../common/utils/tools';
import {FormService} from '@common/components/section/form/form.service';
import {CallStore} from '../../services/call-store.service';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';

@Component({
  selector: 'app-call-detail',
  templateUrl: './call-detail.component.html',
  styleUrls: ['./call-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CallDetailComponent implements OnInit {
  private static readonly DATE_SHOULD_BE_VALID = 'common.date.should.be.valid';
  private static readonly CALL_INVALID_PERIOD = 'call.lengthOfPeriod.invalid.period';

  static ID = 'CallDetailComponent';
  tools = Tools;

  @Input()
  call: OutputCall;
  @Input()
  priorityCheckboxes: CallPriorityCheckbox[];
  @Input()
  strategies: OutputProgrammeStrategy[];
  @Input()
  funds: ProgrammeFundOutputDTO[];
  @Input()
  isApplicant: boolean;

  startDateErrors = {
    required: 'call.startDate.unknown',
    matDatetimePickerParse: CallDetailComponent.DATE_SHOULD_BE_VALID,
    matDatetimePickerMax: 'call.startDate.must.be.before.endDate',
  };
  endDateErrors = {
    required: 'call.endDate.unknown',
    matDatetimePickerParse: CallDetailComponent.DATE_SHOULD_BE_VALID,
    matDatetimePickerMin: 'call.endDate.must.be.after.startDate',
  };
  nameErrors = {
    required: 'call.name.unknown',
    maxlength: 'call.name.wrong.size'
  };
  descriptionErrors = {
    maxlength: 'call.description.wrong.size'
  };
  lengthOfPeriodErrors = {
    required: 'call.lengthOfPeriod.unknown',
    max: CallDetailComponent.CALL_INVALID_PERIOD,
    min: CallDetailComponent.CALL_INVALID_PERIOD,
  };
  editable = false;

  callForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(250)
    ])],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    description: [[], Validators.maxLength(1000)],
    lengthOfPeriod: ['', Validators.compose(
      [Validators.required, Validators.max(99), Validators.min(1)])],
    multipleFundsAllowed: [false]
  });

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private callStore: CallStore,
              private formService: FormService,
              private callNavService: CallPageSidenavService) {
  }

  ngOnInit(): void {
    this.formService.init(this.callForm);
    this.formService.setCreation(!this.call?.id);
    this.editable = this.call?.status !== OutputCall.StatusEnum.PUBLISHED && !this.isApplicant;
    this.formService.setEditable(this.editable);
    this.callForm.controls.multipleFundsAllowed.setValue(this.call.isAdditionalFundAllowed);
    this.resetForm();
  }

  onSubmit(): void {
    const call = this.callForm.value;
    call.priorityPolicies = this.priorityCheckboxes
      .flatMap(checkbox => checkbox.getCheckedChildPolicies());
    call.strategies = this.buildUpdateEntityStrategies();
    call.funds = this.buildUpdateEntityFunds();
    call.isAdditionalFundAllowed = this.callForm.controls.multipleFundsAllowed.value;

    if (!this.call.id) {
      this.callStore.createCall(call)
        .pipe(
          take(1),
          tap(created => this.callNavService.redirectToCallDetail(
            created.id,
            {
              i18nKey: 'call.detail.created.success',
              i18nArguments: {name: created.name}
            })
          ),
          catchError(err => this.formService.setError(err))
        ).subscribe();
      return;
    }

    call.id = this.call.id;
    this.callStore.saveCall(call)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.detail.save.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  onCancel(): void {
    if (this.call?.id) {
      this.resetForm();
      return;
    }
    this.callNavService.redirectToCallOverview();
  }

  publishCall(): void {
    Forms.confirmDialog(
      this.dialog,
      'call.dialog.title',
      'call.dialog.message'
    ).pipe(
      take(1),
      filter(yes => !!yes)
    ).subscribe(() => {
      this.callStore.publishCall(this.call?.id)
        .pipe(
          take(1),
          tap(published => this.callNavService.redirectToCallOverview(
            {
              i18nKey: 'call.detail.publish.success',
              i18nArguments: {name: published.name}
            })
          ),
          catchError(err => this.formService.setError(err))
        ).subscribe();
    });
  }

  publishingRequirementsNotAchieved(): boolean {
    return (this.priorityCheckboxes
      && !this.priorityCheckboxes.some(priority => priority.checked || priority.someChecked())
      || !this.call.lengthOfPeriod
      || this.buildUpdateEntityFunds().length === 0);
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }

  resetForm(): void {
    this.callForm.patchValue(this.call);
  }

  private buildUpdateEntityStrategies(): OutputProgrammeStrategy.StrategyEnum[] {
    return this.strategies
      .filter(strategy => strategy.active)
      .map(strategy => strategy.strategy);
  }

  private buildUpdateEntityFunds(): number[] {
    return this.funds
      .filter(fund => fund.selected)
      .map(fund => fund.id);
  }
}
