import {BaseComponent} from '../../common/components/base-component';
import {Component} from '@angular/core';
import {SideNavService} from '../../common/components/side-nav/side-nav.service';

@Component({
  selector: 'app-no-auth-wrap',
  templateUrl: './no-auth-wrap.component.html',
  styleUrls: ['./no-auth-wrap.component.scss'],
})
export class NoAuthWrapComponent extends BaseComponent {
}
