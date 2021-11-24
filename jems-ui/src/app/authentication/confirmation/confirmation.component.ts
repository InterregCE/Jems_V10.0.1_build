import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {RegistrationPageService} from '../registration/services/registration-page.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfirmationComponent implements OnInit {

  token = this.router.getParameter(this.activatedRoute, 'token');
  confirmationSuccess$: Observable<boolean>;

  constructor(public userRegistrationService: RegistrationPageService,
              private router: RoutingService,
              private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.confirmationSuccess$ = this.userRegistrationService.confirmationSuccess();
    this.userRegistrationService.confirmRegistration(this.token ? this.token.toString() : '');
  }

  redirectToLogin(): void {
    this.userRegistrationService.redirectToLogin();
  }
}
