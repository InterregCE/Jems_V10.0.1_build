import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputCallCreate, InputCallUpdate, OutputCall, OutputProgrammeFund, OutputProgrammeStrategy} from '@cat/api';
import {Forms} from '../../../common/utils/forms';
import {filter, take, takeUntil} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {CallPriorityCheckbox} from '../../containers/model/call-priority-checkbox';
import {Tools} from '../../../common/utils/tools';
import {BaseComponent} from '@common/components/base-component';
import {EventBusService} from '../../../common/services/event-bus/event-bus.service';

@Component({
  selector: 'app-call-detail',
  templateUrl: './call-detail.component.html',
  styleUrls: ['./call-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CallDetailComponent extends BaseComponent implements OnInit {
  static ID = 'CallDetailComponent';
  tools = Tools;
  CallDetailComponent = CallDetailComponent;

  @Input()
  call: OutputCall;
  @Input()
  priorityCheckboxes: CallPriorityCheckbox[];
  @Input()
  strategies: OutputProgrammeStrategy[];
  @Input()
  funds: OutputProgrammeFund[];

  @Output()
  create: EventEmitter<InputCallCreate> = new EventEmitter<InputCallCreate>();
  @Output()
  update: EventEmitter<InputCallUpdate> = new EventEmitter<InputCallUpdate>();
  @Output()
  publish: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>();

  startDateErrors = {
    required: 'call.startDate.unknown',
    matDatetimePickerParse: 'common.date.should.be.valid',
    matDatetimePickerMax: 'call.startDate.must.be.before.endDate',
  };
  endDateErrors = {
    required: 'call.endDate.unknown',
    matDatetimePickerParse: 'common.date.should.be.valid',
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
    max: 'call.lengthOfPeriod.invalid.period',
    min: 'call.lengthOfPeriod.invalid.period',
  };
  published = false;

  callForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(250)
    ])],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    description: ['', Validators.maxLength(1000)],
    lengthOfPeriod: ['', Validators.compose(
      [Validators.required, Validators.max(99), Validators.min(1)])]
  });

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private eventBusService: EventBusService) {
    super();
  }

  ngOnInit(): void {
    this.published = this.call?.status === OutputCall.StatusEnum.PUBLISHED;
    if (this.published) {
      this.getForm()?.disable();
    }
    this.resetForm();
  }

  getForm(): FormGroup | null {
    return this.callForm;
  }

  onSubmit(): void {
    const call = {
      name: this.callForm?.controls?.name?.value,
      startDate: this.callForm?.controls?.startDate?.value,
      endDate: this.callForm?.controls?.endDate?.value,
      description: this.callForm?.controls?.description?.value,
      lengthOfPeriod: this.callForm?.controls?.lengthOfPeriod?.value,
      priorityPolicies: this.priorityCheckboxes
        .flatMap(checkbox => checkbox.getCheckedChildPolicies()),
      strategies: this.buildUpdateEntityStrategies(),
      funds: this.buildUpdateEntityFunds(),
    };
    if (!this.call.id) {
      this.create.emit(call);
      return;
    }
    this.update.emit({
      ...call,
      id: this.call.id
    });
  }

  onCancel(): void {
    if (this.call?.id) {
      this.resetForm();
    }
    this.cancel.emit();
  }

  publishCall(): void {
    Forms.confirmDialog(
      this.dialog,
      'call.dialog.title',
      'call.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      this.publish.emit(this.call?.id);
    });
  }

  publishingRequirementsNotAchieved(): boolean {
    return (this.priorityCheckboxes
      && !this.priorityCheckboxes.some(priority => priority.checked || priority.someChecked())
      || !this.call.lengthOfPeriod
      || this.buildUpdateEntityFunds().length === 0);
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

  formChanged(): void {
    this.eventBusService.setDirty(CallDetailComponent.ID, true);
  }

  resetForm(): void {
    this.callForm.patchValue(this.call);
  }
}
