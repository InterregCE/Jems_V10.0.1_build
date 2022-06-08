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
import {
  ChecklistConsolidatorOptionsStore
} from '@common/components/checklist/checklist-consolidator-options/checklist-consolidator-options-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder} from '@angular/forms';
import {Alert} from '@common/components/forms/alert';
import {catchError, tap} from 'rxjs/operators';
import {ChecklistInstanceDetailDTO} from '@cat/api';

@Component({
  selector: 'jems-checklist-consolidator-options',
  templateUrl: './checklist-consolidator-options.component.html',
  styleUrls: ['./checklist-consolidator-options.component.scss'],
  providers: [ChecklistConsolidatorOptionsStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChecklistConsolidatorOptionsComponent implements OnInit, OnChanges {

  Alert = Alert;

  @Input()
  checklistId: number;
  @Input()
  consolidated: boolean;
  @Input()
  checklistStatus: ChecklistInstanceDetailDTO.StatusEnum;

  @Output()
  consolidatedFlagChanged = new EventEmitter<boolean>();

  form = this.formBuilder.group({
    consolidated: []
  });

  constructor(private formService: FormService,
              private formBuilder: FormBuilder,
              private optionsStore: ChecklistConsolidatorOptionsStore) { }

  ngOnInit(): void {
    this.formService.init(this.form);
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.checklistStatus.currentValue) {
      this.disableFormBaseOnStatus(changes.checklistStatus.currentValue);
    }
  }

  resetForm() {
    this.form.patchValue({
      consolidated: this.consolidated
    });
  }

  save() {
    this.optionsStore.saveOptions(this.checklistId, this.form.value)
      .pipe(
        tap(isConsolidated => this.consolidatedFlagChanged.emit(isConsolidated)),
        tap(() => this.formService.setSuccess('checklists.instance.consolidator.options.saved.successfully')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  disableFormBaseOnStatus(status: ChecklistInstanceDetailDTO.StatusEnum) {
    // Only finished checklists can be set consolidated
    if (this.isChecklistFinished(status)) {
      this.form.disable();
    }else {
      this.form.enable();
    }
  }

  isChecklistFinished(status: ChecklistInstanceDetailDTO.StatusEnum): boolean {
    return status !== ChecklistInstanceDetailDTO.StatusEnum.FINISHED;
  }
}
