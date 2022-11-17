import {ChangeDetectionStrategy, Component} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {SecurityService} from '../../security/security.service';

@Component({
  selector: 'jems-wrap',
  templateUrl: './app-wrap.component.html',
  styleUrls: ['./app-wrap.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppWrapComponent {

  headlines$ = this.sideNavService.getHeadlines();

  constructor(public sideNavService: SideNavService,
              public securityService: SecurityService) {
  }
  dispatchResizeEvent(): void{
    window.dispatchEvent(new Event('resize'));
  }
}
