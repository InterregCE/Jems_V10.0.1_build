import {Injectable, TemplateRef} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {
  catchError,
  debounceTime,
  map,
  mergeMap,
  startWith,
  switchMap,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import {ProjectDetailDTO, ProjectStatusDTO, UserRoleDTO, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '@common/utils/log';
import {TranslateService} from '@ngx-translate/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {filter} from 'rxjs/internal/operators';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {RoutingService} from '@common/services/routing.service';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectPaths, ProjectUtil} from '@project/common/project-util';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import StatusEnum = ProjectStatusDTO.StatusEnum;

@Injectable()
@UntilDestroy()
export class ProjectApplicationFormSidenavService {
  private static readonly PROJECT_DETAIL_URL = '/app/project/detail';

  versionSelectTemplate$ = new Subject<TemplateRef<any>>();
  private fetchPartners$ = new Subject<number>();
  private fetchPackages$ = new Subject<number>();

  private canSeeProjectForm$: Observable<boolean> = combineLatest([
    this.permissionService.hasPermission(PermissionsEnum.ProjectFormRetrieve),
    this.projectStore.userIsProjectOwner$,
  ]).pipe(
    map(([hasPermission, isOwner]) => hasPermission || isOwner),
  );

  private canSubmitApplication$: Observable<boolean> = combineLatest([
    this.projectStore.projectStatus$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectSubmission),
    this.projectStore.userIsProjectOwner$,
  ]).pipe(
    map(([projectStatus, hasPermission, isOwner]) =>
      (hasPermission || isOwner) && ProjectUtil.isOpenForModifications(projectStatus)
    ),
  );

  private canCheckApplication$: Observable<boolean> = combineLatest([
    this.projectStore.projectStatus$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectCheckApplicationForm),
    this.projectStore.userIsProjectOwner$,
  ]).pipe(
    map(([projectStatus, hasPermission, isOwner]) =>
      (hasPermission || isOwner) && ProjectUtil.isOpenForModifications(projectStatus)
    ),
  );

  private canSeeAssessments$: Observable<boolean> = combineLatest([
    this.projectStore.projectStatus$.pipe(map(it => it.status)),
    this.projectStore.callHasTwoSteps$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectAssessmentView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectStatusReturnToApplicant),
    this.permissionService.hasPermission(PermissionsEnum.ProjectStartStepTwo),
    this.permissionService.hasPermission(PermissionsEnum.ProjectStatusDecisionRevert),
    this.fileManagementStore.canReadAssessmentFile$,
  ]).pipe(
    map(([projectStatus, callHas2Steps, hasAssessmentViewPermission, hasReturnToaApplicantPermission, hasStartStepTwoPermission, hasRevertDecisionPermission, canReadAssessmentFiles]: any) => {
      return ((canReadAssessmentFiles || hasAssessmentViewPermission || hasRevertDecisionPermission) && ((callHas2Steps && projectStatus !== StatusEnum.STEP1DRAFT) || (!callHas2Steps && projectStatus !== StatusEnum.DRAFT)))
        || (hasStartStepTwoPermission && (projectStatus === StatusEnum.STEP1APPROVED || projectStatus === StatusEnum.STEP1APPROVEDWITHCONDITIONS))
        || (hasReturnToaApplicantPermission && (projectStatus === StatusEnum.SUBMITTED || projectStatus === StatusEnum.APPROVED || projectStatus === StatusEnum.APPROVEDWITHCONDITIONS || projectStatus === StatusEnum.ELIGIBLE));
    }),
  );

  private partners$: Observable<HeadlineRoute[]> =
    combineLatest([this.canSeeProjectForm$, this.fileManagementStore.canReadApplicationFile$]).pipe(
      switchMap(([canSeeProject, canSeeApplicationFiles]) => {
        return (canSeeProject || canSeeApplicationFiles) ?
          this.partnerStore.partnerSummaries$.pipe(
            withLatestFrom(this.projectStore.projectId$),
            map(([partners, projectId]) =>
              partners.map(partner => ({
                  headline: {
                    i18nKey: 'common.label.project.partner.role.shortcut.' + partner.role,
                    i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
                  },
                  route: `/app/project/detail/${projectId}/applicationFormPartner/${partner.id}/identity`,
                  baseRoute: `/app/project/detail/${projectId}/applicationFormPartner/${partner.id}`,
                }
              ))
            )
          ) : of([]);
      }),
      catchError(() => of([])),
      startWith([])
    );

  private packages$: Observable<HeadlineRoute[]> =
    this.canSeeProjectForm$.pipe(
      switchMap(canSeeProject => {
        return canSeeProject ?
          combineLatest([merge(this.projectStore.projectId$, this.fetchPackages$), this.projectVersionStore.currentRouteVersion$])
            .pipe(
              mergeMap(([projectId, version]) => forkJoin([
                  of(projectId),
                  this.workPackageService.getWorkPackagesByProjectId(projectId, version)
                ])
              ),
              tap(([, packages]) => Log.info('Fetched the project work packages:', this, packages)),
              map(([projectId, packages]) => packages
                .map(workPackage => ({
                    headline: {
                      i18nKey: 'common.label.workpackage.shortcut',
                      i18nArguments: {workpackage: `${workPackage.number}`},
                      disabled: true
                    },
                    route: `/app/project/detail/${projectId}/applicationFormWorkPackage/${workPackage.id}/objectives`,
                    baseRoute: `/app/project/detail/${projectId}/applicationFormWorkPackage/${workPackage.id}`,
                  }
                ))
              )
            ) : of([]);
      }),
      catchError(() => of([])),
      startWith([])
    );

  constructor(private sideNavService: SideNavService,
              private partnerStore: ProjectPartnerStore,
              private workPackageService: WorkPackageService,
              private projectStore: ProjectStore,
              private fileManagementStore: FileManagementStore,
              private projectVersionStore: ProjectVersionStore,
              private translate: TranslateService,
              private permissionService: PermissionService,
              private routingService: RoutingService,
              private visibilityStatusService: FormVisibilityStatusService
  ) {

    const headlines$ = combineLatest([
      this.projectStore.project$,
      this.canSeeAssessments$,
      this.canSubmitApplication$,
      this.canCheckApplication$,
      this.fileManagementStore.canReadApplicationFile$,
      this.partners$,
      this.packages$,
      this.versionSelectTemplate$,
      this.canSeeProjectForm$,
    ])
      .pipe(
        debounceTime(50), // there's race condition with SidenavService.resetOnLeave
        filter(([project]) => !!project),
        tap(([project, canSeeAssessments, canSubmitApplication, canCheckApplication, canReadApplicationFiles, partners, packages, versionTemplate, canSeeProjectForm]: any) => {
          this.setHeadlines(canSeeAssessments, canSubmitApplication, canCheckApplication, canSeeProjectForm, canReadApplicationFiles, project, partners, packages, versionTemplate);
        }),
        catchError(() => of(null)) // ignore errors to keep the sidelines observable alive
      );

    this.routingService.routeChanges(ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL)
      .pipe(
        switchMap(isProjectDetailPath => isProjectDetailPath ? headlines$ : of(null)),
        untilDestroyed(this)
      ).subscribe();
  }

  refreshPartners(projectId: number): void {
    this.fetchPartners$.next(projectId);
  }

  refreshPackages(projectId: number): void {
    this.fetchPackages$.next(projectId);
  }

  private setHeadlines(showAssessment: boolean,
                       canSubmitApplication: boolean,
                       canCheckApplication: boolean,
                       showProjectForm: boolean,
                       showApplicationAnnexes: boolean,
                       project: ProjectDetailDTO,
                       partners: HeadlineRoute[],
                       packages: HeadlineRoute[],
                       versionTemplate: TemplateRef<any>): void {
    this.sideNavService.setHeadlines(ProjectPaths.PROJECT_DETAIL_PATH, [
      this.getProjectOverviewHeadline(project),
      this.getApplicationFormHeadline(project, partners, packages, versionTemplate, showApplicationAnnexes, showAssessment, canSubmitApplication || canCheckApplication, showProjectForm)
    ]);
  }

  private getProjectOverviewHeadline(project: ProjectDetailDTO): HeadlineRoute {
    return {
      headline: {i18nKey: 'project.application.form.tree.title'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}`,
      scrollToTop: true,
      scrollRoute: '',
      icon: 'home'
    };
  }

  private getApplicationFormHeadline(project: ProjectDetailDTO, partners: HeadlineRoute[], packages: HeadlineRoute[], versionTemplate: TemplateRef<any>, showApplicationAnnexes: boolean, showAssessment: boolean, canCheckOrSubmitApplication: boolean, showProjectForm: boolean): HeadlineRoute {
    return {
      headline: {i18nKey: 'project.application.form.title'},
      bullets: [
        ...showProjectForm ? this.getApplicationFormVersionedSections(project, partners, packages, versionTemplate) : [],
        ...showApplicationAnnexes ? this.getApplicationAnnexesHeadline(project.id) : [],
        ...canCheckOrSubmitApplication ? this.getCheckAndSubmitHeadline(project.id) : [],
        ...showAssessment ? this.getAssessmentAndDecisionHeadline(project.id) : []
      ]
    };
  }

  private getApplicationFormVersionedSections(project: ProjectDetailDTO, partners: HeadlineRoute[], packages: HeadlineRoute[], versionTemplate: TemplateRef<any>): HeadlineRoute[] {
    return [
      {
        headlineTemplate: versionTemplate,
        versionedSection: true
      },
      {
        headline: {i18nKey: 'project.application.form.section.part.a'},
        bullets: [
          {
            headline: {i18nKey: 'project.application.form.section.part.a'},
            route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormIdentification`,
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.a.overview.tables'},
            route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormOverviewTables`,
          },
        ],
        versionedSection: true
      },
      {
        headline: {i18nKey: 'project.application.form.section.part.b'},
        bullets: [
          {
            headline: {i18nKey: 'project.application.form.section.part.b.partners'},
            route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormPartner`,
            bullets: [...partners],
          },
          ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.PARTNER_ASSOCIATED_ORGANIZATIONS) ?
            [{
              headline: {i18nKey: 'project.application.form.section.part.b.associatedOrganizations'},
              route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormAssociatedOrganization`,
            }] : []
        ],
        versionedSection: true
      },
      {
        headline: {i18nKey: 'project.application.form.section.part.c'},
        bullets: this.getSectionCHeadlines(project, packages),
        versionedSection: true
      },
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING) ?
        [
          {
            headline: {i18nKey: 'project.application.form.section.part.d'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.d.subsection.one'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormBudgetPerPartner`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.d.subsection.two'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormBudget`,
              },
              ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS) ?
                [{
                  headline: {i18nKey: 'project.application.form.section.part.d.subsection.three'},
                  route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormBudgetPerPeriod`,
                }] : [],
            ],
            versionedSection: true
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.e'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.e'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormLumpSums`,
              }
            ],
            versionedSection: true
          }
        ] : [],
    ];
  }

  private getSectionCHeadlines(project: ProjectDetailDTO, packages: HeadlineRoute[]): HeadlineRoute[] {
    return [
      {
        headline: {i18nKey: 'project.application.form.section.part.c.subsection.one'},
        route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormOverallObjective`,
      },
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_RELEVANCE_AND_CONTEXT) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.two'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormRelevanceAndContext`,
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_PARTNERSHIP) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.three'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormPartnership`,
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.four'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormWorkPackage`,
          bullets: [...packages],
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_RESULT) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.five'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormResults`,
        }] : [],
      {
        headline: {i18nKey: 'project.application.form.section.part.c.subsection.six'},
        route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationTimePlan`,
      },
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_MANAGEMENT) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.seven'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormManagement`,
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_LONG_TERM_PLANS) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.eight'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormFuturePlans`,
        }] : []
    ];
  }

  private getApplicationAnnexesHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.form.section.application.annexes'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/annexes`,
      scrollToTop: true,
      scrollRoute: '',
      icon: 'attachment'
    }];
  }

  private getCheckAndSubmitHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.form.section.check.and.submit'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/checkAndSubmit`,
      scrollToTop: true,
      scrollRoute: '',
      icon: `send`
    }];
  }

  private getAssessmentAndDecisionHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.form.section.assessment.and.decision'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/assessmentAndDecision`,
      scrollToTop: true,
      scrollRoute: '',
      icon: 'visibility'
    }];
  }

}
