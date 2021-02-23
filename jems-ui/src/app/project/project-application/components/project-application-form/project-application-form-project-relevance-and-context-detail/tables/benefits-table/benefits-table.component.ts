import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProjectRelevanceBenefit} from '@cat/api';

@Component({
  selector: 'app-benefits-table',
  templateUrl: './benefits-table.component.html',
  styleUrls: ['./benefits-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BenefitsTableComponent implements OnInit, OnChanges {

  @Input()
  formGroup: FormGroup;
  @Input()
  benefits: InputProjectRelevanceBenefit[];

  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<void>();

  benefitEnums = Object.keys(InputProjectRelevanceBenefit.GroupEnum);

  targetGroupErrors = {
    required: 'project.application.form.relevance.target.group.not.empty',
  };

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.benefits && this.editable) {
      this.resetForm();
    }
  }

  get benefitsForm(): FormArray {
    return this.formGroup.get('benefits') as FormArray;
  }

  addNewBenefit(): void {
    this.addControl();
    this.changed.emit();
  }

  private resetForm(): void {
    this.benefitsForm.clear();
    this.benefits.forEach(benefit => this.addControl(benefit));
  }

  private addControl(benefit?: InputProjectRelevanceBenefit): void {
    this.benefitsForm.push(this.formBuilder.group({
      targetGroup: this.formBuilder.control(
        benefit ? benefit.group : InputProjectRelevanceBenefit.GroupEnum.Other, [Validators.required]
      ),
      specification: this.formBuilder.control(benefit?.specification || [], [Validators.maxLength(2000)]),
    }));
  }

  deleteEntry(elementIndex: number): void {
    this.benefitsForm.removeAt(elementIndex);
    this.changed.emit();
  }
}
