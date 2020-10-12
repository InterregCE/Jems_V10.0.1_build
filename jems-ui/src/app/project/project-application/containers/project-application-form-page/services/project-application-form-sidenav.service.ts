import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {combineLatest, Observable, Subject} from 'rxjs';
import {flatMap, map, takeUntil, tap} from 'rxjs/operators';
import {OutputProjectPartner, OutputProjectStatus, ProjectPartnerService, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '../../../../../common/utils/log';
import {TranslateService} from '@ngx-translate/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';

@Injectable()
export class ProjectApplicationFormSidenavService {
  private pageDestroyed$: Subject<any>;
  private acronym$ = new Subject<string>();
  private projectId: number;
  private fetchPartners$ = new Subject<void>();
  private fetchPackages$ = new Subject<void>();

  private partners$: Observable<HeadlineRoute[]> = this.fetchPartners$
    .pipe(
      flatMap(() =>
        this.projectPartnerService.getProjectPartners(this.projectId, 0, 100, ['role,asc', 'sortNumber,asc'])
      ),
      map(page => page.content),
      tap(partners => Log.info('Fetched the project partners:', this, partners)),
      map(partners => partners
        .sort((a, b) => a.role === OutputProjectPartner.RoleEnum.LEADPARTNER ? -1 : 1)
        .map(partner => ({
            headline: {
              i18nKey: 'common.label.project.partner.role.shortcut.' + partner.role,
              i18nArguments: {partner: `${partner.sortNumber} ${partner.name}`}
            },
            route: `/app/project/detail/${this.projectId}/applicationForm/partner/detail/${partner.id}`,
          }
        ))
      )
    );

  private packages$: Observable<HeadlineRoute[]> = this.fetchPackages$
    .pipe(
      flatMap(() =>
        this.workPackageService.getWorkPackagesByProjectId(this.projectId, 0, 100, 'id,asc')
      ),
      map(page => page.content),
      tap(packages => Log.info('Fetched the project work packages:', this, packages)),
      map(packages => packages
        .map(workPackage => ({
            headline: {
              i18nKey:  'common.label.workpackage.shortcut',
              i18nArguments: {workpackage: `${workPackage.number}`},
            },
            route: `/app/project/detail/${this.projectId}/applicationForm/workPackage/detail/${workPackage.id}`,
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
  }

  init(destroyed: Subject<any>, projectId: number): void {
    this.pageDestroyed$ = destroyed;
    this.projectId = projectId;

    combineLatest([
      this.isNotApplicant$,
      this.partners$,
      this.packages$,
      this.projectStore.getProject()
    ])
      .pipe(
        takeUntil(destroyed),
        tap(([isNotApplicant, partners, packages, project]) => {
          const status = project.projectStatus.status;
          const isNotOpen = status !== OutputProjectStatus.StatusEnum.DRAFT
            && status !== OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT;
          this.setHeadlines(isNotApplicant && isNotOpen, projectId, partners, packages)
        })
      ).subscribe();

    this.refreshPartners();
    this.refreshPackages();
  }

  setAcronym(acronym: string): void {
    this.acronym$.next(acronym);
  }

  refreshPartners(): void {
    this.fetchPartners$.next();
  }

  refreshPackages(): void {
    this.fetchPackages$.next();
  }

  private setHeadlines(showAssessment: boolean, projectId: number, partners: HeadlineRoute[], packages: HeadlineRoute[]): void {
    // extracted this because Assessment is now on the same level with other headlines
    const applicationTreeHeadlines = {
      headline: { i18nKey: 'project.application.form.tree.title'},
      bullets: [
        {
          headline: { i18nKey: 'project.application.form.lifecycle.title'},
            route: '/app/project/detail/' + projectId,
          scrollToTop: true,
          scrollRoute: ''
        },
      ]
    };
    if (showAssessment){
      applicationTreeHeadlines.bullets.push(
        {
          headline: { i18nKey: 'project.assessment.header'},
          scrollRoute: 'applicationFormLifecycleAssessment',
          route: '/app/project/detail/' + projectId,
          scrollToTop: false
        }
      );
    }
    applicationTreeHeadlines.bullets.push(
      {
        headline: { i18nKey: 'file.tab.header'},
        scrollRoute: 'applicationFormLifecycleAttachments',
        route: '/app/project/detail/' + projectId,
        scrollToTop: false
      }
    );
    this.sideNavService.setHeadlines(this.pageDestroyed$, [
      applicationTreeHeadlines,
      {
        headline: { i18nKey: 'project.application.form.title'},
        bullets: [
          {
            headline: { i18nKey: 'project.application.form.section.part.a'},
            route: '/app/project/detail/' + projectId + '/applicationForm',
            scrollToTop: true,
            bullets: [
              {
                headline: { i18nKey: 'project.application.form.section.part.a.subsection.one'},
                scrollRoute: 'projectIdentificationHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
              },
              {
                headline: { i18nKey: 'project.application.form.section.part.a.subsection.two'},
                scrollRoute: 'projectSummaryHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
              },
            ]
          },
          {
            headline: { i18nKey: 'project.application.form.section.part.b'},
            scrollRoute: 'projectPartnersHeading',
            route: '/app/project/detail/' + projectId + '/applicationForm',
            bullets: [...partners],
          },
          {
            headline: { i18nKey: 'project.application.form.section.part.c'},
            scrollRoute: 'projectDescriptionHeading',
            route: '/app/project/detail/' + projectId + '/applicationForm',
            bullets: [
              {
                headline: { i18nKey: 'project.application.form.section.part.c.subsection.one'},
                scrollRoute: 'projectOverallObjectiveHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
              },
              {
                headline: { i18nKey: 'project.application.form.section.part.c.subsection.two'},
                scrollRoute: 'projectRelevanceHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
              },
              {
                headline: { i18nKey: 'project.application.form.section.part.c.subsection.three'},
                scrollRoute: 'projectPartnershipHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
              },
              {
                headline: { i18nKey: 'project.application.form.section.part.c.subsection.four'},
                scrollRoute: 'projectWorkPlanHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
                bullets: [...packages],
              },
              {
                headline: { i18nKey: 'project.application.form.section.part.c.subsection.seven'},
                scrollRoute: 'projectManagementHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
              },
              {
                headline: { i18nKey: 'project.application.form.section.part.c.subsection.eight'},
                scrollRoute: 'projectFuturePlansHeading',
                route: '/app/project/detail/' + projectId + '/applicationForm',
              }
            ],
          },
        ]
      },
    ]);
  }
}
