import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineType} from '@common/components/side-nav/headline-type';
import {combineLatest, Observable, Subject} from 'rxjs';
import {map, takeUntil, tap} from 'rxjs/operators';
import {OutputProjectPartner, ProjectPartnerService, WorkPackageService} from '@cat/api';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Log} from '../../../../../common/utils/log';

@Injectable()
export class ProjectApplicationFormSidenavService {
  private pageDestroyed$: Subject<any>;
  private acronym$ = new Subject<string>();

  constructor(private sideNavService: SideNavService,
              private projectPartnerService: ProjectPartnerService,
              private workPackageService: WorkPackageService) {
  }

  init(destroyed: Subject<any>, projectId: number): void {
    this.pageDestroyed$ = destroyed;

    combineLatest([
      this.acronym$,
      this.getPartners(projectId),
      this.getWorkPackages(projectId)
    ])
      .pipe(
        takeUntil(destroyed),
        tap(([acronym, partners, packages]) => this.setHeadlines(acronym, projectId, partners, packages))
      ).subscribe();
  }

  setAcronym(acronym: string): void {
    this.acronym$.next(acronym);
  }

  private getPartners(projectId: number): Observable<HeadlineRoute[]> {
    return this.projectPartnerService.getProjectPartners(projectId, 0, 100, 'sortNumber,asc')
      .pipe(
        map(page => page.content),
        tap(partners => Log.info('Fetched the project partners:', this, partners)),
        map(partners => partners
          .sort((a, b) => a.role === OutputProjectPartner.RoleEnum.LEADPARTNER ? -1 : 1)
          .map(partner => ({
              headline: partner.name,
              route: '/project/' + projectId + '/partner/' + partner.id,
              paddingLeft: 20,
              paddingTop: 3
            }
          ))
        )
      );
  }

  private getWorkPackages(projectId: number): Observable<HeadlineRoute[]> {
    return this.workPackageService.getWorkPackagesByProjectId(projectId, 0, 100, 'id,asc')
      .pipe(
        map(page => page.content),
        tap(packages => Log.info('Fetched the project work packages:', this, packages)),
        map(packages => packages
          .map(workPackage => ({
              headline: workPackage.name,
              route: '/project/' + projectId + '/workPackage/' + workPackage.id,
              paddingLeft: 30,
              paddingTop: 3
            }
          ))
        )
      );
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
