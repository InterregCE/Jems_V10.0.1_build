import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {CallDetailDTO, OutputProgrammeStrategy, ProgrammeFundDTO} from '@cat/api';
import {Forms} from '../../../common/utils/forms';
import {catchError, filter, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {CallPriorityCheckbox} from '../../containers/model/call-priority-checkbox';
import {Tools} from '../../../common/utils/tools';
import {FormService} from '@common/components/section/form/form.service';
import {CallStore} from '../../services/call-store.service';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';
import {ProgrammeEditableStateStore} from '../../../programme/programme-page/services/programme-editable-state-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import { Alert } from '@common/components/forms/alert';

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
    matDatetimePickerMin: 'call.endDate.must.be.after.startDate',
    matDatetimePickerMax: 'common.error.start.before.end'
  };

  editable = false;
  published = false;

  callForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(250)
    ])],
    startDateTime: ['', Validators.required],
    endDateTime: ['', Validators.required],
    description: [[], Validators.maxLength(1000)],
    lengthOfPeriod: ['', Validators.compose(
      [Validators.required, Validators.max(99), Validators.min(1)])],
    multipleFundsAllowed: [false]
  });

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
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
      this.callForm.controls.endDateTime.enable();
    }
  }

  onSubmit(): void {
    const call = this.callForm.value;
    call.priorityPolicies = this.priorityCheckboxes
      .flatMap(checkbox => checkbox.getCheckedChildPolicies());
    call.strategies = this.buildUpdateEntityStrategies();
    call.fundIds = this.buildUpdateEntityFunds();
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
    Forms.confirmDialog(
      this.dialog,
      'call.dialog.title',
      this.isFirstCall ? 'call.dialog.message.and.additional.message' : 'call.dialog.message'
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
