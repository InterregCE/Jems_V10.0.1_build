import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {ProjectPeriodDTO} from '@cat/api';
import {ProjectPartnerBudgetConstants} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/project-partner-budget.constants';
import {ProjectPartnerBudgetTabService} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.service';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {PartnerBudgetSpfTables} from '@project/model/budget/partner-budget-spf-tables';
import {SpfPartnerBudgetTable} from '@project/model/budget/spf-partner-budget-table';
import {SpfPartnerBudgetTableEntry} from '@project/model/budget/spf-partner-budget-table-entry';

@UntilDestroy()
@Component({
  selector: 'jems-small-project-fund-budget',
  templateUrl: './small-project-fund-budget.component.html',
  styleUrls: ['./small-project-fund-budget.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SmallProjectFundBudgetComponent implements OnInit {

  constants = ProjectPartnerBudgetConstants;
  spfBudgetsForm = this.initForm();
  data: {
    spfBudgetTables: PartnerBudgetSpfTables;
    periods: ProjectPeriodDTO[];
  };

  constructor(private cdr: ChangeDetectorRef, private formService: FormService, private tabService: ProjectPartnerBudgetTabService, private formBuilder: FormBuilder, private pageStore: ProjectPartnerDetailPageStore) {
  }

  get spfCost(): FormGroup {
    return this.spfBudgetsForm.get(this.constants.SPF_FORM_CONTROL_NAMES.spf) as FormGroup;
  }

  ngOnInit(): void {
    this.formService.init(this.spfBudgetsForm, combineLatest([this.pageStore.isProjectEditable$, this.tabService.isBudgetOptionsFormInEditMode$.pipe(startWith(false))]).pipe(map(([isProjectEditable, isBudgetOptionsFormInEditMode]) => isProjectEditable && !isBudgetOptionsFormInEditMode)));
    this.tabService.trackBudgetFormState(this.formService);

    this.pageStore.spfBudgets$.pipe(untilDestroyed(this)).subscribe();

    combineLatest([
      this.pageStore.spfBudgets$,
      this.pageStore.periods$,
    ]).pipe(
      map(([spfBudgetTables, periods]: any) => {
        return {
          spfBudgetTables,
          periods
        };
      }),
      untilDestroyed(this)
    ).subscribe(data => {
      this.data = data;
      setTimeout(() => {
        this.cdr.markForCheck();
      });
    });
  }

  updateBudgets(): void {
    this.pageStore.updateSpfBudgets(this.formToSpfBudgetTables()).pipe(
      tap(() => this.formService.setSuccess('project.partner.budget.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private formToSpfBudgetTables(): PartnerBudgetSpfTables {
    return new PartnerBudgetSpfTables(
      new SpfPartnerBudgetTable(this.getTotalOf(this.spfCost), this.spfCost?.value.items.map((item: any) => new SpfPartnerBudgetTableEntry({...item})))
    );
  }

  private initForm(): FormGroup {
    return this.formBuilder.group({
      spf: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      }),
    });
  }

  private getTotalOf(formGroup: FormGroup): number {
    return formGroup.get(this.constants.SPF_FORM_CONTROL_NAMES.total)?.value || 0;
  }
}
