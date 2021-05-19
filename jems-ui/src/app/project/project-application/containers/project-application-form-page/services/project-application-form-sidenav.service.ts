import {Injectable, TemplateRef} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, mergeMap, switchMap, tap} from 'rxjs/operators';
import {ProjectPartnerService, ProjectStatusDTO, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '../../../../../common/utils/log';
import {TranslateService} from '@ngx-translate/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProjectUtil} from '../../../../project-util';
import {filter} from 'rxjs/internal/operators';
import {ProjectVersionStore} from '../../../../services/project-version-store.service';
import {RoutingService} from '../../../../../common/services/routing.service';

@Injectable()
@UntilDestroy()
export class ProjectApplicationFormSidenavService {
  private static readonly PROJECT_DETAIL_URL = '/app/project/detail';

  versionSelectTemplate$ = new Subject<TemplateRef<any>>();
  private fetchPartners$ = new Subject<number>();
  private fetchPackages$ = new Subject<number>();

  private partners$: Observable<HeadlineRoute[]> =
    merge(this.projectStore.projectId$, this.fetchPartners$)
      .pipe(
        mergeMap(projectId => forkJoin([
            of(projectId),
            this.projectPartnerService.getProjectPartners(projectId, 0, 100, ['sortNumber,asc'])
          ])
        ),
        tap(([projectId, partners]) => Log.info('Fetched the project partners:', this, partners.content)),
        map(([projectId, partners]) => partners.content
          .map(partner => ({
              headline: {
                i18nKey: 'common.label.project.partner.role.shortcut.' + partner.role,
                i18nArguments: {partner: `${partner.sortNumber} ${partner.abbreviation}`}
              },
              route: `/app/project/detail/${projectId}/applicationFormPartner/detail/${partner.id}`,
            }
          ))
        )
      );

  private packages$: Observable<HeadlineRoute[]> =
    merge(this.projectStore.projectId$, this.fetchPackages$)
      .pipe(
        mergeMap(projectId => forkJoin([
            of(projectId),
            this.workPackageService.getWorkPackagesByProjectId(projectId, 0, 100, 'id,asc')
          ])
        ),
        tap(([projectId, packages]) => Log.info('Fetched the project work packages:', this, packages.content)),
        map(([projectId, packages]) => packages.content
          .map(workPackage => ({
              headline: {
                i18nKey: 'common.label.workpackage.shortcut',
                i18nArguments: {workpackage: `${workPackage.number}`},
                disabled: true
              },
              route: `/app/project/detail/${projectId}/applicationFormWorkPackage/detail/${workPackage.id}`,
            }
          ))
        )
      );

  private isNotApplicant$: Observable<boolean> = this.permissionService.permissionsChanged()
    .pipe(
      map(permissions => !permissions.includes(Permission.APPLICANT_USER))
    );

  constructor(private sideNavService: SideNavService,
              private projectPartnerService: ProjectPartnerService,
              private workPackageService: WorkPackageService,
              private projectStore: ProjectStore,
              private translate: TranslateService,
              private permissionService: PermissionService,
              private routingService: RoutingService,
              private projectVersionStore: ProjectVersionStore) {

    const headlines$ = combineLatest([
      this.isNotApplicant$,
      this.projectStore.project$,
      this.partners$,
      this.packages$,
      this.versionSelectTemplate$,
      this.projectVersionStore.currentIsLatest$
    ])
      .pipe(
        filter(([isNotApplicant, project]) => !!project),
        tap(([isNotApplicant, project, partners, packages, versionTemplate, currentVersionIsLatest]) => {
          const status = project.projectStatus.status;
          const isNotOpen = !ProjectUtil.isDraft(project)
            && status !== ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT;
          this.setHeadlines(isNotApplicant && isNotOpen, project.id, partners, packages, versionTemplate, currentVersionIsLatest);
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
                       projectId: number,
                       partners: HeadlineRoute[],
                       packages: HeadlineRoute[],
                       versionTemplate: TemplateRef<any>,
                       currentVersionIsLatest: boolean): void {
    // extracted this because Assessment is now on the same level with other headlines
    const applicationTreeHeadlines = {
      headline: {i18nKey: 'project.application.form.tree.title'},
      bullets: [
        {
          headline: {i18nKey: 'project.application.form.lifecycle.title'},
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}`,
          scrollToTop: true,
          scrollRoute: ''
        },
      ]
    };
    if (showAssessment) {
      applicationTreeHeadlines.bullets.push(
        {
          headline: {i18nKey: 'project.assessment.header'},
          scrollRoute: 'applicationFormLifecycleAssessment',
          route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}`,
          scrollToTop: false
        }
      );
    }
    applicationTreeHeadlines.bullets.push(
      {
        headline: {i18nKey: 'file.tab.header'},
        scrollRoute: 'applicationFormLifecycleAttachments',
        route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}`,
        scrollToTop: false
      }
    );
    this.sideNavService.setHeadlines(ProjectStore.PROJECT_DETAIL_PATH, [
      applicationTreeHeadlines,
      {
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
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormIdentification`,
              },
            ]
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.b'},
            disabled: !currentVersionIsLatest,
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.b.partners'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormPartner`,
                bullets: [...partners],
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.b.associatedOrganizations'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormAssociatedOrganization`,
              },
            ]
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.c'},
            disabled: !currentVersionIsLatest,
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.one'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormOverallObjective`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.two'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormRelevanceAndContext`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.three'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormPartnership`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.four'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormWorkPackage`,
                bullets: [...packages],
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.five'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormResults`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.six'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationTimePlan`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.seven'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormManagement`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.eight'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormFuturePlans`,
              }
            ],
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.d'},
            disabled: !currentVersionIsLatest,
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.d.subsection.one'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormBudgetPerPartner`,
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.d.subsection.two'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormBudget`,
              }
            ]
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.e'},
            disabled: !currentVersionIsLatest,
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.e'},
                route: `${ProjectApplicationFormSidenavService.PROJECT_DETAIL_URL}/${projectId}/applicationFormLumpSums`,
              }
            ]
          },
        ]
      },
    ]);
  }
}
