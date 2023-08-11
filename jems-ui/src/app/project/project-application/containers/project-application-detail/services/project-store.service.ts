import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  CallService,
  InputProjectData,
  InvestmentSummaryDTO,
  OutputProgrammePrioritySimple,
  ProgrammePriorityDTO,
  ProgrammeSpecificObjectiveDTO,
  ProgrammeUnitCostDTO,
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
import {filter, map, mergeMap, shareReplay, startWith, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {BudgetCostCategoryEnum} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import {RoutingService} from '@common/services/routing.service';
import {ProjectPaths, ProjectUtil} from '@project/common/project-util';
import {SecurityService} from '../../../../../security/security.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {
  InvestmentSummary
} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {AllowedBudgetCategories, AllowedBudgetCategory} from '@project/model/allowed-budget-category';
import {NumberService} from '@common/services/number.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

export const AFTER_CONTRACTED_STATUSES: ProjectStatusDTO.StatusEnum[] = [
  ProjectStatusDTO.StatusEnum.CONTRACTED,
  ProjectStatusDTO.StatusEnum.INMODIFICATION,
  ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED,
  ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED,
];
export const AFTER_APPROVED_STATUSES: ProjectStatusDTO.StatusEnum[] = [
  ProjectStatusDTO.StatusEnum.APPROVED,
  ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING,
  ...AFTER_CONTRACTED_STATUSES,
];

/**
 * Stores project related information.
 */
@Injectable({
  providedIn: 'root'
})
export class ProjectStore {

  projectId$: Observable<number>;
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
  userIsEditOrManageCollaborator$: Observable<boolean>;
  allowedBudgetCategories$: Observable<AllowedBudgetCategories>;
  activities$: Observable<WorkPackageActivitySummaryDTO[]>;
  projectPeriods$: Observable<ProjectPeriodDTO[]>;
  collaboratorLevel$: Observable<ProjectUserCollaboratorDTO.LevelEnum>;

  projectCallSettings$: Observable<ProjectCallSettingsDTO>;

  investmentChangeEvent$ = new Subject<void>();

  projectCallObjectives$: Observable<{
    priorities: OutputProgrammePrioritySimple[];
    objectivesWithPolicies: { [p: string]: ProgrammeSpecificObjectiveDTO[] };
  }>;

  projectBudget$: Observable<number>;


  private projectAcronym$ = new ReplaySubject<string>(1);
  private updatedProjectData$ = new Subject<void>();
  private updatedProjectForm$ = new Subject<ProjectDetailFormDTO>();
  private projectCallSettingsSubject$ = new ReplaySubject<ProjectCallSettingsDTO>(1);

  constructor(private projectService: ProjectService,
              private router: RoutingService,
              private securityService: SecurityService,
              private permissionService: PermissionService,
              private projectVersionStore: ProjectVersionStore,
              private callService: CallService,
              private projectUserCollaboratorService: ProjectUserCollaboratorService,
              private projectBudgetService: ProjectBudgetService,
              private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService) {
    this.projectId$ = this.router.routeParameterChanges(ProjectPaths.PROJECT_DETAIL_PATH, 'projectId')
      .pipe(map(Number));

    this.project$ = this.setProject(true);
    this.currentVersionOfProject$ = this.setProject(false);
    this.projectForm$ = this.projectForm();
    this.collaboratorLevel$ = this.collaboratorLevel();
    this.projectEditable$ = this.projectEditable();
    this.projectStatus$ = ProjectStore.projectStatus(this.project$);
    this.projectCallType$ = ProjectStore.projectCallType(this.project$);
    this.currentVersionOfProjectStatus$ = ProjectStore.projectStatus(this.currentVersionOfProject$);
    this.projectCallSettings();
    this.projectCallSettings$ = this.projectCallSettingsSubject$.asObservable().pipe(shareReplay(1));
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
    this.userIsEditOrManageCollaborator$ = this.userIsEditOrManageCollaborator();
    this.allowedBudgetCategories$ = this.allowedBudgetCategories();
    this.activities$ = this.projectActivities();
    this.projectPeriods$ = this.projectForm$.pipe(
      map(projectForm => projectForm.periods)
    );
    this.projectCallObjectives$ = this.projectCallObjectives();
    this.projectBudget$ = this.getProjectBudget();
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

  /* returns the last approved version of project total budget */
  getProjectBudget(): Observable<number> {
    return combineLatest([
      this.projectId$,
      this.projectVersionStore.lastApprovedOrContractedVersion$,
    ]).pipe(
      switchMap(([projectId, version]) =>
        this.projectService.getProjectCoFinancingOverview(projectId, version?.version)
      ),
      map(data => NumberService.sum([
        data.projectManagementCoFinancing.totalFundAndContribution,
        data.projectSpfCoFinancing.totalFundAndContribution
      ])),
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
      this.projectVersionStore.isSelectedVersionCurrent$,
      this.collaboratorLevel$,
    ])
      .pipe(
        map(([project, permissions, isSelectedVersionCurrent, collaboratorLevel]) => {
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

  private projectCallSettings(): void {
    this.projectId$
      .pipe(
        filter(projectId => projectId !== 0),
        switchMap(projectId => this.projectService.getProjectCallSettingsById(projectId)),
        take(1),
        tap(value => this.projectCallSettingsSubject$.next(value)),
        tap(value => Log.info('Fetched Project call settings:', this, value))
      ).subscribe();
  }

  private allowedBudgetCategories(): Observable<AllowedBudgetCategories> {
    const allowedRealCosts$ = this.project$
      .pipe(
        map(project => project.callSettings.callId),
        switchMap(callId => this.callService.getAllowedRealCosts(callId))
      );

    return combineLatest([allowedRealCosts$, this.projectCallSettings$])
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
                                unitCosts: ProgrammeUnitCostDTO[]): any {
    const unitCostsEnabled = !!unitCosts.find(unitCost => unitCost.oneCostCategory && unitCost.categories.includes(category));
    return [category, new AllowedBudgetCategory(allowedRealCost, unitCostsEnabled)];
  }

  private callHasTwoSteps(): Observable<boolean> {
    return this.projectCallSettings$
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
          .map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber, it.deactivated))),
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
        map((investmentSummeryDTOs: InvestmentSummaryDTO[]) => investmentSummeryDTOs.map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber, it.deactivated)))
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

  private userIsEditOrManageCollaborator(): Observable<boolean> {
    return this.collaboratorLevel$
      .pipe(
        map(collaboratorLevel => collaboratorLevel === ProjectUserCollaboratorDTO.LevelEnum.EDIT || collaboratorLevel === ProjectUserCollaboratorDTO.LevelEnum.MANAGE)
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
        }),
        shareReplay(1)
      );
  }

  private projectCallObjectives(): Observable<{
    priorities: OutputProgrammePrioritySimple[];
    objectivesWithPolicies: { [p: string]: ProgrammeSpecificObjectiveDTO[] };
  }> {
    return this.project$
      .pipe(
        mergeMap(project => this.callService.getCallById(project.callSettings.callId)),
        map(call => call.objectives),
        tap(objectives => Log.info('Fetched objectives', this, objectives)),
        map(objectives => ({
          priorities: objectives
            .sort((a, b) => {
              const orderBool = a.code.toLocaleLowerCase() > b.code.toLocaleLowerCase();
              return orderBool ? 1 : -1;
            })
            .map(objective => ({title: objective.title, code: objective.code}) as OutputProgrammePrioritySimple),
          objectivesWithPolicies: this.getObjectivesWithPolicies(objectives)
        }))
      );
  }

  private getObjectivesWithPolicies(objectives: ProgrammePriorityDTO[]): { [key: string]: ProgrammeSpecificObjectiveDTO[] } {
    const objectivesWithPolicies: { [key: string]: ProgrammeSpecificObjectiveDTO[] } = {};
    objectives.forEach(objective =>
      objectivesWithPolicies[objective.code] =
        objective.specificObjectives.map(priority => priority));
    return objectivesWithPolicies;
  }

}
