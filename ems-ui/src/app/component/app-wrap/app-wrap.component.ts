import {BaseComponent} from '../../common/components/base-component';
import {Component} from '@angular/core';
import {SideNavService} from '../../common/components/side-nav/side-nav.service';

@Component({
  selector: 'app-wrap',
  templateUrl: './app-wrap.component.html',
  styleUrls: ['./app-wrap.component.scss'],
})
export class AppWrapComponent extends BaseComponent {

  headlines$ = this.sideNavService.getHeadlines();

  constructor(public sideNavService: SideNavService) {
    super();
  }

}
