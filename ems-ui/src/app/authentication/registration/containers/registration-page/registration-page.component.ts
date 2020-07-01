import { Component } from '@angular/core';
import {Observable} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {InputUserRegistration} from '@cat/api';
import {RegistrationPageService} from '../../services/registration-page.service';

@Component({
  selector: 'app-registration-page',
  templateUrl: './registration-page.component.html',
  styleUrls: ['./registration-page.component.scss']
})
export class RegistrationPageComponent{

  saveSuccess$: Observable<boolean>;
  saveError$: Observable<I18nValidationError | null>;
  disableButton$: Observable<boolean>;

  constructor(private userRegistrationService: RegistrationPageService) {
    this.saveSuccess$ = this.userRegistrationService.saveSuccess();
    this.saveError$ = this.userRegistrationService.saveError();
    this.disableButton$ = this.userRegistrationService.disableButton();
  }

  registerApplicant(user: InputUserRegistration): void {
    this.userRegistrationService.registerApplicant(user);
  }

  redirectToLogin(): void {
    this.userRegistrationService.redirectToLogin();
  }
}
