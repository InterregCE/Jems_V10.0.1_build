import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectPartnerBudgetDTO, ProjectService} from '@cat/api';
import {tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {Observable} from 'rxjs';
import {Numbers} from '../../../common/utils/numbers';

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
    'externalCosts', 'equipmentCosts', 'infrastructureCosts', 'total'
  ];

  totalStaffCosts: number;
  totalOfficeAndAdministrationCosts: number;
  totalTravelCosts: number;
  totalExternalCosts: number;
  totalEquipmentCosts: number;
  totalInfrastructureCosts: number;
  total: number;

  budget$: Observable<ProjectPartnerBudgetDTO[]> = this.projectService.getProjectBudget(this.projectId)
    .pipe(
      tap(budgets => this.totalStaffCosts = Numbers.sum(budgets.map(budget => budget.staffCosts))),
      tap(budgets => this.totalOfficeAndAdministrationCosts
        = Numbers.sum(budgets.map(budget => budget.officeAndAdministrationCosts))),
      tap(budgets => this.totalTravelCosts = Numbers.sum(budgets.map(budget => budget.travelCosts))),
      tap(budgets => this.totalExternalCosts = Numbers.sum(budgets.map(budget => budget.externalCosts))),
      tap(budgets => this.totalEquipmentCosts = Numbers.sum(budgets.map(budget => budget.equipmentCosts))),
      tap(budgets => this.totalInfrastructureCosts = Numbers.sum(budgets.map(budget => budget.infrastructureCosts))),
      tap(budgets => this.total = Numbers.sum(budgets.map(budget => budget.totalSum))),
      tap(budget => Log.info('Fetching the project budget', this, budget))
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectService: ProjectService) {
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
  }
}
