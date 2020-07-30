import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputCallCreate, InputCallUpdate, OutputCall} from '@cat/api'

@Component({
  selector: 'app-call-detail',
  templateUrl: './call-detail.component.html',
  styleUrls: ['./call-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallDetailComponent extends AbstractForm implements OnInit {

  @Input()
  call: OutputCall

  @Output()
  create: EventEmitter<InputCallCreate> = new EventEmitter<InputCallCreate>()
  @Output()
  update: EventEmitter<InputCallUpdate> = new EventEmitter<InputCallUpdate>()
  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef)
  }

  startDateErrors = {
    required: 'call.startDate.unknown',
  };
  endDateErrors = {
    required: 'call.endDate.unknown',
  };
  nameErrors = {
    required: 'call.name.unknown',
    maxLength: 'call.name.wrong.size'
  };
  descriptionErrors = {
    maxLength: 'call.description.wrong.size'
  }

  callForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(250)
    ])],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    description: ['', Validators.maxLength(1000)]
  });

  ngOnInit(): void {
    super.ngOnInit()
    if (!this.call) {
      return;
    }
    this.callForm.controls.name.setValue(this.call.name);
    this.callForm.controls.startDate.setValue(this.call.startDate);
    this.callForm.controls.endDate.setValue(this.call.endDate);
    this.callForm.controls.description.setValue(this.call.description);
  }

  getForm(): FormGroup | null {
    return this.callForm;
  }

  onSubmit() {
    const call = {
      name: this.callForm?.controls?.name?.value,
      startDate: this.callForm?.controls?.startDate?.value,
      endDate: this.callForm?.controls?.endDate?.value,
      description: this.callForm?.controls?.description?.value
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
}
