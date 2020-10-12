import {ChangeDetectionStrategy, Component} from '@angular/core';
import {InputBudget, ProjectPartnerBudgetService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {Log} from '../../../../../../common/utils/log';
import {catchError, flatMap, map, startWith, tap} from 'rxjs/operators';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {PartnerBudgetTable} from '../../../../model/partner-budget-table';

@Component({
  selector: 'app-project-application-partner-budget-page',
  templateUrl: './project-application-partner-budget-page.component.html',
  styleUrls: ['./project-application-partner-budget-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerBudgetPageComponent {

  projectId = this.activatedRoute?.snapshot?.params?.projectId
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  cancelEdit$ = new Subject<void>();

  saveBudgets$ = new Subject<{ [key: string]: PartnerBudgetTable }>();

  private initialBudgets$ =
    forkJoin({
      staff: this.projectPartnerBudgetService.getBudgetStaffCost(this.partnerId, this.projectId)
        .pipe(tap(staff => Log.info('Fetched the staff budget', this, staff))),
      travel: this.projectPartnerBudgetService.getBudgetTravel(this.partnerId, this.projectId)
        .pipe(tap(travel => Log.info('Fetched the travel budget', this, travel))),
      external: this.projectPartnerBudgetService.getBudgetExternal(this.partnerId, this.projectId)
        .pipe(tap(external => Log.info('Fetched the external budget', this, external))),
      equipment: this.projectPartnerBudgetService.getBudgetEquipment(this.partnerId, this.projectId)
        .pipe(tap(equipment => Log.info('Fetched the equipment budget', this, equipment))),
      infrastructure: this.projectPartnerBudgetService.getBudgetInfrastructure(this.partnerId, this.projectId)
        .pipe(tap(infrastructure => Log.info('Fetched the infrastructure budget', this, infrastructure))),
    });

  private savedBudgets$ = this.saveBudgets$
    .pipe(
      flatMap(budgets =>
        forkJoin({
          staff: this.updateBudget('staff', this.getBudgetEntries(budgets.staff)),
          travel: this.updateBudget('travel', this.getBudgetEntries(budgets.travel)),
          external: this.updateBudget('external', this.getBudgetEntries(budgets.external)),
          equipment: this.updateBudget('equipment', this.getBudgetEntries(budgets.equipment)),
          infrastructure: this.updateBudget('infrastructure', this.getBudgetEntries(budgets.infrastructure))
        })),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.cancelEdit$.next();
        this.saveError$.next(error.error);
        throw error;
      })
    );

  budgets$ = combineLatest([
    merge(this.initialBudgets$, this.savedBudgets$),
    this.cancelEdit$.pipe(startWith(null))
  ])
    .pipe(
      map(([budgets]) => ({
        staff: new PartnerBudgetTable(budgets.staff),
        travel: new PartnerBudgetTable(budgets.travel),
        external: new PartnerBudgetTable(budgets.external),
        equipment: new PartnerBudgetTable(budgets.equipment),
        infrastructure: new PartnerBudgetTable(budgets.infrastructure)
      }))
    );

  constructor(private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private activatedRoute: ActivatedRoute) {
  }

  private getBudgetEntries(table: PartnerBudgetTable): InputBudget[] {
    return table.entries.map(entry => ({
      id: (entry.new ? null : entry.id) as any,
      description: entry.description as any,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any
    }));
  }

  private updateBudget(type: string, entries: InputBudget[]): Observable<Array<InputBudget>> {
    let update$;
    if (type === 'staff')
      update$ = this.projectPartnerBudgetService.updateBudgetStaffCost(this.partnerId, this.projectId, entries);
    if (type === 'travel')
      update$ = this.projectPartnerBudgetService.updateBudgetTravel(this.partnerId, this.projectId, entries);
    if (type === 'external')
      update$ = this.projectPartnerBudgetService.updateBudgetExternal(this.partnerId, this.projectId, entries);
    if (type === 'equipment')
      update$ = this.projectPartnerBudgetService.updateBudgetEquipment(this.partnerId, this.projectId, entries);
    if (type === 'infrastructure')
      update$ = this.projectPartnerBudgetService.updateBudgetInfrastructure(this.partnerId, this.projectId, entries);

    return !update$ ? of([]) : update$
      .pipe(
        tap(budget => Log.info('Updated the' + type + ' budget', this, budget))
      )
  }

}
