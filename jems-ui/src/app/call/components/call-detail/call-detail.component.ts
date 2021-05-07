import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {CallDetailDTO, CallDTO, OutputProgrammeStrategy, ProgrammeFundDTO} from '@cat/api';
import {catchError, take, tap} from 'rxjs/operators';
import {CallPriorityCheckbox} from '../../containers/model/call-priority-checkbox';
import {Tools} from '../../../common/utils/tools';
import {FormService} from '@common/components/section/form/form.service';
import {CallStore} from '../../services/call-store.service';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';
import {ProgrammeEditableStateStore} from '../../../programme/programme-page/services/programme-editable-state-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {Router} from '@angular/router';
import moment from 'moment';

@UntilDestroy()
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

  Alert = Alert;

  tools = Tools;
  isFirstCall: boolean;
  publishPending = false;

  @Input()
  call: CallDetailDTO;
  @Input()
  priorityCheckboxes: CallPriorityCheckbox[];
  @Input()
  strategies: OutputProgrammeStrategy[];
  @Input()
  funds: ProgrammeFundDTO[];
  @Input()
  isApplicant: boolean;
  @Input()
  initialPriorityCheckboxes: CallPriorityCheckbox[];
  @Input()
  initialStrategies: OutputProgrammeStrategy[];
  @Input()
  initialFunds: ProgrammeFundDTO[];

  inputErrorMessages = {
    required: 'common.error.field.blank',
    maxlength: 'common.error.field.max.length',
    max: CallDetailComponent.CALL_INVALID_PERIOD,
    min: CallDetailComponent.CALL_INVALID_PERIOD,
    matDatetimePickerParse: CallDetailComponent.DATE_SHOULD_BE_VALID,
    matDatetimePickerMin: 'common.error.start.before.end',
    matDatetimePickerMax: 'common.error.end.after.start'
  };

  inputErrorMessagesForEndDateStep1 = {
    ...this.inputErrorMessages,
    matDatetimePickerMin: 'call.endDateTimeStep1.needs.to.be.between.start.and.end',
    matDatetimePickerMax: 'call.endDateTimeStep1.needs.to.be.between.start.and.end'
  };

  editable = false;
  published = false;

  callForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(250)
    ])],
    is2Step: [false, Validators.required],
    startDateTime: ['', Validators.required],
    endDateTimeStep1: [''],
    endDateTime: ['', Validators.required],
    description: [[], Validators.maxLength(1000)],
    lengthOfPeriod: ['', Validators.compose(
      [Validators.required, Validators.max(99), Validators.min(1)])],
    multipleFundsAllowed: [false]
  });

  constructor(private router: Router,
              private formBuilder: FormBuilder,
              private callStore: CallStore,
              private formService: FormService,
              private callNavService: CallPageSidenavService,
              private programmeEditableStateStore: ProgrammeEditableStateStore) {
    this.programmeEditableStateStore.init();
    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
      tap(isProgrammeEditingLimited => this.isFirstCall = !isProgrammeEditingLimited),
      untilDestroyed(this)
    ).subscribe();
  }

  ngOnInit(): void {
    this.formService.init(this.callForm);
    this.formService.setCreation(!this.call?.id);
    this.editable = this.call?.status !== CallDetailDTO.StatusEnum.PUBLISHED && !this.isApplicant;
    this.published = this.call?.status === CallDetailDTO.StatusEnum.PUBLISHED;
    this.formService.setEditable(this.editable);
    this.callForm.controls.multipleFundsAllowed.setValue(this.call.isAdditionalFundAllowed);
    this.resetForm();
    if (this.call && this.call.status === CallDetailDTO.StatusEnum.PUBLISHED && !this.isApplicant) {
      this.callForm.controls.name.enable();
      this.callForm.controls.description.enable();
      if (this.callForm.controls.is2Step) {
        this.callForm.controls.endDateTimeStep1.enable();
      }
      this.callForm.controls.endDateTime.enable();
      if (!this.call.isAdditionalFundAllowed) {
        this.callForm.controls.multipleFundsAllowed.enable();
      }
    }
  }

  onSubmit(): void {
    const call = this.callForm.value;
    call.priorityPolicies = this.priorityCheckboxes
      .flatMap(checkbox => checkbox.getCheckedChildPolicies());
    call.strategies = this.buildUpdateEntityStrategies();
    call.fundIds = this.buildUpdateEntityFunds();
    call.isAdditionalFundAllowed = this.callForm.controls.multipleFundsAllowed.value;
    if (!this.callForm.controls.is2Step.value) {
      call.endDateTimeStep1 = null;
      call.is2Step = null;
    }

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
    call.startDateTime = this.call.startDateTime;
    call.lengthOfPeriod = this.call.lengthOfPeriod;
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
    this.publishPending = true;
    this.callStore.publishCall(this.call?.id)
      .pipe(
        take(1),
        tap(() => this.publishPending = false),
        tap(published => this.callNavService.redirectToCallOverview(
          {
            i18nKey: 'call.detail.publish.success',
            i18nArguments: {name: published.name}
          })
        ),
        catchError(err => this.formService.setError(err))
      ).subscribe();
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
    if (this.call.endDateTimeStep1) {
      this.callForm.get('is2Step')?.setValue(true);
    }
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

  confirmData(): ConfirmDialogData {
    return {
      title: 'call.dialog.title',
      message: this.isFirstCall ? 'call.dialog.message.and.additional.message' : 'call.dialog.message'
    };
  }

  isOpen(call: CallDTO): boolean {
    const currentDate = moment(new Date());
    return currentDate.isBefore(call.endDateTime) && currentDate.isAfter(call.startDateTime);
  }

  applyToCall(callId: number): void {
    this.router.navigate(['/app/project/applyTo/' + callId]);
  }
}
