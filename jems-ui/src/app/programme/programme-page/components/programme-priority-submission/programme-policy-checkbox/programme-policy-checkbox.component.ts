import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-programme-policy-checkbox',
  templateUrl: './programme-policy-checkbox.component.html',
  styleUrls: ['./programme-policy-checkbox.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePolicyCheckboxComponent extends AbstractForm implements OnInit {

  @Input()
  policyForm: FormGroup;
  @Input()
  checked: Map<string, boolean>;

  specificObjectiveCodeErrors = {
    maxlength: 'programme.priority.specific.objective.code.size.too.long',
    required: 'programme.priority.specific.objective.code.should.not.be.empty'
  };

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return this.policyForm;
  }

  originalOrder = (): number => 0;

  setCheckedStatus(key: string, value: boolean): void {
    this.checked.set(key, value);
    if (value) {
      this.policyForm.controls[key].setValidators(Validators.compose([Validators.required, Validators.maxLength(50)]));
      this.policyForm.updateValueAndValidity();
      this.policyForm.controls[key].setValue(this.policyForm.controls[key].value ? this.policyForm.controls[key].value : null);
      this.policyForm.controls[key].markAsTouched();
      this.changeDetectorRef.markForCheck();
    } else {
      this.policyForm.controls[key].setValidators(Validators.compose([Validators.maxLength(50)]));
      this.policyForm.controls[key].setValue(this.policyForm.controls[key].value ? this.policyForm.controls[key].value : null);
      this.policyForm.updateValueAndValidity();
      this.changeDetectorRef.markForCheck();
    }
  }
}
