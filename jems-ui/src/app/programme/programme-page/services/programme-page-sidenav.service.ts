import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {Subject} from 'rxjs';

@Injectable()
export class ProgrammePageSidenavService {

  private indicatorsPage = {
    headline: 'programme.tab.indicators',
    route: '/app/programme/indicators'
  };

  private prioritiesPage = {
    headline: 'programme.tab.priority',
    route: '/app/programme/priorities',
  };

  private languagesPage = {
    headline: 'programme.tab.languages',
    route: '/app/programme/languages',
  };

  constructor(private sideNavService: SideNavService) {
  }

  public init(destroyed$: Subject<any>): void {
    this.sideNavService.setHeadlines(destroyed$, [
      {
        headline: 'programme.data.page.title',
        bullets: [
          {
            headline: 'programme.tab.data',
            route: '/app/programme',
            scrollToTop: true
          },
          {
            headline: 'programme.fund.list.title',
            scrollRoute: 'funds',
            route: '/app/programme',
          },
          this.languagesPage,
          this.prioritiesPage,
          {
            headline: 'programme.tab.area',
            route: '/app/programme/areas',
          },
          this.indicatorsPage,
          {
            headline: 'programme.tab.strategies',
            route: '/app/programme/strategies',
          },
          {
            headline: 'programme.tab.legal.status',
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
