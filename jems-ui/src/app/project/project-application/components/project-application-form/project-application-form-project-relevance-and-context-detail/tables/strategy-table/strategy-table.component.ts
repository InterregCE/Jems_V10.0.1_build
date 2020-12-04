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
import {InputProjectRelevanceStrategy, OutputCall} from '@cat/api';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {MultiLanguageInputService} from '../../../../../../../common/services/multi-language-input.service';

@Component({
  selector: 'app-strategy-table',
  templateUrl: './strategy-table.component.html',
  styleUrls: ['./strategy-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StrategyTableComponent implements OnInit, OnChanges {
  @Input()
  formGroup: FormGroup;
  @Input()
  strategies: InputProjectRelevanceStrategy[];
  @Input()
  callStrategies: OutputCall.StrategiesEnum[];
  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<MultiLanguageInput[]>();

  strategyEnum: string[] = [];
  contributionInputs: MultiLanguageInput[] = [];

  strategyErrors = {
    required: 'project.application.form.relevance.strategy.not.empty',
  };
  contributionErrors = {
    maxlength: 'project.application.form.relevance.contribution.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              private languageService: MultiLanguageInputService) {
  }

  ngOnInit(): void {
    this.resetForm();
    this.strategyEnum = this.callStrategies.map(strategy => InputProjectRelevanceStrategy.StrategyEnum[strategy]);
    this.strategyEnum.push('Other');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.strategies && this.editable) {
      this.resetForm();
    }
  }

  get strategiesForm(): FormArray {
    return this.formGroup.get('strategies') as FormArray;
  }

  addNewStrategy(): void {
    this.addControl();
    this.changed.emit();
  }

  private resetForm(): void {
    this.strategiesForm.clear();
    this.strategies.forEach(strategy => this.addControl(strategy));
  }

  private addControl(strategy?: InputProjectRelevanceStrategy): void {
    const contributionControl = this.formBuilder.control('', [Validators.maxLength(2000)]);
    const contributionInput = this.languageService.initInput(strategy?.specification || [], contributionControl);
    this.contributionInputs = [...this.contributionInputs, contributionInput];

    this.strategiesForm.push(this.formBuilder.group({
      strategy: this.formBuilder.control(strategy?.strategy || 'Other', []),
      contribution: contributionControl,
      contributionMultiInput: contributionInput
    }));
  }

  deleteEntry(elementIndex: number): void {
    this.strategiesForm.removeAt(elementIndex);
    this.contributionInputs = this.contributionInputs.filter((element, index) => index !== elementIndex);
    this.changed.emit();
  }
}
