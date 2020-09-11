import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {combineLatest, Observable, Subject} from 'rxjs';
import {flatMap, map, takeUntil, tap} from 'rxjs/operators';
import {OutputProjectPartner, ProjectPartnerService, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '../../../../../common/utils/log';
import {TranslateService} from '@ngx-translate/core';

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
            headline: this.translate.instant('common.label.project.partner.role.' + partner.role)
              + ' ' + partner.name,
            route: '/project/' + this.projectId + '/partner/' + partner.id,
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
            headline: workPackage.name,
            route: '/project/' + this.projectId + '/workPackage/' + workPackage.id,
          }
        ))
      )
    );

  constructor(private sideNavService: SideNavService,
              private projectPartnerService: ProjectPartnerService,
              private workPackageService: WorkPackageService,
              private translate: TranslateService) {
  }

  init(destroyed: Subject<any>, projectId: number): void {
    this.pageDestroyed$ = destroyed;
    this.projectId = projectId;

    combineLatest([
      this.acronym$,
      this.partners$,
      this.packages$
    ])
      .pipe(
        takeUntil(destroyed),
        tap(([acronym, partners, packages]) => this.setHeadlines(acronym, projectId, partners, packages))
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

  private setHeadlines(acronym: string, projectId: number, partners: HeadlineRoute[], packages: HeadlineRoute[]): void {
    this.sideNavService.setHeadlines(this.pageDestroyed$, [
      {
        headline: 'project.application.form.tree.title',
        bullets: [
          {
            headline: 'project.application.form.lifecycle.title',
            route: '/project/' + projectId,
            scrollToTop: true,
            bullets: [
              {
                headline: 'project.assessment.header',
                scrollRoute: 'applicationFormLifecycleAssessment',
                route: '/project/' + projectId,
              },
              {
                headline: 'file.tab.header',
                scrollRoute: 'applicationFormLifecycleAttachments',
                route: '/project/' + projectId,
              },
            ]
          },
        ]
      },
      {
        headline: 'project.application.form.title',
        bullets: [
          {
            headline: 'project.application.form.section.part.a',
            route: '/project/' + projectId + '/applicationForm',
            scrollToTop: true,
            bullets: [
              {
                headline: 'project.application.form.section.part.a.subsection.one',
                scrollRoute: 'projectIdentificationHeading',
                route: '/project/' + projectId + '/applicationForm',
              },
              {
                headline: 'project.application.form.section.part.a.subsection.two',
                scrollRoute: 'projectSummaryHeading',
                route: '/project/' + projectId + '/applicationForm',
              },
            ]
          },
          {
            headline: 'project.application.form.section.part.b',
            scrollRoute: 'projectPartnersHeading',
            route: '/project/' + projectId + '/applicationForm',
            bullets: [...partners],
          },
          {
            headline: 'project.application.form.section.part.c',
            scrollRoute: 'projectDescriptionHeading',
            route: '/project/' + projectId + '/applicationForm',
            bullets: [
              {
                headline: 'project.application.form.section.part.c.subsection.four',
                scrollRoute: 'projectWorkPlanHeading',
                route: '/project/' + projectId + '/applicationForm',
                bullets: [...packages],
              },
            ],
          },
        ]
      },
    ]);
  }
}
