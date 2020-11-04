import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {Subject} from 'rxjs';

@Injectable()
export class ProgrammePageSidenavService {
  public static PROGRAMME_DETAIL_PATH = '/app/programme';

  private indicatorsPage = {
    headline: {i18nKey: 'programme.tab.indicators'},
    route: '/app/programme/indicators'
  };

  private prioritiesPage = {
    headline: {i18nKey: 'programme.tab.priority'},
    route: '/app/programme/priorities',
  };

  private languagesPage = {
    headline: {i18nKey: 'programme.tab.languages'},
    route: '/app/programme/languages',
  };

  constructor(private sideNavService: SideNavService) {
  }

  public init(destroyed$: Subject<any>): void {
    this.sideNavService.setHeadlines(ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH, [
      {
        headline: {i18nKey: 'programme.data.page.title'},
        bullets: [
          {
            headline: {i18nKey: 'programme.tab.data'},
            route: '/app/programme',
            scrollToTop: true
          },
          {
            headline: {i18nKey: 'programme.fund.list.title'},
            scrollRoute: 'funds',
            route: '/app/programme',
          },
          this.languagesPage,
          this.prioritiesPage,
          {
            headline: {i18nKey: 'programme.tab.area'},
            route: '/app/programme/areas',
          },
          this.indicatorsPage,
          {
            headline: {i18nKey: 'programme.tab.strategies'},
            route: '/app/programme/strategies',
          },
          {
            headline: {i18nKey: 'programme.tab.legal.status'},
            route: '/app/programme/legalStatus',
          }
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
}
