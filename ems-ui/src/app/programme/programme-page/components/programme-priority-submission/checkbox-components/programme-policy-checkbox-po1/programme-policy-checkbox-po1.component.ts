import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup} from '@angular/forms';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {InputProgrammePriorityPolicy} from '@cat/api';

@Component({
  selector: 'app-programme-policy-checkbox-po1',
  templateUrl: './programme-policy-checkbox-po1.component.html',
  styleUrls: ['./programme-policy-checkbox-po1.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePolicyCheckboxPo1Component extends AbstractForm implements OnInit {

  @Input()
  policyForm: FormGroup;

  specificObjectiveCodeErrors = {
    maxlength: 'programme.priority.specific.objective.code.size.too.long',
  };
  checked = new Map<string, boolean>();

  currentSelectedValues: InputProgrammePriorityPolicy[] = [];

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  getForm(): FormGroup | null {
    return this.po1Form;
  }

  modifyListOfSelectedValues(value: string, event: MatCheckboxChange): void {
    if (event.checked) {
      this.selectedValues.set(value, this.policyForm.controls[value].value);
      this.currentSelectedValues.push(this.getSpecificPolicy(event.source));
    }
    this.selectedValues.emit(this.currentSelectedValues);
  }
}
