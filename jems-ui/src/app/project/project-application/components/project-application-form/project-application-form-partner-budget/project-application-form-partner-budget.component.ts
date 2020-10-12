import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormGroup} from '@angular/forms';
import {FormState} from '@common/components/forms/form-state';
import {PartnerBudgetTable} from '../../../model/partner-budget-table';

@Component({
  selector: 'app-project-application-form-partner-budget',
  templateUrl: './project-application-form-partner-budget.component.html',
  styleUrls: ['./project-application-form-partner-budget.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerBudgetComponent extends ViewEditForm {
  @Input()
  budgets: { [key: string]: PartnerBudgetTable };

  @Output()
  save = new EventEmitter<{ [key: string]: PartnerBudgetTable }>();
  @Output()
  cancelEdit = new EventEmitter<void>();

  saveEnabled = true;

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit() {
    this.submitted = true;
    this.save.emit(this.budgets);
    this.changeFormState$.next(FormState.VIEW);
  }

  cancel(): void {
    this.changeFormState$.next(FormState.VIEW);
    this.cancelEdit.emit();
  }

  adaptValidity() {
    this.saveEnabled = Object.values(this.budgets).every(table => table.valid());
  }
}
