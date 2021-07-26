import {Injectable, TemplateRef} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, mergeMap, switchMap, tap} from 'rxjs/operators';
import {ProjectDetailDTO, ProjectPartnerService, ProjectStatusDTO, UserRoleDTO, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '@common/utils/log';
import {TranslateService} from '@ngx-translate/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {filter} from 'rxjs/internal/operators';
import {FormVisibilityStatusService} from '@project/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/application-form-model';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import StatusEnum = ProjectStatusDTO.StatusEnum;
import {ProjectVersionStore} from '@project/services/project-version-store.service';
import {RoutingService} from '@common/services/routing.service';

@Injectable()
@UntilDestroy()
export class ProjectApplicationFormSidenavService {
  private static readonly PROJECT_DETAIL_URL = '/app/project/detail';

  versionSelectTemplate$ = new Subject<TemplateRef<any>>();
  private fetchPartners$ = new Subject<number>();
  private fetchPackages$ = new Subject<number>();

  private partners$: Observable<HeadlineRoute[]> =
    combineLatest([merge(this.projectStore.projectId$, this.fetchPartners$), this.projectVersionStore.currentRouteVersion$])
      .pipe(
        mergeMap(([projectId, version]) => forkJoin([
            of(projectId),
            this.projectPartnerService.getProjectPartners(projectId, 0, 100, undefined, version)
          ])
        ),
        tap(([, partners]) => Log.info('Fetched the project partners:', this, partners.content)),
        map(([projectId, partners]) => partners.content
          .map(partner => ({
              headline: {
                i18nKey: 'common.label.project.partner.role.shortcut.' + partner.role,
                i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
              },
              route: `/app/project/detail/${projectId}/applicationFormPartner/${partner.id}/identity`,
            }
          ))
        )
      );

  private packages$: Observable<HeadlineRoute[]> =
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
            }
          ))
        )
      );

  private canSeeAssessments$: Observable<boolean> = this.permissionService
    .hasPermission(PermissionsEnum.ProjectAssessmentView);

  constructor(private sideNavService: SideNavService,
              private projectPartnerService: ProjectPartnerService,
              private workPackageService: WorkPackageService,
              private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private translate: TranslateService,
              private permissionService: PermissionService,
              private routingService: RoutingService,
              private visibilityStatusService: FormVisibilityStatusService
  ) {

    const headlines$ = combineLatest([
      this.canSeeAssessments$,
      this.projectStore.project$,
      this.partners$,
      this.packages$,
      this.versionSelectTemplate$
    ])
      .pipe(
        filter(([, project]) => !!project),
        tap(([canSeeAssessments, project, partners, packages, versionTemplate]) => {
          const status = project.projectStatus.status;
          const callHas2Steps = !!project.callSettings.endDateStep1;
          const showAssessments = (callHas2Steps && status !== StatusEnum.STEP1DRAFT) || (!callHas2Steps && status !== StatusEnum.DRAFT);
          this.setHeadlines(canSeeAssessments && showAssessments, project, partners, packages, versionTemplate);
        }),
        catchError(() => of(null)) // ignore errors to keep the sidelines observable alive
      );

    this.routingService.routeChanges(ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL)
      .pipe(
        filter(isProjectDetailPath => isProjectDetailPath),
        switchMap(() => headlines$),
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
                       project: ProjectDetailDTO,
                       partners: HeadlineRoute[],
                       packages: HeadlineRoute[],
                       versionTemplate: TemplateRef<any>): void {

    this.sideNavService.setHeadlines(ProjectStore.PROJECT_DETAIL_PATH,
                                     [
        this.getProjectOverviewHeadline(project, showAssessment),
        this.getApplicationFormHeadline(project, partners, packages, versionTemplate)
      ]
    );
  }


  private getProjectOverviewHeadline(project: ProjectDetailDTO, showAssessment: boolean): HeadlineRoute {
    return {
      headline: {i18nKey: 'project.application.form.tree.title'},
      bullets: [
        {
          headline: {i18nKey: 'project.application.form.lifecycle.title'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}`,
          scrollToTop: true,
          scrollRoute: ''
        },
        ...showAssessment ? [{
          headline: {i18nKey: 'project.assessment.header'},
          scrollRoute: 'applicationFormLifecycleAssessment',
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}`,
          scrollToTop: false
        }] : [],
        {
          headline: {i18nKey: 'file.tab.header'},
          scrollRoute: 'applicationFormLifecycleAttachments',
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}`,
          scrollToTop: false
        }
      ]
    };
  }

  private getApplicationFormHeadline(project: ProjectDetailDTO, partners: HeadlineRoute[], packages: HeadlineRoute[], versionTemplate: TemplateRef<any>): HeadlineRoute {
    return {
      headline: {i18nKey: 'project.application.form.title'},
      bullets: [
        {
          headlineTemplate: versionTemplate
        },
        {
          headline: {i18nKey: 'project.application.form.section.part.a'},
          bullets: [
            {
              headline: {i18nKey: 'project.application.form.section.part.a'},
              route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormIdentification`,
            },
          ]
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
          ]
        },
        {
          headline: {i18nKey: 'project.application.form.section.part.c'},
          bullets: this.getSectionCHeadlines(project, packages)
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
                }
              ]
            },
            {
              headline: {i18nKey: 'project.application.form.section.part.e'},
              bullets: [
                {
                  headline: {i18nKey: 'project.application.form.section.part.e'},
                  route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormLumpSums`,
                }
              ]
            }
          ] : [],
      ]
    };
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
      {
        headline: {i18nKey: 'project.application.form.section.part.c.subsection.four'},
        route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${project.id}/applicationFormWorkPackage`,
        bullets: [...packages],
      },
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

}
