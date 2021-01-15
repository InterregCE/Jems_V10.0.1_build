import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectPartnerBudgetDTO, ProjectService} from '@cat/api';
import {tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {Observable} from 'rxjs';
import {NumberService} from '../../../common/services/number.service';

@Component({
  selector: 'app-budget-page',
  templateUrl: './budget-page.component.html',
  styleUrls: ['./budget-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPageComponent implements OnInit {
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

  budget$: Observable<ProjectPartnerBudgetDTO[]> = this.projectService.getProjectBudget(this.projectId)
    .pipe(
      tap(budgets => this.calculateFooterSums(budgets)),
      tap(budgets => Log.info('Fetching the project budget', this, budgets))
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectService: ProjectService) {
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
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
}
