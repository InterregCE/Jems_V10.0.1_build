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
          this.prioritiesPage,
          {
            headline: 'programme.tab.area',
            route: '/app/programme/areas',
          },
          this.indicatorsPage,
          {
            headline: 'programme.tab.strategies',
            route: '/app/programme/strategies',
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
