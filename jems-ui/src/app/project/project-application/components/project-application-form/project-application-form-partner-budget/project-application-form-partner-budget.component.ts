import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnChanges,
  Output, SimpleChanges
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormGroup} from '@angular/forms';
import {FormState} from '@common/components/forms/form-state';
import {PartnerBudgetTable} from '../../../model/partner-budget-table';
import {Numbers} from '../../../../../common/utils/numbers';

@Component({
  selector: 'app-project-application-form-partner-budget',
  templateUrl: './project-application-form-partner-budget.component.html',
  styleUrls: ['./project-application-form-partner-budget.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerBudgetComponent extends ViewEditForm implements OnChanges {
  Number = Number;

  @Input()
  editable: boolean;
  @Input()
  officeAdministrationFlatRate: number;
  @Input()
  staffCostsFlatRate: number;
  @Input()
  budgets: { [key: string]: PartnerBudgetTable };

  @Output()
  save = new EventEmitter<{ [key: string]: PartnerBudgetTable }>();
  @Output()
  cancelEdit = new EventEmitter<void>();

  saveEnabled = true;
  officeAndAdministrationTotal = 0;
  staffCostsTotal = 0;

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnChanges(changes: SimpleChanges) {

    if (changes.budgets || changes.staffCostsFlatRate) {
      this.updateStaffCostsTotal()
    }

    if (changes.budgets || changes.officeAdministrationFlatRate || changes.staffCostsFlatRate) {
      const staffTotal = Number.isInteger(this.staffCostsFlatRate) && Number.isInteger(this.officeAdministrationFlatRate) ?
        this.staffCostsTotal :
        (this.budgets?.staff?.total || 0)

      this.updateOfficeAndAdministrationTotal(staffTotal)
    }
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

  private updateStaffCostsTotal(): void {
    const travelTotal = this.budgets.travel?.total || 0;
    const externalTotal = this.budgets.external?.total || 0;
    const equipmentTotal = this.budgets.equipment?.total || 0;
    const infrastructureTotal = this.budgets.infrastructure?.total || 0;
    this.staffCostsTotal = Numbers.truncateNumber(Numbers.product([
      Numbers.divide(this.staffCostsFlatRate, 100),
      Numbers.sum([travelTotal, externalTotal, equipmentTotal, infrastructureTotal])
    ]));
  }

  private updateOfficeAndAdministrationTotal(staffTotal: number): void {
    this.officeAndAdministrationTotal = Numbers.truncateNumber(Numbers.product([
      Numbers.divide(this.officeAdministrationFlatRate, 100),
      staffTotal
    ]));
  }
}
