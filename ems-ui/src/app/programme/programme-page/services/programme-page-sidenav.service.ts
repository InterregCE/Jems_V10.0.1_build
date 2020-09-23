import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {Subject} from 'rxjs';

@Injectable()
export class ProgrammePageSidenavService {

  private indicatorsPage = {
    headline: 'programme.tab.indicators',
    route: 'programme/indicators',
    bullets: [
      {
        headline: 'output.indicator.table.title',
        scrollRoute: 'outputIndicators',
        route: 'programme/indicators',
      },
      {
        headline: 'result.indicator.table.title',
        scrollRoute: 'resultIndicators',
        route: 'programme/indicators',
      },
    ]
  };

  private prioritiesPage = {
    headline: 'programme.tab.priority',
    route: 'programme/priorities',
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
            route: '/programme',
            scrollToTop: true,
            bullets: [
              {
                headline: 'programme.data.header',
                scrollRoute: 'basicData',
                route: '/programme',
              },
              {
                headline: 'programme.fund.list.title',
                scrollRoute: 'funds',
                route: '/programme',
              },
            ]
          },
          this.prioritiesPage,
          {
            headline: 'programme.tab.area',
            route: 'programme/areas',
          },
          this.indicatorsPage,
          {
            headline: 'programme.tab.strategies',
            route: 'programme/strategies',
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
}
