import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputCallCreate, InputCallUpdate, OutputCall} from '@cat/api'
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormState} from '@common/components/forms/form-state';
import {Forms} from '../../../common/utils/forms';
import {filter, take, takeUntil} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {CallPriorityCheckbox} from '../../containers/model/call-priority-checkbox';

@Component({
  selector: 'app-call-detail',
  templateUrl: './call-detail.component.html',
  styleUrls: ['./call-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallDetailComponent extends ViewEditForm implements OnInit {

  @Input()
  call: OutputCall
  @Input()
  priorityCheckboxes: CallPriorityCheckbox[];

  @Output()
  create: EventEmitter<InputCallCreate> = new EventEmitter<InputCallCreate>()
  @Output()
  update: EventEmitter<InputCallUpdate> = new EventEmitter<InputCallUpdate>()
  @Output()
  publish: EventEmitter<number> = new EventEmitter<number>()
  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>()

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
  published = false;

  callForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(250)
    ])],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    description: ['', Validators.maxLength(1000)]
  });

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef)
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.published = this.call?.status === OutputCall.StatusEnum.PUBLISHED;
    this.changeFormState$.next(this.call?.id ? FormState.VIEW : FormState.EDIT);
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
      lengthOfPeriod: 1,
      priorityPolicies: this.priorityCheckboxes
        .flatMap(checkbox => checkbox.getCheckedChildPolicies())
    }
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
      this.changeFormState$.next(FormState.VIEW);
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

  noPolicyChecked(): boolean {
    return this.priorityCheckboxes
      && !this.priorityCheckboxes.some(priority => priority.checked || priority.someChecked());
  }

  protected enterViewMode(): void {
    this.callForm.controls.name.setValue(this.call.name);
    this.callForm.controls.startDate.setValue(this.call.startDate);
    this.callForm.controls.endDate.setValue(this.call.endDate);
    this.callForm.controls.description.setValue(this.call.description);
  }
}
