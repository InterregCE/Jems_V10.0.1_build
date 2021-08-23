import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectCallSettingsDTO, ProjectPartnerBudgetDTO, ProjectService} from '@cat/api';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {combineLatest, Observable} from 'rxjs';
import {NumberService} from '@common/services/number.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';

@Component({
  selector: 'app-budget-page',
  templateUrl: './budget-page.component.html',
  styleUrls: ['./budget-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPageComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  displayedColumns: string[];

  totalStaffCosts: number;
  totalOfficeAndAdministrationCosts: number;
  totalTravelCosts: number;
  totalExternalCosts: number;
  totalEquipmentCosts: number;
  totalInfrastructureCosts: number;
  totalOtherCosts: number;
  totalLumpSums: number;
  totalUnitCosts: number;
  total: number;

  budget$: Observable<ProjectPartnerBudgetDTO[]> = combineLatest([
    this.projectVersionStore.currentRouteVersion$
      .pipe(
        switchMap(version => this.projectService.getProjectBudget(this.projectId, version))
      ),
    this.projectStore.allowedBudgetCategories$,
    this.projectService.getProjectCallSettingsById(this.projectId),
  ])
    .pipe(
      tap(([, allowedBudgetCategories, callSettings]) => {
        this.displayedColumns = this.getDisplayedColumns(allowedBudgetCategories, callSettings);
      }),
      tap(([budgets]) => this.calculateFooterSums(budgets)),
      map(([budgets]) => budgets),
      tap(budgets => Log.info('Fetching the project budget', this, budgets)),
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectService: ProjectService,
              private projectVersionStore: ProjectVersionStore) {
  }

  private calculateFooterSums(budgets: ProjectPartnerBudgetDTO[]): void {
    this.totalStaffCosts = NumberService.sum(budgets.map(budget => budget.staffCosts));
    this.totalOfficeAndAdministrationCosts
      = NumberService.sum(budgets.map(budget => budget.officeAndAdministrationCosts));
    this.totalTravelCosts = NumberService.sum(budgets.map(budget => budget.travelCosts));
    this.totalExternalCosts = NumberService.sum(budgets.map(budget => budget.externalCosts));
    this.totalEquipmentCosts = NumberService.sum(budgets.map(budget => budget.equipmentCosts));
    this.totalInfrastructureCosts = NumberService.sum(budgets.map(budget => budget.infrastructureCosts));
    this.totalOtherCosts = NumberService.sum(budgets.map(budget => budget.otherCosts));
    this.totalLumpSums = NumberService.sum(budgets.map(budget => budget.lumpSumContribution));
    this.totalUnitCosts = NumberService.sum(budgets.map(budget => budget.unitCosts));
    this.total = NumberService.sum(budgets.map(budget => budget.totalSum));
  }

  private getDisplayedColumns(allowedBudgetCategories: AllowedBudgetCategories,
                              callSettings: ProjectCallSettingsDTO): string[] {
    const columns: string[] = ['partner', 'country'];
    if (allowedBudgetCategories.staff.realOrUnitCosts()) {
      columns.push('staffCosts');
    }
    columns.push('officeAndAdministrationCosts');
    if (allowedBudgetCategories.travel.realOrUnitCosts()) {
      columns.push('travelCosts');
    }
    if (allowedBudgetCategories.external.realOrUnitCosts()) {
      columns.push('externalCosts');
    }
    if (allowedBudgetCategories.equipment.realOrUnitCosts()) {
      columns.push('equipmentCosts');
    }
    if (allowedBudgetCategories.infrastructure.realOrUnitCosts()) {
      columns.push('infrastructureCosts');
    }
    columns.push('otherCosts');
    if (callSettings.lumpSums?.length) {
      columns.push('lumpSums');
    }
    if (callSettings.unitCosts?.length) {
      columns.push('unitCosts');
    }
    columns.push('total');
    return columns;
  }
}
