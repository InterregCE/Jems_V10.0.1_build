import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {Router} from '@angular/router';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideNavComponent {

  @Input()
  headlines: HeadlineRoute[];

  constructor(public sideNavService: SideNavService, private router: Router) {
  }

  currentRouteStartsWith(routeLink: string): boolean {
    return this.router.url.startsWith(routeLink);
  }
}
