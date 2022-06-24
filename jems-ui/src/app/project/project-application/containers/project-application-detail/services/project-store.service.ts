import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  CallService,
  InputProjectData,
  InvestmentSummaryDTO,
  ProjectBudgetService,
  ProjectCallSettingsDTO,
  ProjectDecisionDTO,
  ProjectDetailDTO,
  ProjectDetailFormDTO,
  ProjectPartnerBudgetPerFundDTO,
  ProjectPartnerUserCollaboratorService,
  ProjectPeriodDTO,
  ProjectService,
  ProjectStatusDTO,
  ProjectUserCollaboratorDTO,
  ProjectUserCollaboratorService,
  UserRoleCreateDTO,
  WorkPackageActivitySummaryDTO
} from '@cat/api';
import {filter, map, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {ProjectCallSettings} from '@project/model/projectCallSettings';
import {CallFlatRateSetting} from '@project/model/call-flat-rate-setting';
import {ProgrammeLumpSum} from '@project/model/lump-sums/programmeLumpSum';
import {ProgrammeUnitCost} from '@project/model/programmeUnitCost';
import {LumpSumPhaseEnumUtils} from '@project/model/lump-sums/LumpSumPhaseEnum';
import {BudgetCostCategoryEnum, BudgetCostCategoryEnumUtils} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import {RoutingService} from '@common/services/routing.service';
import {ProjectPaths, ProjectUtil} from '@project/common/project-util';
import {SecurityService} from '../../../../../security/security.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {
  InvestmentSummary
} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {AllowedBudgetCategories, AllowedBudgetCategory} from '@project/model/allowed-budget-category';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

/**
 * Stores project related information.
 */
@Injectable({
  providedIn: 'root'
})
export class ProjectStore {

  projectId$ = new ReplaySubject<number>(1);
  projectStatusChanged$ = new Subject();

  currentVersionOfProject$: Observable<ProjectDetailDTO>;
  currentVersionOfProjectTitle$: Observable<string>;
  currentVersionOfProjectStatus$: Observable<ProjectStatusDTO>;

  projectStatus$: Observable<ProjectStatusDTO>;
  project$: Observable<ProjectDetailDTO>;
  projectCallType$: Observable<CallTypeEnum>;
  projectForm$: Observable<ProjectDetailFormDTO>;
  project: ProjectDetailDTO;
  projectEditable$: Observable<boolean>;
  projectTitle$: Observable<string>;
  callHasTwoSteps$: Observable<boolean>;
  projectCurrentDecisions$: Observable<ProjectDecisionDTO>;
  investmentSummaries$: Observable<InvestmentSummary[]>;
  investmentSummariesForFiles$: Observable<InvestmentSummary[]>;
  userIsProjectOwner$: Observable<boolean>;
  userIsPartnerCollaborator$: Observable<boolean>;
  userIsProjectOwnerOrEditCollaborator$: Observable<boolean>;
  allowedBudgetCategories$: Observable<AllowedBudgetCategories>;
  activities$: Observable<WorkPackageActivitySummaryDTO[]>;
  projectPeriods$: Observable<ProjectPeriodDTO[]>;
  collaboratorLevel$: Observable<ProjectUserCollaboratorDTO.LevelEnum>;

  // move to page store
  projectCall$: Observable<ProjectCallSettings>;

  investmentChangeEvent$ = new Subject<void>();

  private projectAcronym$ = new ReplaySubject<string>(1);
  private updatedProjectData$ = new Subject<void>();
  private updatedProjectForm$ = new Subject<ProjectDetailFormDTO>();

  constructor(private projectService: ProjectService,
              private router: RoutingService,
              private securityService: SecurityService,
              private permissionService: PermissionService,
              private projectVersionStore: ProjectVersionStore,
              private callService: CallService,
              private projectUserCollaboratorService: ProjectUserCollaboratorService,
              private projectBudgetService: ProjectBudgetService,
              private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService) {
    this.router.routeParameterChanges(ProjectPaths.PROJECT_DETAIL_PATH, 'projectId')
      .pipe(
        // TODO: remove init make projectId$ just an observable
        tap(id => this.projectId$.next(id as number))
      ).subscribe();

    this.project$ = this.setProject(true);
    this.currentVersionOfProject$ = this.setProject(false);
    this.projectForm$ = this.projectForm();
    this.collaboratorLevel$ = this.collaboratorLevel();
    this.projectEditable$ = this.projectEditable();
    this.projectStatus$ = ProjectStore.projectStatus(this.project$);
    this.projectCallType$ = ProjectStore.projectCallType(this.project$);
    this.currentVersionOfProjectStatus$ = ProjectStore.projectStatus(this.currentVersionOfProject$);
    this.projectCall$ = this.projectCallSettings();
    this.currentVersionOfProjectTitle$ = this.currentVersionOfProject$
      .pipe(
        map(project => `${project.customIdentifier} – ${project.acronym}`)
      );
    this.projectTitle$ = this.project$
      .pipe(
        map(project => `${project.customIdentifier} – ${project.acronym}`)
      );
    this.callHasTwoSteps$ = this.callHasTwoSteps();
    this.projectCurrentDecisions$ = this.projectCurrentDecisions();
    this.investmentSummaries$ = this.investmentSummaries();
    this.investmentSummariesForFiles$ = this.investmentSummariesForFiles();
    this.userIsProjectOwner$ = this.userIsProjectOwner();
    this.userIsPartnerCollaborator$ = this.userIsPartnerCollaborator();
    this.userIsProjectOwnerOrEditCollaborator$ = this.userIsProjectOwnerOrEditCollaborator();
    this.allowedBudgetCategories$ = this.allowedBudgetCategories();
    this.activities$ = this.projectActivities();
    this.projectPeriods$ = this.projectForm$.pipe(
      map(projectForm => projectForm.periods)
    );
  }

  updateProjectData(data: InputProjectData): Observable<ProjectDetailFormDTO> {
    return this.projectId$
      .pipe(
        switchMap(id => this.projectService.updateProjectForm(id, data)),
        tap(projectForm => this.updatedProjectForm$.next(projectForm)),
        tap(saved => Log.info('Updated project data:', this, saved)),
        tap(() => this.updatedProjectData$.next()),
      );
  }

  getProjectBudgetPerFund(): Observable<ProjectPartnerBudgetPerFundDTO[]> {
    return combineLatest([this.projectId$, this.projectVersionStore.selectedVersionParam$])
      .pipe(
        switchMap(([id, version]) => this.projectBudgetService.getProjectPartnerBudgetPerFund(id, version)),
        tap((data: ProjectPartnerBudgetPerFundDTO[]) => Log.info('Fetched project budget per fund:', this, data))
      );
  }

  projectDecisions(step: number | undefined): Observable<ProjectDecisionDTO> {
    return this.project$
      .pipe(
        map(project => step && Number(step) === 2 ? project.secondStepDecision : project.firstStepDecision)
      );
  }

  private static projectStatus(project: Observable<ProjectDetailDTO>): Observable<ProjectStatusDTO> {
    return project
      .pipe(
        map(it => it.projectStatus),
        shareReplay(1)
      );
  }

  private static projectCallType(project: Observable<ProjectDetailDTO>): Observable<CallTypeEnum> {
    return project
      .pipe(
        map(proj => proj.callSettings.callType)
      );
  }

  private setProject(withVersioning: boolean): Observable<ProjectDetailDTO> {
    const byId$ = combineLatest([this.projectId$, withVersioning ? this.projectVersionStore.selectedVersionParam$ : of(undefined)])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id, version]) => this.projectService.getProjectById(id, version)),
        tap(project => Log.info('Fetched project byId:', this, project))
      );

    const byStatusChanged$ = this.projectStatusChanged$
      .pipe(
        withLatestFrom(this.projectId$),
        switchMap(([, id]) => this.projectService.getProjectById(id)),
        tap(() => this.projectVersionStore.refreshVersions()),
        tap(project => Log.info('Fetched project byStatus:', this, project))
      );

    const byProjectDataChanged$ = this.updatedProjectData$
      .pipe(
        withLatestFrom(this.projectId$),
        switchMap(([, id]) => this.projectService.getProjectById(id)),
        tap(project => Log.info('Fetched project due to updated data:', this, project))
      );

    return merge(
      byId$,
      byStatusChanged$,
      byProjectDataChanged$
    )
      .pipe(
        tap(project => this.projectAcronym$.next(project?.acronym)),
        tap(project => this.project = project),
        shareReplay(1)
      );
  }

  private projectForm(): Observable<ProjectDetailFormDTO> {
    const formById$ = combineLatest([this.projectId$, this.projectVersionStore.selectedVersionParam$, this.projectVersionStore.currentVersion$])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id, version]) => this.projectService.getProjectFormById(id, version)),
        tap(project => Log.info('Fetched projectForm:', this, project))
      );

    return merge(
      formById$,
      this.updatedProjectForm$
    )
      .pipe(
        tap(project => this.projectAcronym$.next(project?.acronym)),
        shareReplay(1)
      );
  }

  collaboratorLevel(): Observable<ProjectUserCollaboratorDTO.LevelEnum> {
    return this.projectId$
      .pipe(
        switchMap(id => this.projectUserCollaboratorService.checkMyProjectLevel(id)),
        map(level => level as ProjectUserCollaboratorDTO.LevelEnum),
        tap(level => Log.info('Fetched collaborator level:', this, level)),
        shareReplay(1),
      );
  }

  private projectEditable(): Observable<boolean> {
    return combineLatest([
      this.project$,
      this.permissionService.permissionsChanged(),
      this.securityService.currentUser,
      this.projectVersionStore.isSelectedVersionCurrent$,
      this.collaboratorLevel$,
    ])
      .pipe(
        map(([project, permissions, currentUser, isSelectedVersionCurrent, collaboratorLevel]) => {
          if (!isSelectedVersionCurrent) {
            return false;
          }
          if (!ProjectUtil.isOpenForModifications(project)) {
            return false;
          }
          return permissions.includes(PermissionsEnum.ProjectFormUpdate) ||
            collaboratorLevel === ProjectUserCollaboratorDTO.LevelEnum.EDIT ||
            collaboratorLevel === ProjectUserCollaboratorDTO.LevelEnum.MANAGE;
        }),
        shareReplay(1)
      );
  }

  private projectCallSettings(): Observable<ProjectCallSettings> {
    return this.project$
      .pipe(
        map(project => project.callSettings),
        map((callSetting: ProjectCallSettingsDTO) => new ProjectCallSettings(
          callSetting.callId,
          callSetting.callName,
          callSetting.callType,
          callSetting.startDate,
          callSetting.endDate,
          callSetting.endDateStep1,
          callSetting.lengthOfPeriod,
          new CallFlatRateSetting(
            callSetting.flatRates.staffCostFlatRateSetup,
            callSetting.flatRates.officeAndAdministrationOnStaffCostsFlatRateSetup,
            callSetting.flatRates.officeAndAdministrationOnDirectCostsFlatRateSetup,
            callSetting.flatRates.travelAndAccommodationOnStaffCostsFlatRateSetup,
            callSetting.flatRates.otherCostsOnStaffCostsFlatRateSetup
          ),
          callSetting.lumpSums.map(lumpSum =>
            new ProgrammeLumpSum(lumpSum.id, lumpSum.name, lumpSum.description, lumpSum.cost, lumpSum.splittingAllowed, LumpSumPhaseEnumUtils.toLumpSumPhaseEnum(lumpSum.phase), BudgetCostCategoryEnumUtils.toBudgetCostCategoryEnums(lumpSum.categories))),
          callSetting.unitCosts
            .map(unitCost => new ProgrammeUnitCost(unitCost.id, unitCost.name, unitCost.description, unitCost.type, unitCost.costPerUnit, unitCost.oneCostCategory, BudgetCostCategoryEnumUtils.toBudgetCostCategoryEnums(unitCost.categories))),
          callSetting.additionalFundAllowed,
          callSetting.applicationFormFieldConfigurations
        )),
        shareReplay(1)
      );
  }

  private allowedBudgetCategories(): Observable<AllowedBudgetCategories> {
    const allowedRealCosts$ = this.project$
      .pipe(
        map(project => project.callSettings.callId),
        switchMap(callId => this.callService.getAllowedRealCosts(callId))
      );

    return combineLatest([allowedRealCosts$, this.projectCall$])
      .pipe(
        map(([allowedRealCosts, callSettings]) => (
          new AllowedBudgetCategories([
            this.allowedBudgetCategory(BudgetCostCategoryEnum.STAFF_COSTS, allowedRealCosts.allowRealStaffCosts, callSettings.unitCosts),
            this.allowedBudgetCategory(BudgetCostCategoryEnum.TRAVEL_AND_ACCOMMODATION_COSTS, allowedRealCosts.allowRealTravelAndAccommodationCosts, callSettings.unitCosts),
            this.allowedBudgetCategory(BudgetCostCategoryEnum.EXTERNAL_COSTS, allowedRealCosts.allowRealExternalExpertiseAndServicesCosts, callSettings.unitCosts),
            this.allowedBudgetCategory(BudgetCostCategoryEnum.EQUIPMENT_COSTS, allowedRealCosts.allowRealEquipmentCosts, callSettings.unitCosts),
            this.allowedBudgetCategory(BudgetCostCategoryEnum.INFRASTRUCTURE_COSTS, allowedRealCosts.allowRealInfrastructureCosts, callSettings.unitCosts)
          ])
        )),
        shareReplay(1)
      );
  }

  private allowedBudgetCategory(category: BudgetCostCategoryEnum,
                                allowedRealCost: boolean,
                                unitCosts: ProgrammeUnitCost[]): any {
    const unitCostsEnabled = !!unitCosts.find(unitCost => unitCost.isOneCostCategory && unitCost.categories.includes(category));
    return [category, new AllowedBudgetCategory(allowedRealCost, unitCostsEnabled)];
  }

  private callHasTwoSteps(): Observable<boolean> {
    return this.projectCall$
      .pipe(
        map(call => !!call.endDateStep1)
      );
  }

  private projectCurrentDecisions(): Observable<ProjectDecisionDTO> {
    return this.project$
      .pipe(
        map(project => project.step2Active ? project.secondStepDecision : project.firstStepDecision),
      );
  }


  private investmentSummaries(): Observable<InvestmentSummary[]> {
    return combineLatest([
      this.project$,
      this.projectVersionStore.selectedVersionParam$,
      this.investmentChangeEvent$.pipe(startWith(null))])
      .pipe(
        switchMap(([project, selectedVersion]) => this.getProjectInvestmentSummaries(project, selectedVersion as string)),
        map((investmentSummeryDTOs: InvestmentSummaryDTO[]) => investmentSummeryDTOs
          .map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber))),
        shareReplay(1)
      );
  }

  getProjectInvestmentSummaries(project: ProjectDetailDTO, version: string): Observable<InvestmentSummaryDTO[]> {
    return this.projectService.getProjectInvestmentSummaries(project.id, version);
  }

  private investmentSummariesForFiles(): Observable<InvestmentSummary[]> {
    return combineLatest([
      this.project$,
      this.investmentChangeEvent$.pipe(startWith(null))])
      .pipe(
        switchMap(([project]) => this.projectService.getProjectInvestmentSummaries(project.id)),
        map((investmentSummeryDTOs: InvestmentSummaryDTO[]) => investmentSummeryDTOs.map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber)))
      );
  }

  private userIsProjectOwner(): Observable<boolean> {
    return combineLatest([
      this.project$,
      this.securityService.currentUser,
      this.collaboratorLevel$,
    ])
      .pipe(
        map(([project, currentUser, collaboratorLevel]) => project?.applicant?.id === currentUser?.id || !!collaboratorLevel)
      );
  }

  private userIsProjectOwnerOrEditCollaborator(): Observable<boolean> {
    return combineLatest([
      this.project$,
      this.securityService.currentUser,
      this.collaboratorLevel$,
    ])
      .pipe(
        map(([project, currentUser, collaboratorLevel]) => project?.applicant?.id === currentUser?.id || collaboratorLevel === ProjectUserCollaboratorDTO.LevelEnum.EDIT || collaboratorLevel === ProjectUserCollaboratorDTO.LevelEnum.MANAGE)
      );
  }

  private projectActivities(): Observable<WorkPackageActivitySummaryDTO[]> {
    return this.project$
      .pipe(
        switchMap((project) =>
          this.projectService.getProjectActivities(project.id, '')
        ),
        tap(activities => Log.info('Fetched project activities', activities))
      );
  }

  private userIsPartnerCollaborator(): Observable<boolean> {
    return this.projectId$
      .pipe(
        switchMap(projectId => this.partnerUserCollaboratorService.listCurrentUserPartnerCollaborations(projectId)),
        tap(collaborators => Log.info('Fetched current user partner collaborations', this, collaborators)),
        map(userPartnerCollaborations => {
          return userPartnerCollaborations.length > 0;
        })
      );
  }

}
