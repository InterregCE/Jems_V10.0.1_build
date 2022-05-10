import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {
  ChecklistConsolidatorOptionsStore
} from '@common/components/checklist/checklist-consolidator-options/checklist-consolidator-options-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder} from '@angular/forms';
import {catchError, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-checklist-consolidator-options',
  templateUrl: './checklist-consolidator-options.component.html',
  styleUrls: ['./checklist-consolidator-options.component.scss'],
  providers: [ChecklistConsolidatorOptionsStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChecklistConsolidatorOptionsComponent implements OnInit {

  @Input()
  checklistId: number;
  @Input()
  consolidated: boolean;

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

  resetForm() {
    this.form.patchValue({
      consolidated: this.consolidated
    });
  }

  save() {
    this.optionsStore.saveOptions(this.checklistId, this.form.value)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instance.consolidator.options.saved.successfully')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }
}
