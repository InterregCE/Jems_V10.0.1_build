import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {InputBudget, ProjectPartnerBudgetService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {Log} from '../../../../../../common/utils/log';
import {catchError, flatMap, map, startWith, tap} from 'rxjs/operators';
import {combineLatest, forkJoin, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {PartnerBudgetTable} from '../../../../model/partner-budget-table';
import {PartnerBudgetTableType} from '../../../../model/partner-budget-table-type';

@Component({
  selector: 'app-project-application-partner-budget-page',
  templateUrl: './project-application-partner-budget-page.component.html',
  styleUrls: ['./project-application-partner-budget-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerBudgetPageComponent implements OnInit {

  @Input()
  partnerId: number;
  @Input()
  editable: boolean

  projectId = this.activatedRoute?.snapshot?.params?.projectId
  partnerInitialized$ = new ReplaySubject<number>();
  saveBudgets$ = new Subject<{ [key: string]: PartnerBudgetTable }>();

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  cancelEdit$ = new Subject<void>();

  private initialBudgets$ = this.partnerInitialized$
    .pipe(
      flatMap(id =>
        forkJoin({
          staff: this.projectPartnerBudgetService.getBudgetStaffCost(id, this.projectId)
            .pipe(tap(staff => Log.info('Fetched the staff budget', this, staff))),
          travel: this.projectPartnerBudgetService.getBudgetTravel(id, this.projectId)
            .pipe(tap(travel => Log.info('Fetched the travel budget', this, travel))),
          external: this.projectPartnerBudgetService.getBudgetExternal(id, this.projectId)
            .pipe(tap(external => Log.info('Fetched the external budget', this, external))),
          equipment: this.projectPartnerBudgetService.getBudgetEquipment(id, this.projectId)
            .pipe(tap(equipment => Log.info('Fetched the equipment budget', this, equipment))),
          infrastructure: this.projectPartnerBudgetService.getBudgetInfrastructure(id, this.projectId)
            .pipe(tap(infrastructure => Log.info('Fetched the infrastructure budget', this, infrastructure))),
        })
      )
    );

  private savedBudgets$ = this.saveBudgets$
    .pipe(
      flatMap(budgets =>
        forkJoin({
          staff: this.updateBudget(PartnerBudgetTableType.STAFF, this.getBudgetEntries(budgets.staff)),
          travel: this.updateBudget(PartnerBudgetTableType.TRAVEL, this.getBudgetEntries(budgets.travel)),
          external: this.updateBudget(PartnerBudgetTableType.EXTERNAL, this.getBudgetEntries(budgets.external)),
          equipment: this.updateBudget(PartnerBudgetTableType.EQUIPMENT, this.getBudgetEntries(budgets.equipment)),
          infrastructure: this.updateBudget(PartnerBudgetTableType.INFRASTRUCTURE, this.getBudgetEntries(budgets.infrastructure))
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
        staff: new PartnerBudgetTable(PartnerBudgetTableType.STAFF, budgets.staff),
        travel: new PartnerBudgetTable(PartnerBudgetTableType.TRAVEL, budgets.travel),
        external: new PartnerBudgetTable(PartnerBudgetTableType.EXTERNAL, budgets.external),
        equipment: new PartnerBudgetTable(PartnerBudgetTableType.EQUIPMENT, budgets.equipment),
        infrastructure: new PartnerBudgetTable(PartnerBudgetTableType.INFRASTRUCTURE, budgets.infrastructure)
      }))
    );

  constructor(private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    if (this.partnerId) {
      this.partnerInitialized$.next(this.partnerId);
    }
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
    if (type === PartnerBudgetTableType.STAFF)
      update$ = this.projectPartnerBudgetService.updateBudgetStaffCost(this.partnerId, this.projectId, entries);
    if (type === PartnerBudgetTableType.TRAVEL)
      update$ = this.projectPartnerBudgetService.updateBudgetTravel(this.partnerId, this.projectId, entries);
    if (type === PartnerBudgetTableType.TRAVEL)
      update$ = this.projectPartnerBudgetService.updateBudgetExternal(this.partnerId, this.projectId, entries);
    if (type === PartnerBudgetTableType.EQUIPMENT)
      update$ = this.projectPartnerBudgetService.updateBudgetEquipment(this.partnerId, this.projectId, entries);
    if (type === PartnerBudgetTableType.INFRASTRUCTURE)
      update$ = this.projectPartnerBudgetService.updateBudgetInfrastructure(this.partnerId, this.projectId, entries);

    return !update$ ? of([]) : update$
      .pipe(
        tap(budget => Log.info('Updated the' + type + ' budget', this, budget))
      )
  }

}
