import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';

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

  constructor(private sideNavService: SideNavService) {
    this.init();
  }

  private init(): void {
    this.sideNavService.setHeadlines(ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH, [
      {
        headline: {i18nKey: 'programme.data.page.title'},
        bullets: [
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
          this.languagesPage,
          this.prioritiesPage,
          {
            headline: {i18nKey: 'programme.tab.area'},
            route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/areas`,
          },
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
