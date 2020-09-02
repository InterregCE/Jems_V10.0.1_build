import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineType} from '@common/components/side-nav/headline-type';
import {combineLatest, Subject} from 'rxjs';
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

  private partners$ = this.fetchPartners$
    .pipe(
      flatMap(() =>
        this.projectPartnerService.getProjectPartners(this.projectId, 0, 100, 'sortNumber,asc')
      ),
      map(page => page.content),
      tap(partners => Log.info('Fetched the project partners:', this, partners)),
      map(partners => partners
        .sort((a, b) => a.role === OutputProjectPartner.RoleEnum.LEADPARTNER ? -1 : 1)
        .map(partner => ({
            headline: this.translate.instant('common.label.project.partner.role.' + partner.role)
              + ' ' + partner.name,
            route: '/project/' + this.projectId + '/partner/' + partner.id,
            paddingLeft: 20,
            paddingTop: 3
          }
        ))
      )
    );

  private packages$ = this.fetchPackages$
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
            paddingLeft: 30,
            paddingTop: 3
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
        headline: 'back.project.overview',
        route: '/project/' + projectId,
        type: HeadlineType.BACKROUTE,
        paddingLeft: 30
      },
      {
        headline: 'project.application.form.title',
        type: HeadlineType.TITLE
      },
      {
        headline: projectId + ' ' + acronym,
        type: HeadlineType.SUBTITLE
      },
      {
        headline: 'project.application.form.section.part.a',
        scrollRoute: 'applicationFormHeading',
        route: '/project/' + projectId + '/applicationForm',
        fontSize: 'large',
        paddingTop: 20
      },
      {
        headline: 'project.application.form.section.part.a.subsection.one',
        scrollRoute: 'projectIdentificationHeading',
        route: '/project/' + projectId + '/applicationForm',
        paddingLeft: 10,
        paddingTop: 3
      },
      {
        headline: 'project.application.form.section.part.a.subsection.two',
        scrollRoute: 'projectSummaryHeading',
        route: '/project/' + projectId + '/applicationForm',
        paddingLeft: 10,
        paddingTop: 3
      },
      {
        headline: 'project.application.form.section.part.b',
        scrollRoute: 'projectPartnersHeading',
        route: '/project/' + projectId + '/applicationForm',
        fontSize: 'large',
        paddingTop: 20
      },
      ...partners,
      {
        headline: 'project.application.form.section.part.c',
        scrollRoute: 'projectDescriptionHeading',
        route: '/project/' + projectId + '/applicationForm',
        fontSize: 'large',
        paddingTop: 20
      },
      {
        headline: 'project.application.form.section.part.c.subsection.four',
        scrollRoute: 'projectWorkPlanHeading',
        route: '/project/' + projectId + '/applicationForm',
        paddingLeft: 10,
        paddingTop: 3
      },
      ...packages
    ]);
  }
}
