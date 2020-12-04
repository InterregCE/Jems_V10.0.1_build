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
import {InputProjectRelevanceSynergy} from '@cat/api';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {MultiLanguageInputService} from '../../../../../../../common/services/multi-language-input.service';

@Component({
  selector: 'app-synergy-table',
  templateUrl: './synergy-table.component.html',
  styleUrls: ['./synergy-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SynergyTableComponent implements OnInit, OnChanges {

  @Input()
  formGroup: FormGroup;
  @Input()
  synergies: InputProjectRelevanceSynergy[];
  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<MultiLanguageInput[]>();

  initiativeInputs: MultiLanguageInput[] = [];
  synergyInputs: MultiLanguageInput[] = [];
  allInputs: MultiLanguageInput[] = [];

  projectErrors = {
    maxlength: 'project.application.form.relevance.project.size.too.long',
  };
  synergyErrors = {
    maxlength: 'project.application.form.relevance.synergy.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              private languageService: MultiLanguageInputService) {
  }

  ngOnInit(): void {
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.synergies && this.editable) {
      this.resetForm();
    }
  }

  get synergiesForm(): FormArray {
    return this.formGroup.get('synergies') as FormArray;
  }

  addNewSynergy(): void {
    this.addControl();
    this.changed.emit();
  }

  private resetForm(): void {
    this.synergiesForm.clear();
    this.synergies.forEach(synergy => this.addControl(synergy));
  }

  private addControl(synergy?: InputProjectRelevanceSynergy): void {
    const initiativeControl = this.formBuilder.control('', [Validators.maxLength(2000)]);
    const initiativeInput = this.languageService.initInput(synergy?.specification || [], initiativeControl);
    this.initiativeInputs.push(initiativeInput);

    const synergyControl = this.formBuilder.control('', [Validators.maxLength(2000)]);
    const synergyInput = this.languageService.initInput(synergy?.synergy || [], synergyControl);
    this.synergyInputs.push(synergyInput);

    this.allInputs = [...this.initiativeInputs, ...this.synergyInputs];

    this.synergiesForm.push(this.formBuilder.group({
      initiative: initiativeControl,
      synergy: synergyControl,
      initiativeMultiInput: initiativeInput,
      synergyMultiInput: synergyInput
    }));
  }

  deleteEntry(elementIndex: number): void {
    this.synergiesForm.removeAt(elementIndex);
    this.initiativeInputs.splice(elementIndex, 1);
    this.synergyInputs.splice(elementIndex, 1);
    this.allInputs = [...this.initiativeInputs, ...this.synergyInputs];
    this.changed.emit();
  }
}
