import {Injectable, TemplateRef} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {
  catchError,
  debounceTime,
  filter,
  map,
  mergeMap,
  startWith,
  switchMap,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import {ProjectStatusDTO, UserRoleDTO, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '@common/utils/log';
import {TranslateService} from '@ngx-translate/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {RoutingService} from '@common/services/routing.service';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectPaths, ProjectUtil} from '@project/common/project-util';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import StatusEnum = ProjectStatusDTO.StatusEnum;

@Injectable()
@UntilDestroy()
export class ProjectApplicationFormSidenavService {
  private static readonly PROJECT_DETAIL_URL = '/app/project/detail';

  versionSelectTemplate$ = new Subject<TemplateRef<any>>();
  private readonly fetchPartners$ = new Subject<number>();
  private readonly fetchPackages$ = new Subject<number>();

  private readonly canSeeContractMonitoring$: Observable<boolean> = combineLatest([
    this.permissionService.hasPermission(PermissionsEnum.ProjectContractingView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted),
    this.projectStore.currentVersionOfProjectStatus$,
  ]).pipe(
    map(([hasContractingViewPermission, hasSetToContractedPermission, projectStatus]) =>
      (hasContractingViewPermission || hasSetToContractedPermission) && ProjectUtil.isInApprovedOrAnyStatusAfterApproved(projectStatus)
    ),
  );

  private readonly canSeeProjectManagement$: Observable<boolean> = combineLatest([
    this.permissionService.hasPermission(PermissionsEnum.ProjectContractingManagementView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectContractingManagementEdit),
    this.projectStore.userIsProjectOwner$,
    this.projectStore.userIsPartnerCollaborator$,
    this.projectStore.currentVersionOfProjectStatus$,
  ]).pipe(
    map(([hasProjectManagementViewPermission, hasProjectManagementEditPermission, isOwner, isPartnerCollaborator, projectStatus]) =>
      (hasProjectManagementViewPermission || hasProjectManagementEditPermission || isOwner || isPartnerCollaborator) &&
      ProjectUtil.isInApprovedOrAnyStatusAfterApproved(projectStatus)
    )
  );

  private readonly canSeeContractReporting$: Observable<boolean> = combineLatest([
    this.permissionService.hasPermission(PermissionsEnum.ProjectContractingReportingView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectContractingReportingEdit),
    this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorContractingReportingView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorContractingReportingEdit),
    this.projectStore.userIsProjectOwner$,
    this.projectStore.userIsPartnerCollaborator$,
    this.projectStore.currentVersionOfProjectStatus$,
  ]).pipe(
    map(([hasViewPermission, hasEditPermission, hasCreatorViewPermission, hasCreatorEditPermission, isOwner, isPartnerCollaborator, projectStatus]:
           [boolean, boolean, boolean, boolean, boolean, boolean, ProjectStatusDTO]) =>
      ((hasViewPermission || hasEditPermission) || ((hasCreatorViewPermission || hasCreatorEditPermission) && (isOwner || isPartnerCollaborator))) &&
        ProjectUtil.isInApprovedOrAnyStatusAfterApproved(projectStatus),
    ),
  );

  private readonly canSeeReporting$: Observable<boolean> = combineLatest([
    this.projectStore.currentVersionOfProjectStatus$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectReportingView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectReportingEdit),
    this.partnerStore.partnerReportSummaries$
  ]).pipe(
    map(([projectStatus, hasContractingViewPermission, hasSetToContractedPermission, partnerReports]) =>
      (hasContractingViewPermission || hasSetToContractedPermission || partnerReports.length > 0)
      && ProjectUtil.isContractedOrAnyStatusAfterContracted(projectStatus)
    ),
  );

  private readonly canSeeProjectForm$: Observable<boolean> = combineLatest([
    this.permissionService.hasPermission(PermissionsEnum.ProjectFormRetrieve),
    this.projectStore.userIsProjectOwner$,
  ]).pipe(
    map(([hasPermission, isOwner]) => hasPermission || isOwner),
  );

  private readonly canSubmitApplication$: Observable<boolean> = combineLatest([
    this.projectStore.currentVersionOfProjectStatus$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectSubmission),
    this.projectStore.userIsProjectOwnerOrEditCollaborator$,
  ]).pipe(
    map(([projectStatus, hasPermission, userIsProjectOwnerOrEditCollaborator]) =>
      (hasPermission || userIsProjectOwnerOrEditCollaborator) && ProjectUtil.isOpenForModifications(projectStatus)
    ),
  );

  private readonly canCheckApplication$: Observable<boolean> = combineLatest([
    this.projectStore.currentVersionOfProjectStatus$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectCheckApplicationForm),
    this.projectStore.userIsProjectOwner$,
  ]).pipe(
    map(([projectStatus, hasPermission, isOwner]) =>
      (hasPermission || isOwner) && ProjectUtil.isOpenForModifications(projectStatus)
    ),
  );

  private readonly canSeeModificationSection$: Observable<boolean> = combineLatest([
    this.projectStore.currentVersionOfProjectStatus$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectModificationView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectOpenModification),
    this.permissionService.hasPermission(PermissionsEnum.ProjectModificationFileAssessmentRetrieve)
  ]).pipe(
    map(([currentVersionOfProjectStatus, canSeeSection, canOpenModification, canSeeModificationFiles]) => {
        const hasViewPermission = canSeeSection || canOpenModification || canSeeModificationFiles;
        return hasViewPermission && ProjectUtil.isInApprovedOrAnyStatusAfterApproved(currentVersionOfProjectStatus);
      }
    ),
  );

  private readonly canSeePrivilegesSection$: Observable<boolean> = combineLatest([
    this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorCollaboratorsRetrieve),
    this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorCollaboratorsUpdate),
    this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorCollaboratorsRetrieve),
    this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorCollaboratorsUpdate)
  ]).pipe(
    map(perms => perms.some(perm => perm)),
  );

  private readonly canSeeAssessments$: Observable<boolean> = combineLatest([
    this.projectStore.currentVersionOfProjectStatus$.pipe(map(it => it.status)),
    this.projectStore.callHasTwoSteps$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectAssessmentView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectStatusReturnToApplicant),
    this.permissionService.hasPermission(PermissionsEnum.ProjectStartStepTwo),
    this.permissionService.hasPermission(PermissionsEnum.ProjectStatusDecisionRevert),
    this.permissionService.hasPermission(PermissionsEnum.ProjectAssessmentChecklistUpdate),
    this.permissionService.hasPermission(PermissionsEnum.ProjectAssessmentChecklistSelectedRetrieve),
    this.fileManagementStore.canReadAssessmentFile$
  ]).pipe(
    map(([projectStatus, callHas2Steps, hasAssessmentViewPermission, hasReturnToaApplicantPermission, hasStartStepTwoPermission,
           hasRevertDecisionPermission, hasInstantiateChecklistPermission, hasSelectedAssessmentListView, canReadAssessmentFiles]: any) => {
      return (
          (canReadAssessmentFiles || hasAssessmentViewPermission || hasRevertDecisionPermission || hasInstantiateChecklistPermission || hasSelectedAssessmentListView) &&
          ((callHas2Steps && projectStatus !== StatusEnum.STEP1DRAFT && projectStatus !== StatusEnum.DRAFT) || (!callHas2Steps && projectStatus !== StatusEnum.DRAFT))
        )
        || (
          hasStartStepTwoPermission &&
          (projectStatus === StatusEnum.STEP1APPROVED || projectStatus === StatusEnum.STEP1APPROVEDWITHCONDITIONS)
        )
        || (
          hasReturnToaApplicantPermission &&
          (projectStatus === StatusEnum.SUBMITTED || projectStatus === StatusEnum.APPROVED
            || projectStatus === StatusEnum.APPROVEDWITHCONDITIONS || projectStatus === StatusEnum.ELIGIBLE)
        );
    }),
  );

  private readonly partners$: Observable<HeadlineRoute[]> =
    combineLatest([this.canSeeProjectForm$, this.fileManagementStore.canReadApplicationFile$, this.projectStore.projectCallType$]).pipe(
      switchMap(([canSeeProject, canSeeApplicationFiles, callType]) => {
        return (canSeeProject || canSeeApplicationFiles) ?
          this.partnerStore.partnerSummaries$.pipe(
            withLatestFrom(this.projectStore.projectId$),
            map(([partners, projectId]) =>
              partners.map(partner => ({
                  headline: {
                    i18nKey: ProjectPartnerStore.getPartnerTranslationKey(partner.role, callType),
                    i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
                  },
                  icon: partner.active ? '' : 'person_off',
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

  private readonly reportSectionPartners$: Observable<HeadlineRoute[]> =
    combineLatest([this.canSeeReporting$, this.projectStore.projectId$, this.projectStore.projectCallType$]).pipe(
      switchMap(([canSeeReporting, projectId, callType]) => {
        return (canSeeReporting) ?
          this.partnerStore.partnerReportSummaries$.pipe(
            withLatestFrom(this.projectStore.projectId$),
            map(([partners]) =>
              partners.map(partner => ({
                  headline: {
                    i18nKey: ProjectPartnerStore.getPartnerTranslationKey(partner.role, callType),
                    i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
                  },
                  icon: partner.active ? '' : 'person_off',
                  route: `/app/project/detail/${projectId}/reporting/${partner.id}/reports`
                }
              ))
            )
          ) : of([]);
      }),
      catchError(() => of([])),
      startWith([])
    );

  private readonly packages$: Observable<HeadlineRoute[]> =
    this.canSeeProjectForm$.pipe(
      switchMap(canSeeProject => {
        return canSeeProject ?
          combineLatest([merge(this.projectStore.projectId$, this.fetchPackages$), this.projectVersionStore.selectedVersionParam$])
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
              private visibilityStatusService: FormVisibilityStatusService,
  ) {

    const headlines$ = combineLatest([
      this.projectStore.project$,
      this.canSeeReporting$,
      this.reportSectionPartners$,
      this.canSeeContractMonitoring$,
      this.canSeeProjectManagement$,
      this.canSeeContractReporting$,
      this.canSeeAssessments$,
      this.canSubmitApplication$,
      this.canCheckApplication$,
      this.fileManagementStore.canReadApplicationFile$,
      this.partners$,
      this.packages$,
      this.versionSelectTemplate$,
      this.canSeeProjectForm$,
      this.canSeeModificationSection$,
      this.canSeePrivilegesSection$
    ])
      .pipe(
        debounceTime(50), // there's race condition with SidenavService.resetOnLeave
        filter(([project]) => !!project),
        tap(([
               project,
               canSeeReporting,
               reportSectionPartners,
               canSeeContractMonitoring,
               canSeeProjectManagement,
               canSeeContractReporting,
               canSeeAssessments,
               canSubmitApplication,
               canCheckApplication,
               canReadApplicationFiles,
               partners,
               packages,
               versionTemplate,
               canSeeProjectForm,
               canSeeModificationSection,
               canSeePrivilegesSection
             ]: any) => {
          this.sideNavService.setHeadlines(ProjectPaths.PROJECT_DETAIL_PATH, [
            this.getProjectOverviewHeadline(project.id),
            ...canSeeReporting ? this.getReportingHeadline(reportSectionPartners) : [],
            ...(canSeeProjectManagement || canSeeContractMonitoring) ?
              this.getContractingHeadlines(project.id, canSeeContractMonitoring, canSeeProjectManagement, canSeeContractReporting) : [],
            this.getApplicationFormHeadline(project.id, partners, packages, versionTemplate, canReadApplicationFiles,
              canSeeAssessments, canSubmitApplication || canCheckApplication, canSeeProjectForm, canSeeModificationSection),
            ...canSeeProjectForm ? this.getExportHeadline(project.id) : [],
            ...canSeePrivilegesSection ? this.getProjectPrivilegesHeadline(project.id) : [],
          ]);
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

  private getProjectOverviewHeadline(projectId: number): HeadlineRoute {
    return {
      headline: {i18nKey: 'project.application.form.tree.title'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}`,
      scrollToTop: true,
      scrollRoute: '',
      icon: 'home'
    };
  }

  private getContractingHeadlines(projectId: number, canSeeContractMonitoring: boolean, canSeeProjectManagement: boolean, canSeeContractReporting: boolean): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.contracting.title'},
      bullets: [
        ...canSeeContractMonitoring ? this.getContractMonitoringHeadline(projectId) : [],
        ...canSeeProjectManagement ? this.getProjectContractsHeadline(projectId) : [],
        ...canSeeProjectManagement ? this.getProjectManagementHeadline(projectId) : [],
        ...canSeeContractReporting ? this.getContractReportingHeadline(projectId) : []
      ]
    }];
  }

  private getContractMonitoringHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.contract.monitoring.title'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/contractMonitoring`,
      scrollToTop: true,
      scrollRoute: ''
    }];
  }

  private getProjectContractsHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.contract.contracts.title'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/contract`,
      scrollToTop: true,
      scrollRoute: ''
    }];
  }

  private getProjectManagementHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.contract.management.title'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/projectManagement`,
      scrollToTop: true,
      scrollRoute: ''
    }];
  }

  private getContractReportingHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.contract.reporting.title'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/contractReporting`,
      scrollToTop: true,
      scrollRoute: ''
    }];
  }


  private getReportingHeadline(partners: HeadlineRoute[]): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.reporting.title'},
      bullets: [
        ...this.getPartnerReportingSections(partners)
      ]
    }];
  }

  private getPartnerReportingSections(partners: HeadlineRoute[]): HeadlineRoute[] {
    return [
      {
        headline: {i18nKey: 'project.application.partner.reports.title'},
        bullets: [...partners]
      }
    ];
  }

  private getApplicationFormHeadline(projectId: number, partners: HeadlineRoute[], packages: HeadlineRoute[], versionTemplate: TemplateRef<any>, showApplicationAnnexes: boolean, showAssessment: boolean, canCheckOrSubmitApplication: boolean, showProjectForm: boolean, canSeeModificationSection: boolean): HeadlineRoute {
    return {
      headline: {i18nKey: 'project.application.form.title'},
      bullets: [
        ...showProjectForm ? this.getApplicationFormVersionedSections(projectId, partners, packages, versionTemplate) : [],
        ...showApplicationAnnexes ? this.getApplicationAnnexesHeadline(projectId) : [],
        ...canCheckOrSubmitApplication ? this.getCheckAndSubmitHeadline(projectId) : [],
        ...showAssessment ? this.getAssessmentAndDecisionHeadline(projectId) : [],
        ...canSeeModificationSection ? this.getModificationHeadline(projectId) : []
      ]
    };
  }

  private getApplicationFormVersionedSections(projectId: number, partners: HeadlineRoute[], packages: HeadlineRoute[], versionTemplate: TemplateRef<any>): HeadlineRoute[] {
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
            route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormIdentification`,
          },
          ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_A.PROJECT_OVERVIEW_TABLES) ?
            [{
              headline: {i18nKey: 'project.application.form.section.part.a.overview.tables'},
              route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormOverviewTables`,
            }] : [],
        ],
        versionedSection: true
      },
      {
        headline: {i18nKey: 'project.application.form.section.part.b'},
        bullets: [
          {
            headline: {i18nKey: 'project.application.form.section.part.b.partners'},
            route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormPartner`,
            bullets: [...partners],
          },
          ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.PARTNER_ASSOCIATED_ORGANIZATIONS) ?
            [{
              headline: {i18nKey: 'project.application.form.section.part.b.associatedOrganizations'},
              route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormAssociatedOrganization`,
            }] : []
        ],
        versionedSection: true
      },
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c'},
          bullets: this.getSectionCHeadlines(projectId, packages),
          versionedSection: true
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING) ?
        [
          {
            headline: {i18nKey: 'project.application.form.section.part.d'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.d.subsection.one'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormBudgetPerPartner`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.d.subsection.two'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormBudget`,
              },
              ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS) ?
                [{
                  headline: {i18nKey: 'project.application.form.section.part.d.subsection.three'},
                  route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormBudgetPerPeriod`,
                }] : [],
            ],
            versionedSection: true
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.e'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.e.subsection.one'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormLumpSums`,
              },
              ...(this.visibilityStatusService.shouldBeVisibleIfUnitCostsSelected() || this.visibilityStatusService.shouldBeVisibleIfProjectDefinedUnitCostsAllowed()) ?
                [{
                  headline: {i18nKey: 'project.application.form.section.part.e.subsection.two'},
                  route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormUnitCosts`,
                  bullets: this.visibilityStatusService.shouldBeVisibleIfProjectDefinedUnitCostsAllowed() ? [
                    {
                      headline: {i18nKey: 'project.application.form.section.part.e.subsection.two.subsection.one'},
                      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormUnitCosts/projectProposed`,
                    }
                  ] : []
                }] : []
            ],
            versionedSection: true
          }
        ] : [],
    ];
  }

  private getSectionCHeadlines(projectId: number, packages: HeadlineRoute[]): HeadlineRoute[] {
    return [
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_OVERALL_OBJECTIVE) ?
      [{
        headline: {i18nKey: 'project.application.form.section.part.c.subsection.one'},
        route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormOverallObjective`,
      }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_RELEVANCE_AND_CONTEXT) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.two'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormRelevanceAndContext`,
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_PARTNERSHIP) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.three'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormPartnership`,
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.four'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormWorkPackage`,
          bullets: [...packages],
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_RESULT) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.five'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormResults`,
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN) ||
      this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_RESULT) ?
        [{
        headline: {i18nKey: 'project.application.form.section.part.c.subsection.six'},
        route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationTimePlan`,
      }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_MANAGEMENT) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.seven'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormManagement`,
        }] : [],
      ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_LONG_TERM_PLANS) ?
        [{
          headline: {i18nKey: 'project.application.form.section.part.c.subsection.eight'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormFuturePlans`,
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

  private getModificationHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.form.section.modification'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/modification`,
      scrollToTop: true,
      scrollRoute: '',
      icon: 'autorenew'
    }];
  }

  private getExportHeadline(projectId: number): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.form.section.export'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/export`,
      scrollToTop: true,
      scrollRoute: '',
      icon: 'download'
    }];
  }

  private getProjectPrivilegesHeadline(projectId: string): HeadlineRoute[] {
    return [{
      headline: {i18nKey: 'project.application.form.section.privileges'},
      route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/privileges`,
      scrollToTop: true,
      scrollRoute: '',
      icon: 'manage_accounts'
    }];
  }
}
