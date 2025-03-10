import {combineLatest, Subject} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Injectable} from '@angular/core';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM, ApplicationFormModel} from '@project/common/application-form-model';
import {BudgetPeriodDTO, ProjectPeriodDTO} from '@cat/api';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {
  ProjectPartnerBudgetConstants
} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/project-partner-budget.constants';
import {NumberService} from '@common/services/number.service';
import {RoutingService} from '@common/services/routing.service';
import {v4 as uuid} from 'uuid';

@UntilDestroy()
@Injectable()
export class ProjectPartnerBudgetTabService {

  private serviceId = uuid();
  private isBudgetOptionsFormInEditModeSubject = new Subject<boolean>();
  private isBudgetFormInEditModeSubject = new Subject<boolean>();
  private isSPFBudgetFormInEditModeSubject = new Subject<boolean>();
  isBudgetOptionsFormInEditMode$ = this.isBudgetOptionsFormInEditModeSubject.asObservable();
  isBudgetFormInEditMode$ = this.isBudgetFormInEditModeSubject.asObservable();
  isSPFBudgetFormInEditMode$ = this.isSPFBudgetFormInEditModeSubject.asObservable();

  constructor(private formVisibilityStatusService: FormVisibilityStatusService, private formBuilder: FormBuilder, private routingService: RoutingService) {
    combineLatest([this.isBudgetOptionsFormInEditMode$, this.isBudgetFormInEditMode$]).pipe(
      tap(([optionsDirty, budgetsDirty]) => {
        if (optionsDirty || budgetsDirty) {
          this.routingService.confirmLeaveSet.add(this.serviceId);
        }
      }),
      untilDestroyed(this)
    ).subscribe();
  }

  trackBudgetOptionsFormState(formService: FormService): void {
    formService.dirty$.pipe(
      tap(dirty => this.isBudgetOptionsFormInEditModeSubject.next(dirty)),
      untilDestroyed(this)
    ).subscribe();
  }

  trackBudgetFormState(formService: FormService): void {
    formService.dirty$.pipe(
      tap(dirty => this.isBudgetFormInEditModeSubject.next(dirty)),
      untilDestroyed(this)
    ).subscribe();
  }

  trackSPFBudgetFormState(formService: FormService): void {
    formService.dirty$.pipe(
      tap(dirty => this.isSPFBudgetFormInEditModeSubject.next(dirty)),
      untilDestroyed(this)
    ).subscribe();
  }

  setRowSum(rowSumControl: FormGroup): void {
    const numberOfUnits = rowSumControl.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.numberOfUnits)?.value || 0;
    const pricePerUnit = rowSumControl.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.pricePerUnit)?.value || 0;
    rowSumControl.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.rowSum)?.setValue(NumberService.truncateNumber(NumberService.product([numberOfUnits, pricePerUnit])), {emitEvent: false});
  }

  setTotal(items: FormArray, totalControl: FormControl): void {
    totalControl.setValue(NumberService.truncateNumber(
      items.controls.reduce((sum, control) =>
          NumberService.sum([control.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.rowSum)?.value || 0, sum])
        ,                   0)
    ));
  }

  addPeriods(items: FormArray, projectPeriods: ProjectPeriodDTO[], budgetPeriods?: BudgetPeriodDTO[]): void {
    if (!projectPeriods?.length  || this.arePeriodsHidden()) {
      return;
    }
    projectPeriods.forEach(projectPeriod => {
      const budgetPeriod = budgetPeriods?.find(period => period.number === projectPeriod.number);
      this.getPeriodsFormArray(items, items.length - 1).push(this.formBuilder.group({
        amount: this.formBuilder.control(
          budgetPeriod?.amount || 0,
          [Validators.max(ProjectPartnerBudgetConstants.MAX_VALUE), Validators.min(ProjectPartnerBudgetConstants.MIN_VALUE)]
        ),
        number: this.formBuilder.control(projectPeriod.number)
      }));
    });
  }

  shouldShowWarningForPeriods(projectPeriods: ProjectPeriodDTO[], items: FormArray): boolean {
    if (!projectPeriods?.length || this.arePeriodsHidden()) {
      return false;
    }
    return items.controls.some(
      control => control.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.openForPeriods)?.value !== 0
    );
  }

  setOpenForPeriods(projectPeriods: ProjectPeriodDTO[], control: FormGroup): void {
    if (!projectPeriods?.length || this.arePeriodsHidden()) {
      return;
    }
    let periodsSum = 0;
    (control.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.budgetPeriods) as FormArray).controls.forEach(period => {
      periodsSum = NumberService.sum([period.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.amount)?.value || 0, periodsSum]);
    });
    const rowSum = control.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.rowSum)?.value || 0;
    control.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.openForPeriods)?.setValue(
      NumberService.minus(rowSum, periodsSum), {emitEvent: false}
    );
  }

  getPeriodsWidthConfigs(projectPeriods: ProjectPeriodDTO[]): TableConfig[] {
    if (this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS)
      && projectPeriods?.length) {
      return [...projectPeriods?.map(() => ({minInRem: 8, maxInRem: 8})), {minInRem: 8, maxInRem: 8}];
    }
    return [];
  }

  getPeriodTableColumns(projectPeriods: ProjectPeriodDTO[]): string[] {
    if (this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS)
      && projectPeriods?.length) {
      return [...projectPeriods?.map(period => 'period' + period.number), 'openForPeriods'];
    }
    return [];
  }

  addIfItsVisible(field: ApplicationFormModel | string, result: any[]): any[] {
    return this.formVisibilityStatusService.isVisible(field) ? result : [];
  }

  getPeriodsFormArray(items: FormArray, rowIndex: number): FormArray {
    return items.at(rowIndex).get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.budgetPeriods) as FormArray;
  }

  private arePeriodsHidden(): boolean {
    return !this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS);
  }
}
