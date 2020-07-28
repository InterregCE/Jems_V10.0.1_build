import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatCheckbox, MatCheckboxChange} from '@angular/material/checkbox';
import {InputProgrammePriorityPolicy} from '@cat/api';

@Component({
  selector: 'app-programme-policy-checkbox-po1',
  templateUrl: './programme-policy-checkbox-po1.component.html',
  styleUrls: ['./programme-policy-checkbox-po1.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePolicyCheckboxPo1Component extends AbstractForm implements OnInit {
  @Input()
  policyObjectives: InputProgrammePriorityPolicy.ProgrammeObjectivePolicyEnum[];

  @Output()
  selectedValues: EventEmitter<InputProgrammePriorityPolicy[]> = new EventEmitter<InputProgrammePriorityPolicy[]>();

  po1Form = this.formBuilder.group({
    specificObjectiveCode1: ['', Validators.maxLength(50)],
    specificObjectiveCode2: ['', Validators.maxLength(50)],
    specificObjectiveCode3: ['', Validators.maxLength(50)],
    specificObjectiveCode4: ['', Validators.maxLength(50)],
    specificObjectiveCheckbox1: [''],
    specificObjectiveCheckbox2: [''],
    specificObjectiveCheckbox3: [''],
    specificObjectiveCheckbox4: ['']
  });

  specificObjectiveCodeErrors = {
    maxlength: 'programme.priority.specific.objective.code.size.too.long',
  };

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

  modifyListOfSelectedValues(event: MatCheckboxChange): void {
    if (event.checked) {
      this.currentSelectedValues.push(this.getSpecificPolicy(event.source));
    } else {
      this.currentSelectedValues =
        this.removeFromSelected(this.getSpecificPolicy(event.source));
    }
    this.selectedValues.emit(this.currentSelectedValues);
  }

  getSpecificPolicy(checkbox: MatCheckbox): InputProgrammePriorityPolicy {
    if (checkbox.name === 'specificObjectiveCheckbox1') {
      return {code: this.po1Form.controls.specificObjectiveCode1.value, programmeObjectivePolicy: this.policyObjectives[0]};
    }
    if (checkbox.name === 'specificObjectiveCheckbox2') {
      return {code: this.po1Form.controls.specificObjectiveCode2.value, programmeObjectivePolicy: this.policyObjectives[1]};
    }
    if (checkbox.name === 'specificObjectiveCheckbox3') {
      return {code: this.po1Form.controls.specificObjectiveCode3.value, programmeObjectivePolicy: this.policyObjectives[2]};
    }
    return {code: this.po1Form.controls.specificObjectiveCode4.value, programmeObjectivePolicy: this.policyObjectives[3]};
  }

  removeFromSelected(element: InputProgrammePriorityPolicy) {
    return this.currentSelectedValues.filter((value) => {
      return value.code !== element.code
        || value.programmeObjectivePolicy !== element.programmeObjectivePolicy;
    });
  }
}
