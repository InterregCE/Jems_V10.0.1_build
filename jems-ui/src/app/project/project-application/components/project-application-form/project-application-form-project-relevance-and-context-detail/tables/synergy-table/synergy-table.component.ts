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
import {FormService} from '@common/components/section/form/form.service';

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
  changed = new EventEmitter<void>();

  constructor(private formBuilder: FormBuilder,
              private formService: FormService) {
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
    this.formService.resetEditable();
  }

  private addControl(synergy?: InputProjectRelevanceSynergy): void {
    this.synergiesForm.push(this.formBuilder.group({
      initiative: this.formBuilder.control(synergy?.specification || [], [Validators.maxLength(2000)]),
      synergy: this.formBuilder.control(synergy?.synergy || [], [Validators.maxLength(2000)])
    }));
  }

  deleteEntry(elementIndex: number): void {
    this.synergiesForm.removeAt(elementIndex);
    this.changed.emit();
  }
}
