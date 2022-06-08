import {ChangeDetectionStrategy, Component} from '@angular/core';
import {SecurityService} from '../../security/security.service';

@Component({
  selector: 'jems-no-auth-wrap',
  templateUrl: './no-auth-wrap.component.html',
  styleUrls: ['./no-auth-wrap.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NoAuthWrapComponent {

  constructor(public securityService: SecurityService) {
  }
}
