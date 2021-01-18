import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {mergeMap, map, tap} from 'rxjs/operators';
import {OutputProjectStatus, ProjectPartnerService, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '../../../../../common/utils/log';
import {TranslateService} from '@ngx-translate/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';

@Injectable()
export class ProjectApplicationFormSidenavService {

  private projectId$ = this.projectStore.getProject()
    .pipe(
      map(project => project.id)
    );
  private fetchPartners$ = new Subject<number>();
  private fetchPackages$ = new Subject<number>();

  private partners$: Observable<HeadlineRoute[]> =
    merge(this.projectId$, this.fetchPartners$)
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
    merge(this.projectId$, this.fetchPackages$)
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
              private permissionService: PermissionService) {
    combineLatest([
      this.isNotApplicant$,
      this.projectStore.getProject(),
      this.partners$,
      this.packages$,
    ])
      .pipe(
        tap(([isNotApplicant, project, partners, packages]) => {
          if (!project) {
            return;
          }
          const status = project.projectStatus.status;
          const isNotOpen = status !== OutputProjectStatus.StatusEnum.DRAFT
            && status !== OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT;
          this.setHeadlines(isNotApplicant && isNotOpen, project.id, partners, packages);
        })
      ).subscribe();
  }

  refreshPartners(projectId: number): void {
    this.fetchPartners$.next(projectId);
  }

  refreshPackages(projectId: number): void {
    this.fetchPackages$.next(projectId);
  }

  private setHeadlines(showAssessment: boolean, projectId: number, partners: HeadlineRoute[], packages: HeadlineRoute[]): void {
    // extracted this because Assessment is now on the same level with other headlines
    const applicationTreeHeadlines = {
      headline: {i18nKey: 'project.application.form.tree.title'},
      bullets: [
        {
          headline: {i18nKey: 'project.application.form.lifecycle.title'},
          route: '/app/project/detail/' + projectId,
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
          route: '/app/project/detail/' + projectId,
          scrollToTop: false
        }
      );
    }
    applicationTreeHeadlines.bullets.push(
      {
        headline: {i18nKey: 'file.tab.header'},
        scrollRoute: 'applicationFormLifecycleAttachments',
        route: '/app/project/detail/' + projectId,
        scrollToTop: false
      }
    );
    this.sideNavService.setHeadlines(ProjectStore.PROJECT_DETAIL_PATH, [
      applicationTreeHeadlines,
      {
        headline: {i18nKey: 'project.application.form.title'},
        bullets: [
          {
            headline: {i18nKey: 'project.application.form.section.part.a'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.a'},
                route: '/app/project/detail/' + projectId + '/applicationFormIdentification',
              },
            ]
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.b'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.b.partners'},
                route: '/app/project/detail/' + projectId + '/applicationFormPartner',
                bullets: [...partners],
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.b.associatedOrganizations'},
                route: '/app/project/detail/' + projectId + '/applicationFormAssociatedOrganization',
              },
            ]
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.c'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.one'},
                route: '/app/project/detail/' + projectId + '/applicationFormOverallObjective',
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.two'},
                route: '/app/project/detail/' + projectId + '/applicationFormRelevanceAndContext',
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.three'},
                route: '/app/project/detail/' + projectId + '/applicationFormPartnership',
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.four'},
                route: '/app/project/detail/' + projectId + '/applicationFormWorkPackage',
                bullets: [...packages],
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.five'},
                route: '/app/project/detail/' + projectId + '/applicationFormResults',
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.seven'},
                route: '/app/project/detail/' + projectId + '/applicationFormManagement',
              },
              {
                headline: {i18nKey: 'project.application.form.section.part.c.subsection.eight'},
                route: '/app/project/detail/' + projectId + '/applicationFormFuturePlans',
              }
            ],
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.d'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.d.subsection.two'},
                route: '/app/project/detail/' + projectId + '/applicationFormBudget',
              }
            ]
          },
          {
            headline: {i18nKey: 'project.application.form.section.part.e'},
            bullets: [
              {
                headline: {i18nKey: 'project.application.form.section.part.e'},
                route: '/app/project/detail/' + projectId + '/applicationFormLumpSums',
              }
            ]
          },
        ]
      },
    ]);
  }
}
