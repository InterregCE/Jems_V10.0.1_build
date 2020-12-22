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
    'externalCosts', 'equipmentCosts', 'infrastructureCosts', 'otherCosts', 'total'
  ];

  totalStaffCosts: number;
  totalOfficeAndAdministrationCosts: number;
  totalTravelCosts: number;
  totalExternalCosts: number;
  totalEquipmentCosts: number;
  totalInfrastructureCosts: number;
  totalOtherCosts: number;
  total: number;

  budget$: Observable<ProjectPartnerBudgetDTO[]> = this.projectService.getProjectBudget(this.projectId)
    .pipe(
      tap(budgets => this.totalStaffCosts = NumberService.sum(budgets.map(budget => budget.staffCosts))),
      tap(budgets => this.totalOfficeAndAdministrationCosts
        = NumberService.sum(budgets.map(budget => budget.officeAndAdministrationCosts))),
      tap(budgets => this.totalTravelCosts = NumberService.sum(budgets.map(budget => budget.travelCosts))),
      tap(budgets => this.totalExternalCosts = NumberService.sum(budgets.map(budget => budget.externalCosts))),
      tap(budgets => this.totalEquipmentCosts = NumberService.sum(budgets.map(budget => budget.equipmentCosts))),
      tap(budgets => this.totalInfrastructureCosts = NumberService.sum(budgets.map(budget => budget.infrastructureCosts))),
      tap(budgets => this.totalOtherCosts = NumberService.sum(budgets.map(budget => budget.otherCosts))),
      tap(budgets => this.total = NumberService.sum(budgets.map(budget => budget.totalSum))),
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
