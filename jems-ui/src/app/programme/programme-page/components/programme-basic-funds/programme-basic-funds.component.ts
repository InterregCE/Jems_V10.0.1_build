import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {ProgrammeFundDTO} from '@cat/api';
import {FormState} from '@common/components/forms/form-state';

@Component({
  selector: 'app-programme-basic-funds',
  templateUrl: './programme-basic-funds.component.html',
  styleUrls: ['./programme-basic-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeBasicFundsComponent extends ViewEditForm implements OnInit {

  @Input()
  programmeFunds: ProgrammeFundDTO[];

  @Output()
  saveFunds = new EventEmitter<ProgrammeFundDTO[]>();

  editableFundsForm = new FormGroup({});

  constructor(
    private formBuilder: FormBuilder,
    protected changeDetectorRef: ChangeDetectorRef,
  ) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.editableFundsForm = this.formBuilder.group({
      funds: this.formBuilder.array([]),
    });
    this.resetForm();
  }

  get fundsForm(): FormArray {
    return this.editableFundsForm.get('funds') as FormArray;
  }

  getForm(): FormGroup | null {
    return this.editableFundsForm;
  }

  private resetForm(): void {
    this.fundsForm.clear();
    this.programmeFunds.forEach(fund => this.addControl(fund));
    this.changeFormState$.next(FormState.VIEW);
  }

  addNewFund(): void {
    this.addControl();
  }

  onSubmit(): void {
    this.saveFunds.emit(this.editableFundsForm.controls.funds.value);
  }

  private addControl(fund?: ProgrammeFundDTO): void {
    this.fundsForm.push(this.formBuilder.group({
      id: this.formBuilder.control(fund?.id || null),
      selected: this.formBuilder.control(fund?.selected || false),
      abbreviation: this.formBuilder.control(fund?.abbreviation || []),
      description: this.formBuilder.control(fund?.description || []),
    }));
  }

}
