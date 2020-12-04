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
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {MultiLanguageInputService} from '../../../../../../../common/services/multi-language-input.service';

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
  changed = new EventEmitter<MultiLanguageInput[]>();

  specificationInputs: MultiLanguageInput[] = [];
  benefitEnums = Object.keys(InputProjectRelevanceBenefit.GroupEnum);

  targetGroupErrors = {
    required: 'project.application.form.relevance.target.group.not.empty',
  };
  specificationErrors = {
    maxlength: 'project.application.form.relevance.specification.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              private languageService: MultiLanguageInputService) {
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
    const specificationControl = this.formBuilder.control('', [Validators.maxLength(2000)]);
    const specificationInput = this.languageService.initInput(benefit?.specification || [], specificationControl);
    this.specificationInputs = [...this.specificationInputs, specificationInput];

    this.benefitsForm.push(this.formBuilder.group({
      targetGroup: this.formBuilder.control(
        benefit ? benefit.group : InputProjectRelevanceBenefit.GroupEnum.Other, [Validators.required]
      ),
      specification: specificationControl,
      specificationMultiInput: specificationInput
    }));
  }

  deleteEntry(elementIndex: number): void {
    this.benefitsForm.removeAt(elementIndex);
    this.specificationInputs = this.specificationInputs.filter((element, index) => index !== elementIndex);
    this.changed.emit();
  }
}
