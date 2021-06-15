import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectCallSettingsDTO, ProjectPartnerBudgetDTO, ProjectService} from '@cat/api';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {combineLatest, Observable} from 'rxjs';
import {NumberService} from '../../../common/services/number.service';
import {ProjectVersionStore} from '@project/services/project-version-store.service';

@Component({
  selector: 'app-budget-page',
  templateUrl: './budget-page.component.html',
  styleUrls: ['./budget-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPageComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  displayedColumns: string[] = [
    'partner', 'country', 'staffCosts', 'officeAndAdministrationCosts', 'travelCosts',
    'externalCosts', 'equipmentCosts', 'infrastructureCosts', 'otherCosts', 'lumpSums', 'unitCosts', 'total'
  ];

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
    this.projectService.getProjectCallSettingsById(this.projectId),
  ])
    .pipe(
      tap(([, callSettings]) => this.hideEmptySimplifiedCostOptions(callSettings)),
      map(([budgets]) => budgets),
      tap(budgets => this.calculateFooterSums(budgets)),
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

  private hideEmptySimplifiedCostOptions(callSettings: ProjectCallSettingsDTO): void {
    if (callSettings.unitCosts?.length === 0) {
      const unitCostsColumnIndex = this.displayedColumns.indexOf('unitCosts');
      this.displayedColumns.splice(unitCostsColumnIndex, 1);
    }
    if (callSettings.lumpSums?.length === 0) {
      const lumpSumsColumnIndex = this.displayedColumns.indexOf('lumpSums');
      this.displayedColumns.splice(lumpSumsColumnIndex, 1);
    }
  }
}
