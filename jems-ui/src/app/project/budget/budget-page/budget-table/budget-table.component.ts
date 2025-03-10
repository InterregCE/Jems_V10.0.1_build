import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ProjectStore} from '../../../project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectCallSettingsDTO, ProjectPartnerBudgetDTO, ProjectService} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {NumberService} from '@common/services/number.service';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import { ProjectPartnerDetailPageStore } from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import { ProgrammeUnitCost } from '@project/model/programmeUnitCost';

@Component({
  selector: 'jems-budget-table',
  templateUrl: './budget-table.component.html',
  styleUrls: ['./budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetTableComponent implements OnInit, OnChanges {

  @Input()
  projectId: number;

  @Input()
  dataSource: ProjectPartnerBudgetDTO[];

  @Input()
  hideFooter = false;

  @Input()
  hideCountry = false;

  @Input()
  headerLinks: boolean;

  @Input()
  hidePartnerLink = false;

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
  totalSpfCosts: number;
  total: number;
  tableConfig: TableConfig[];

  budget$: Observable<ProjectPartnerBudgetDTO[]>;
  dataSourceChanged$: BehaviorSubject<ProjectPartnerBudgetDTO[]>;

  constructor(
    public projectStore: ProjectStore,
    private activatedRoute: ActivatedRoute,
    private projectService: ProjectService,
    private projectPartnerDetailPageStore: ProjectPartnerDetailPageStore
  ) {
    this.dataSourceChanged$ = new BehaviorSubject<ProjectPartnerBudgetDTO[]>(this.dataSource);
  }

  ngOnInit(): void {
    this.budget$ = combineLatest([
      this.dataSourceChanged$,
      this.projectStore.allowedBudgetCategories$,
      this.projectService.getProjectCallSettingsById(this.projectId),
      this.projectPartnerDetailPageStore.unitCosts$
    ])
      .pipe(
        tap(([, allowedBudgetCategories, callSettings, unitCosts]) => {
          this.displayedColumns = this.getDisplayedColumns(allowedBudgetCategories, callSettings, unitCosts);
        }),
        tap(() => this.calculateFooterSums(this.dataSource || [])),
        map(() => this.dataSource),
      );
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.dataSource) {
      this.dataSourceChanged$.next(this.dataSource);
    }
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
    this.totalSpfCosts = NumberService.sum(budgets.map(budget => budget.spfCosts));
    this.total = NumberService.sum(budgets.map(budget => budget.totalSum));
  }

  private getDisplayedColumns(allowedBudgetCategories: AllowedBudgetCategories,
                              callSettings: ProjectCallSettingsDTO,
                              unitCosts: ProgrammeUnitCost[]): string[] {
    const columns: string[] = ['partner'];
    this.tableConfig = [{minInRem: 4, maxInRem: 4}];

    columns.push('partnerAbbreviation');
    this.tableConfig.push({minInRem: 7, maxInRem: 10});

    if (!this.hideCountry) {
      columns.push('country');
      this.tableConfig.push({minInRem: 6, maxInRem: 12});
    }
    if (allowedBudgetCategories.staff.realOrUnitCosts() || callSettings.flatRates?.staffCostFlatRateSetup) {
      columns.push('staffCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (callSettings.flatRates?.officeAndAdministrationOnDirectCostsFlatRateSetup || callSettings.flatRates?.officeAndAdministrationOnStaffCostsFlatRateSetup) {
      columns.push('officeAndAdministrationCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (allowedBudgetCategories.travel.realOrUnitCosts() || callSettings.flatRates?.travelAndAccommodationOnStaffCostsFlatRateSetup) {
      columns.push('travelCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (allowedBudgetCategories.external.realOrUnitCosts()) {
      columns.push('externalCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (allowedBudgetCategories.equipment.realOrUnitCosts()) {
      columns.push('equipmentCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (allowedBudgetCategories.infrastructure.realOrUnitCosts()) {
      columns.push('infrastructureCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (callSettings.flatRates?.otherCostsOnStaffCostsFlatRateSetup) {
      columns.push('otherCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (callSettings.lumpSums?.length) {
      columns.push('lumpSums');
      this.tableConfig.push({minInRem: 7});
    }
    if (unitCosts?.find(cost => !cost.isOneCostCategory)) {
      columns.push('unitCosts');
      this.tableConfig.push({minInRem: 7});
    }
    if (callSettings.callType === ProjectCallSettingsDTO.CallTypeEnum.SPF) {
      columns.push('totalWithoutSpf');
      columns.push('spfCosts');
      this.tableConfig.push({minInRem: 7});
      this.tableConfig.push({minInRem: 7});
    }
    columns.push('total');
    this.tableConfig.push({minInRem: 7});
    return columns;
  }

  scrollTo(elementId: string) {
    document.getElementById(elementId)?.scrollIntoView({behavior:'smooth', block:'center'});
  }
}
