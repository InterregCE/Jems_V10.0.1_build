import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormGroup} from '@angular/forms';

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
  };

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  getForm(): FormGroup | null {
    return this.policyForm;
  }
}
