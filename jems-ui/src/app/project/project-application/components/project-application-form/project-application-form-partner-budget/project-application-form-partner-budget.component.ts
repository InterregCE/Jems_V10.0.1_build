import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnChanges, OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {PartnerBudgetTable} from '../../../model/partner-budget-table';
import {Numbers} from '../../../../../common/utils/numbers';
import {FormService} from '@common/components/section/form/form.service';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';
import {takeUntil, tap} from 'rxjs/operators';
import {BudgetOption} from '../../../model/budget-option';
import {InputCallFlatRateSetup} from '@cat/api';

@Component({
  selector: 'app-project-application-form-partner-budget',
  templateUrl: './project-application-form-partner-budget.component.html',
  styleUrls: ['./project-application-form-partner-budget.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerBudgetComponent extends BaseComponent implements OnInit, OnChanges {
  Number = Number;

  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  editable: boolean;
  @Input()
  budgetOptions: BudgetOption[];
  @Input()
  budgets: { [key: string]: PartnerBudgetTable };

  @Output()
  save = new EventEmitter<{ [key: string]: PartnerBudgetTable }>();
  @Output()
  cancelEdit = new EventEmitter<void>();

  saveEnabled = true;
  officeAndAdministrationTotal = 0;
  staffCostsTotal = 0;
  officeAdministrationFlatRate: number;
  officeAdministrationFlatRateActive: boolean;
  staffCostsFlatRate: number;
  staffCostsFlatRateActive: boolean;

  constructor(private formService: FormService) {
    super();
  }

  ngOnInit(): void {
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.partner.budget.save.success'))
      )
      .subscribe();
    this.prepareFlatRates();
  }

  ngOnChanges(changes: SimpleChanges): void {

    if (changes.budgets || changes.staffCostsFlatRate) {
      this.updateStaffCostsTotal();
      this.updateStateOfTables();
    }

    if (changes.budgets || changes.officeAdministrationFlatRate || changes.staffCostsFlatRate) {
      const staffTotal = Number.isInteger(this.staffCostsFlatRate) && Number.isInteger(this.officeAdministrationFlatRate) ?
        this.staffCostsTotal :
        (this.budgets?.staff?.total || 0);

      this.updateOfficeAndAdministrationTotal(staffTotal);
      this.updateStateOfTables();
    }
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
    this.save.emit(this.budgets);
  }

  cancel(): void {
    this.cancelEdit.emit();
  }

  tableChanged(): void {
    this.formService.setDirty(true);
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

  private prepareFlatRates(): void {
    this.budgetOptions.forEach(budgetOption => {
      if (budgetOption.key === InputCallFlatRateSetup.TypeEnum.StaffCost) {
        this.staffCostsFlatRate = budgetOption.value;
        this.staffCostsFlatRateActive = !budgetOption.isDefault;
      }
      if (budgetOption.key === InputCallFlatRateSetup.TypeEnum.OfficeOnStaff) {
        this.officeAdministrationFlatRate = budgetOption.value;
        this.officeAdministrationFlatRateActive = !budgetOption.isDefault;
      }
    });
  }

  private updateStateOfTables(): void {
    this.staffCostsFlatRateActive = false;
    this.officeAdministrationFlatRateActive = false;
    this.budgetOptions.forEach(budgetOption => {
      if (budgetOption.key === InputCallFlatRateSetup.TypeEnum.StaffCost) {
        this.staffCostsFlatRateActive = !budgetOption.isDefault;
      }
      if (budgetOption.key === InputCallFlatRateSetup.TypeEnum.OfficeOnStaff) {
        this.officeAdministrationFlatRateActive = !budgetOption.isDefault;
      }
    });
  }
}
