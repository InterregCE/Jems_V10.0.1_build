import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Tools} from '../../common/utils/tools';
import {combineLatest, Observable} from 'rxjs';
import {CallDetailDTO, CallDTO, CallUpdateRequestDTO, OutputProgrammeStrategy, ProgrammeFundDTO} from '@cat/api';
import {CallPriorityCheckbox} from '../containers/model/call-priority-checkbox';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {FormService} from '@common/components/section/form/form.service';
import {CallPageSidenavService} from '../services/call-page-sidenav.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import moment from 'moment';
import {Alert} from '@common/components/forms/alert';
import {CallDetailPageStore} from './call-detail-page-store.service';

@Component({
  selector: 'app-call-detail-page',
  templateUrl: './call-detail-page.component.html',
  styleUrls: ['./call-detail-page.component.scss'],
  providers: [CallDetailPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallDetailPageComponent {
  private static readonly DATE_SHOULD_BE_VALID = 'common.date.should.be.valid';
  private static readonly CALL_INVALID_PERIOD = 'call.lengthOfPeriod.invalid.period';

  Alert = Alert;
  tools = Tools;

  callId = this.activatedRoute?.snapshot?.params?.callId;
  publishPending = false;
  published = false;

  data$: Observable<{
    call: CallDetailDTO,
    userCanApply: boolean,
    callIsEditable: boolean,
    priorityCheckboxes: CallPriorityCheckbox[],
    initialPriorityCheckboxes: CallPriorityCheckbox[],
    strategies: OutputProgrammeStrategy[],
    initialStrategies: OutputProgrammeStrategy[],
    funds: ProgrammeFundDTO[],
    initialFunds: ProgrammeFundDTO[]
  }>;

  inputErrorMessages = {
    max: CallDetailPageComponent.CALL_INVALID_PERIOD,
    min: CallDetailPageComponent.CALL_INVALID_PERIOD,
    matDatetimePickerParse: CallDetailPageComponent.DATE_SHOULD_BE_VALID,
    matDatetimePickerMin: 'common.error.field.start.before.end',
    matDatetimePickerMax: 'common.error.field.end.after.start'
  };

  dateNameArgs = {
    startDate: 'start date',
    endDate: 'end date'
  };

  inputErrorMessagesForEndDateStep1 = {
    ...this.inputErrorMessages,
    matDatetimePickerMin: 'call.endDateTimeStep1.needs.to.be.between.start.and.end',
    matDatetimePickerMax: 'call.endDateTimeStep1.needs.to.be.between.start.and.end'
  };

  callForm = this.formBuilder.group({
    name: ['', [Validators.required, Validators.maxLength(250)]],
    is2Step: [false, Validators.required],
    startDateTime: ['', Validators.required],
    endDateTimeStep1: [''],
    endDateTime: ['', Validators.required],
    description: [[], Validators.maxLength(1000)],
    lengthOfPeriod: ['', [Validators.required, Validators.max(99), Validators.min(1)]],
    isAdditionalFundAllowed: [false]
  });

  constructor(private router: Router,
              private formBuilder: FormBuilder,
              private pageStore: CallDetailPageStore,
              private formService: FormService,
              private activatedRoute: ActivatedRoute,
              private callNavService: CallPageSidenavService) {
    this.formService.init(this.callForm);
    this.formService.setCreation(!this.callId);

    this.data$ = combineLatest([
      this.pageStore.call$,
      this.pageStore.userCanApply$,
      this.pageStore.callIsEditable$,
      this.pageStore.allPriorities$,
      this.pageStore.allActiveStrategies$,
      this.pageStore.allFunds$
    ])
      .pipe(
        map(([call, userCanApply, callIsEditable, allPriorities, allActiveStrategies, allFunds]) => ({
          call,
          userCanApply,
          callIsEditable,
          priorityCheckboxes: this.getPriorities(allPriorities, call),
          initialPriorityCheckboxes: this.getPriorities(allPriorities, call),
          strategies: this.getStrategies(allActiveStrategies, call),
          initialStrategies: this.getStrategies(allActiveStrategies, call),
          funds: this.getFunds(allFunds, call),
          initialFunds: this.getFunds(allFunds, call),
        })),
        tap(data => this.resetForm(data.call, data.callIsEditable))
      );
  }

  onSubmit(savedCall: CallDetailDTO,
           priorityCheckboxes: CallPriorityCheckbox[],
           strategies: OutputProgrammeStrategy[],
           funds: ProgrammeFundDTO[]): void {
    const call = this.callForm.getRawValue(); // get raw value to include disabled controls
    call.priorityPolicies = priorityCheckboxes
      .flatMap(checkbox => checkbox.getCheckedChildPolicies());
    call.strategies = strategies.filter(strategy => strategy.active).map(strategy => strategy.strategy);
    call.fundIds = funds.filter(fund => fund.selected).map(fund => fund.id);
    if (!this.callForm.controls.is2Step.value) {
      call.endDateTimeStep1 = null;
      call.is2Step = null;
    }

    if (!this.callId) {
      this.createCall(call);
      return;
    }
    this.updateCall(call);
  }

  private createCall(call: CallUpdateRequestDTO): void {
    this.pageStore.createCall(call)
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
  }

  private updateCall(call: CallUpdateRequestDTO): void {
    call.id = this.callId;
    this.pageStore.saveCall(call)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.detail.save.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  onCancel(call: CallDetailDTO, callIsEditable: boolean): void {
    if (call?.id) {
      this.resetForm(call, callIsEditable);
      return;
    }
    this.callNavService.redirectToCallOverview();
  }

  publishCall(): void {
    this.publishPending = true;
    this.pageStore.publishCall(this.callId)
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

  publishingRequirementsNotAchieved(priorityCheckboxes: CallPriorityCheckbox[],
                                    funds: ProgrammeFundDTO[]): boolean {
    return (priorityCheckboxes && !priorityCheckboxes.some(priority => priority.checked || priority.someChecked())
      || !this.callForm.get('lengthOfPeriod')?.value
      || funds.filter(fund => fund.selected).map(fund => fund.id).length === 0);
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }

  resetForm(call: CallDetailDTO, callIsEditable: boolean): void {
    this.callForm.patchValue(call);
    if (call.endDateTimeStep1) {
      this.callForm.get('is2Step')?.setValue(true);
    }
    this.published = call?.status === CallDetailDTO.StatusEnum.PUBLISHED;
    if (this.published || !callIsEditable) {
      this.callForm.disable();
    }
    if (this.published && callIsEditable) {
      // enable some fields when call is published
      this.callForm.controls.name.enable();
      this.callForm.controls.description.enable();
      if (this.callForm.controls.is2Step) {
        this.callForm.controls.endDateTimeStep1.enable();
      }
      this.callForm.controls.endDateTime.enable();
      if (!call.isAdditionalFundAllowed) {
        this.callForm.controls.isAdditionalFundAllowed.enable();
      }
    }
  }

  confirmData(): Observable<ConfirmDialogData> {
    return this.pageStore.isFirstCall$
      .pipe(
        map(isFirstCall => ({
          title: 'call.dialog.title',
          message: 'call.dialog.message',
          warnMessage: isFirstCall ? 'call.dialog.message.and.additional.message' : undefined
        }))
      );
  }

  isOpen(call: CallDTO): boolean {
    const currentDate = moment(new Date());
    const endDateTime = call.endDateTimeStep1 || call.endDateTime;
    return currentDate.isBefore(endDateTime) && currentDate.isAfter(call.startDateTime);
  }

  applyToCall(callId: number): void {
    this.router.navigate(['/app/project/applyTo/' + callId]);
  }

  private getStrategies(allActiveStrategies: OutputProgrammeStrategy[], call: CallDetailDTO): OutputProgrammeStrategy[] {
    const savedStrategies = allActiveStrategies
      .filter(strategy => strategy.active)
      .map(element => ({strategy: element.strategy, active: false} as OutputProgrammeStrategy));
    if (!call || !call.strategies?.length) {
      return savedStrategies;
    }
    savedStrategies
      .filter(element => call.strategies.includes(element.strategy))
      .forEach(element => element.active = true);
    return savedStrategies;
  }

  private getPriorities(allPriorities: CallPriorityCheckbox[], call: CallDetailDTO): CallPriorityCheckbox[] {
    if (!call || !call.objectives) {
      return allPriorities;
    }
    const savedPolicies = call.objectives
      .flatMap(priority => priority.specificObjectives)
      .map(specificObjectives => specificObjectives.programmeObjectivePolicy);
    return allPriorities.map(priority => CallPriorityCheckbox.fromSavedPolicies(priority, savedPolicies));
  }

  private getFunds(allFunds: ProgrammeFundDTO[], call: CallDetailDTO): ProgrammeFundDTO[] {
    const savedFunds = allFunds
      .filter(fund => fund.selected)
      .map(element => ({
        id: element.id,
        abbreviation: element.abbreviation,
        description: element.description,
        selected: false
      } as ProgrammeFundDTO));
    if (!call || !call.funds?.length) {
      return savedFunds;
    }
    const callFundIds = call.funds.map(element => element.id ? element.id : element);
    savedFunds
      .filter(element => callFundIds.includes(element.id))
      .forEach(element => element.selected = true);
    return savedFunds;
  }

}
