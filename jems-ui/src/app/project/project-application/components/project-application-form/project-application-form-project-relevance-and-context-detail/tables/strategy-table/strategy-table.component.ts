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
import {InputProjectRelevanceStrategy, CallDetailDTO} from '@cat/api';

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
  callStrategies: CallDetailDTO.StrategiesEnum[];
  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<void>();

  strategyEnum: string[] = [];

  strategyErrors = {
    required: 'project.application.form.relevance.strategy.not.empty',
  };

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.strategyEnum = this.callStrategies.map(strategy => InputProjectRelevanceStrategy.StrategyEnum[strategy]);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.strategies) {
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
    if (!this.editable) {
      this.strategiesForm.disable();
    }
  }

  private addControl(strategy?: InputProjectRelevanceStrategy): void {
    this.strategiesForm.push(this.formBuilder.group({
      strategy: this.formBuilder.control(strategy?.strategy || 'Other', []),
      contribution: this.formBuilder.control(strategy?.specification || [], [Validators.maxLength(2000)])
    }));
  }

  deleteEntry(elementIndex: number): void {
    this.strategiesForm.removeAt(elementIndex);
    this.changed.emit();
  }
}
