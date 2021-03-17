import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {RoutingService} from '../../../common/services/routing.service';

@UntilDestroy()
@Injectable()
export class ProgrammePageSidenavService {
  public static PROGRAMME_DETAIL_PATH = '/app/programme';

  private indicatorsPage = {
    headline: {i18nKey: 'programme.tab.indicators'},
    route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/indicators`
  };

  private prioritiesPage = {
    headline: {i18nKey: 'programme.tab.priority'},
    route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/priorities`,
  };

  private languagesPage = {
    headline: {i18nKey: 'programme.tab.languages'},
    route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/languages`,
  };

  private costsPage = {
    headline: {i18nKey: 'programme.tab.costs.option'},
    route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/costs`
  };

  constructor(private sideNavService: SideNavService,
              private routingService: RoutingService) {
    this.routingService.routeChanges(ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH)
      .pipe(
        filter(programmePath => programmePath),
        tap(() => this.init()),
        untilDestroyed(this)
      ).subscribe();
  }

  private init(): void {
    this.sideNavService.setHeadlines(ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH, [
      {
        headline: {i18nKey: 'programme.data.page.title'},
        bullets: [
          this.languagesPage,
          {
            headline: {i18nKey: 'programme.tab.area'},
            route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/areas`,
          },
          {
            headline: {i18nKey: 'programme.tab.data'},
            route: ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH,
            scrollToTop: true
          },
          {
            headline: {i18nKey: 'programme.fund.list.title'},
            scrollRoute: 'funds',
            route: ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH,
          },
          this.prioritiesPage,
          this.indicatorsPage,
          {
            headline: {i18nKey: 'programme.tab.strategies'},
            route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/strategies`,
          },
          {
            headline: {i18nKey: 'programme.tab.legal.status'},
            route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/legalStatus`,
          },
          this.costsPage
        ]
      },
    ]);
  }

  public goToIndicators(): void {
    this.sideNavService.navigate(this.indicatorsPage);
  }

  public goToPriorities(): void {
    this.sideNavService.navigate(this.prioritiesPage);
  }

  public goToLanguages(): void {
    this.sideNavService.navigate(this.languagesPage);
  }

  public goToCosts(): void {
    this.sideNavService.navigate(this.costsPage);
  }
}
