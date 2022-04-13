import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {SecurityService} from '../../security/security.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {catchError, finalize, tap} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {APIError} from '@common/models/APIError';

@UntilDestroy()
@Component({
  selector: 'jems-forgot-password-page',
  templateUrl: './forgot-password-page.component.html',
  styleUrls: ['./forgot-password-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ForgotPasswordPageComponent {

  Alert = Alert;
  requestedEmail: string;
  requestError$ = new Subject<APIError | null>();
  requestSuccess$ = new Subject<boolean>();

  loginLink = '/no-auth/login';
  loading = false;

  form = this.formBuilder.group({
    email: ['', [Validators.email, Validators.required]]
  });

  constructor(private readonly formBuilder: FormBuilder, private securityService: SecurityService) { }

  requestPasswordResetLink() {
    this.requestedEmail = this.form.get('email')?.value;
    this.loading = true;
    this.securityService.requestPasswordResetLink(this.requestedEmail)
      .pipe(
        tap(() => {
          this.requestSuccess$.next(true);
          this.requestError$.next(null);
        }),
        catchError(error => {
          this.requestSuccess$.next(false);
          this.requestError$.next(error.error);
          throw error.error;
        }),
        finalize(() => {
          this.loading = false;
        }),
        untilDestroyed(this)
      )
      .subscribe();
  }

}
