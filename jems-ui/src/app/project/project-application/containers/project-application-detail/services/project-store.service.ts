import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {
  InputProjectData,
  ProjectAssessmentEligibilityDTO,
  ProjectAssessmentQualityDTO,
  InvestmentSummaryDTO,
  ProjectDecisionDTO,
  ProjectDetailDTO,
  ProjectPartnerBudgetCoFinancingDTO,
  ProjectService,
  ProjectStatusDTO,
  ProjectStatusService,
  ProjectVersionDTO,
  UserRoleCreateDTO,
} from '@cat/api';
import {
  distinctUntilChanged,
  filter,
  map,
  mergeMap,
  shareReplay,
  startWith,
  switchMap,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {ProjectCallSettings} from '../../../../model/projectCallSettings';
import {CallFlatRateSetting} from '../../../../model/call-flat-rate-setting';
import {ProgrammeLumpSum} from '../../../../model/lump-sums/programmeLumpSum';
import {ProgrammeUnitCost} from '../../../../model/programmeUnitCost';
import {LumpSumPhaseEnumUtils} from '../../../../model/lump-sums/LumpSumPhaseEnum';
import {BudgetCostCategoryEnumUtils} from '../../../../model/lump-sums/BudgetCostCategoryEnum';
import {RoutingService} from '../../../../../common/services/routing.service';
import {ProjectUtil} from '../../../../project-util';
import {SecurityService} from '../../../../../security/security.service';
import {ProjectVersionStore} from '../../../../services/project-version-store.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {InvestmentSummary} from '../../../../work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';

/**
 * Stores project related information.
 */
@Injectable()
export class ProjectStore {
  public static PROJECT_DETAIL_PATH = '/app/project/detail/';

  projectId$ = new ReplaySubject<number>(1);
  projectStatusChanged$ = new Subject();

  projectStatus$: Observable<ProjectStatusDTO.StatusEnum>;
  project$: Observable<ProjectDetailDTO>;
  currentVersionIsLatest$: Observable<boolean>;
  projectEditable$: Observable<boolean>;
  projectTitle$: Observable<string>;
  callHasTwoSteps$: Observable<boolean>;
  projectCurrentDecisions$: Observable<ProjectDecisionDTO>;
  investmentSummaries$: Observable<InvestmentSummary[]>;

  // move to page store
  projectCall$: Observable<ProjectCallSettings>;

  investmentChangeEvent$ = new Subject<void>();

  private projectAcronym$ = new ReplaySubject<string>(1);
  private newEligibilityAssessment$ = new Subject<ProjectAssessmentEligibilityDTO>();
  private newQualityAssessment$ = new Subject<ProjectAssessmentQualityDTO>();
  private updatedProjectData$ = new Subject<ProjectDetailDTO>();

  private changedEligibilityAssessment$ = this.newEligibilityAssessment$
    .pipe(
      withLatestFrom(this.projectId$),
      mergeMap(([assessment, id]) => this.projectStatusService.setEligibilityAssessment(id, assessment)),
      tap(saved => Log.info('Updated project eligibility assessment:', this, saved)),
      tap(saved => this.router.navigate(['app', 'project', 'detail', saved.id]))
    );

  private changedQualityAssessment$ = this.newQualityAssessment$
    .pipe(
      withLatestFrom(this.projectId$),
      mergeMap(([assessment, id]) => this.projectStatusService.setQualityAssessment(id, assessment)),
      tap(saved => Log.info('Updated project quality assessment:', this, saved)),
      tap(saved => this.router.navigate(['app', 'project', 'detail', saved.id]))
    );

  constructor(private projectService: ProjectService,
              private projectStatusService: ProjectStatusService,
              private router: RoutingService,
              private securityService: SecurityService,
              private permissionService: PermissionService,
              private projectVersionStore: ProjectVersionStore) {
    this.router.routeParameterChanges(ProjectStore.PROJECT_DETAIL_PATH, 'projectId')
      .pipe(
        // TODO: remove init make projectId$ just an observable
        tap(id => this.projectId$.next(id as number))
      ).subscribe();

    this.project$ = this.project();
    this.currentVersionIsLatest$ = this.currentVersionIsLatest();
    this.projectEditable$ = this.projectEditable();
    this.projectStatus$ = this.projectStatus();
    this.projectCall$ = this.projectCallSettings();
    this.projectTitle$ = this.project$
      .pipe(
        map(project => `${project.id} – ${project.acronym}`)
      );
    this.callHasTwoSteps$ = this.callHasTwoSteps();
    this.projectCurrentDecisions$ = this.projectCurrentDecisions();
    this.investmentSummaries$ = this.investmentSummaries();
  }

  /**
   * TODO: remove and use project$
   */
  getProject(): Observable<ProjectDetailDTO> {
    return this.project$;
  }

  updateProjectData(data: InputProjectData): Observable<ProjectDetailDTO> {
    return this.projectId$
      .pipe(
        switchMap(id => this.projectService.updateProjectData(id, data)),
        tap(project => this.updatedProjectData$.next(project)),
        tap(saved => Log.info('Updated project data:', this, saved)),
      );
  }

  getProjectCoFinancing(): Observable<ProjectPartnerBudgetCoFinancingDTO[]> {
    return this.projectId$
      .pipe(
        distinctUntilChanged(),
        switchMap(id => this.projectService.getProjectCoFinancing(id)),
        tap((data: ProjectPartnerBudgetCoFinancingDTO[]) => Log.info('Fetched project co-financing:', this, data))
      );
  }

  setEligibilityAssessment(assessment: ProjectAssessmentEligibilityDTO): void {
    this.newEligibilityAssessment$.next(assessment);
  }

  setQualityAssessment(assessment: ProjectAssessmentQualityDTO): void {
    this.newQualityAssessment$.next(assessment);
  }

  private projectStatus(): Observable<ProjectStatusDTO.StatusEnum> {
    return this.project$
      .pipe(
        map(project => project.projectStatus.status),
        shareReplay(1)
      );
  }

  private project(): Observable<ProjectDetailDTO> {
    const byId$ = combineLatest([this.projectId$, this.projectVersionStore.currentRouteVersion$])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id, version]) => this.projectService.getProjectById(id, version)),
        tap(project => Log.info('Fetched project:', this, project))
      );

    const byStatusChanged$ = this.projectStatusChanged$
      .pipe(
        withLatestFrom(this.projectId$),
        switchMap(([, id]) => this.projectService.getProjectById(id)),
        tap(project => Log.info('Fetched project:', this, project))
      );

    return merge(
      byId$,
      byStatusChanged$,
      this.changedEligibilityAssessment$,
      this.changedQualityAssessment$,
      this.updatedProjectData$
    )
      .pipe(
        tap(project => this.projectAcronym$.next(project?.acronym)),
        shareReplay(1)
      );
  }

  private projectEditable(): Observable<boolean> {
    return combineLatest([
      this.project$,
      this.permissionService.permissionsChanged(),
      this.securityService.currentUser,
      this.currentVersionIsLatest$
    ])
      .pipe(
        map(([project, permissions, currentUser, currentVersionIsLatest]) => {
          if (!currentVersionIsLatest) {
            return false;
          }
          if (permissions.includes(PermissionsEnum.ProjectUpdate)) {
            return true;
          }
          return ProjectUtil.isOpenForModifications(project) && currentUser?.id === project.applicant.id;
        }),
        shareReplay(1)
      );
  }

  private projectCallSettings(): Observable<ProjectCallSettings> {
    return this.project$
      .pipe(
        map(project => project.callSettings),
        map(callSetting => new ProjectCallSettings(
          callSetting.callId,
          callSetting.callName,
          callSetting.startDate,
          callSetting.endDate,
          callSetting.endDateStep1,
          callSetting.lengthOfPeriod,
          new CallFlatRateSetting(callSetting.flatRates.staffCostFlatRateSetup, callSetting.flatRates.officeAndAdministrationOnStaffCostsFlatRateSetup, callSetting.flatRates.officeAndAdministrationOnDirectCostsFlatRateSetup, callSetting.flatRates.travelAndAccommodationOnStaffCostsFlatRateSetup, callSetting.flatRates.otherCostsOnStaffCostsFlatRateSetup),
          callSetting.lumpSums.map(lumpSum =>
            new ProgrammeLumpSum(lumpSum.id, lumpSum.name, lumpSum.description, lumpSum.cost, lumpSum.splittingAllowed, LumpSumPhaseEnumUtils.toLumpSumPhaseEnum(lumpSum.phase), BudgetCostCategoryEnumUtils.toBudgetCostCategoryEnums(lumpSum.categories))),
          callSetting.unitCosts
            .map(unitCost => new ProgrammeUnitCost(unitCost.id, unitCost.name, unitCost.description, unitCost.type, unitCost.costPerUnit, unitCost.isOneCostCategory, BudgetCostCategoryEnumUtils.toBudgetCostCategoryEnums(unitCost.categories))),
          callSetting.isAdditionalFundAllowed
        )),
        shareReplay(1)
      );
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

  projectDecisions(step: number | undefined): Observable<ProjectDecisionDTO> {
    return this.project$
      .pipe(
        map(project => step && Number(step) === 2 ? project.secondStepDecision : project.firstStepDecision)
      );
  }

  private currentVersionIsLatest(): Observable<boolean> {
    return combineLatest([
      this.projectVersionStore.currentRouteVersion$,
      this.projectVersionStore.versions$,
      this.project$.pipe(
        distinctUntilChanged((o, n) => o.projectStatus.status === n.projectStatus.status)
      )
    ])
      .pipe(
        map(([currentVersion, versions, project]) => {
            if (!currentVersion) {
              return true;
            }
            const latest = this.latestVersion(versions);
            const current = Number(currentVersion);
            // if project is editable the current version is the next one
            return ProjectUtil.isOpenForModifications(project) ? latest < current : latest === current;
          }
        ),
        shareReplay(1)
      );
  }

  private latestVersion(versions?: ProjectVersionDTO[]): number {
    return versions?.length ? Number(versions[0].version) : 1;
  }

  private investmentSummaries(): Observable<InvestmentSummary[]> {
    return combineLatest([
      this.project$,
      this.projectVersionStore.currentRouteVersion$,
      this.investmentChangeEvent$.pipe(startWith(null))])
      .pipe(
        switchMap(([project, version]) => this.projectService.getProjectInvestmentSummaries(project.id, version)),
        map((investmentSummeryDTOs: InvestmentSummaryDTO[]) => investmentSummeryDTOs.map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber))),
        shareReplay(1)
      );
  }
}
