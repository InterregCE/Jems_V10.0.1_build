import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {
  InputProjectData,
  InputProjectEligibilityAssessment,
  InputProjectQualityAssessment,
  ProjectDecisionDTO,
  ProjectDetailDTO,
  ProjectPartnerBudgetCoFinancingDTO,
  ProjectService,
  ProjectStatusDTO,
  ProjectStatusService
} from '@cat/api';
import {distinctUntilChanged, filter, map, mergeMap, shareReplay, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectCallSettings} from '../../../../model/projectCallSettings';
import {CallFlatRateSetting} from '../../../../model/call-flat-rate-setting';
import {ProgrammeLumpSum} from '../../../../model/lump-sums/programmeLumpSum';
import {ProgrammeUnitCost} from '../../../../model/programmeUnitCost';
import {LumpSumPhaseEnumUtils} from '../../../../model/lump-sums/LumpSumPhaseEnum';
import {BudgetCostCategoryEnumUtils} from '../../../../model/lump-sums/BudgetCostCategoryEnum';
import {RoutingService} from '../../../../../common/services/routing.service';

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
  projectEditable$: Observable<boolean>;
  projectTitle$: Observable<string>;
  callHasTwoSteps$: Observable<boolean>;
  projectCurrentDecisions$: Observable<ProjectDecisionDTO>;

  // move to page store
  projectCall$: Observable<ProjectCallSettings>;

  private projectAcronym$ = new ReplaySubject<string>(1);
  private newEligibilityAssessment$ = new Subject<InputProjectEligibilityAssessment>();
  private newQualityAssessment$ = new Subject<InputProjectQualityAssessment>();
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
              private permissionService: PermissionService) {
    this.router.routeParameterChanges(ProjectStore.PROJECT_DETAIL_PATH, 'projectId')
      .pipe(
        // TODO: remove init make projectId$ just an observable
        tap(id => this.projectId$.next(Number(id)))
      ).subscribe();

    this.project$ = this.project();
    this.projectEditable$ = this.projectEditable();
    this.projectStatus$ = this.projectStatus();
    this.projectCall$ = this.projectCallSettings();
    this.projectTitle$ = this.project$
      .pipe(
        map(project => `${project.id} – ${project.acronym}`)
      );
    this.callHasTwoSteps$ = this.callHasTwoSteps();
    this.projectCurrentDecisions$ = this.projectCurrentDecisions();
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

  setEligibilityAssessment(assessment: InputProjectEligibilityAssessment): void {
    this.newEligibilityAssessment$.next(assessment);
  }

  setQualityAssessment(assessment: InputProjectQualityAssessment): void {
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
    const byId$ = this.projectId$
      .pipe(
        filter(id => !!id),
        switchMap(id => this.projectService.getProjectById(id)),
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
    return combineLatest([this.project$, this.permissionService.permissionsChanged()])
      .pipe(
        map(([project, permissions]) => {
            if (permissions.some(perm => perm === Permission.PROGRAMME_USER)) {
              // programme users cannot edit projects
              return false;
            }
            return project.projectStatus.status === ProjectStatusDTO.StatusEnum.DRAFT
              || project.projectStatus.status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT;
          }
        ),
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
}
