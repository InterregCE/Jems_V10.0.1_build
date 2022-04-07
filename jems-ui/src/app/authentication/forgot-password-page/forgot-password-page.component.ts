import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {SecurityService} from '../../security/security.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import { Alert } from '@common/components/forms/alert';
import {finalize, tap} from 'rxjs/operators';
import {BehaviorSubject} from 'rxjs';

@UntilDestroy()
@Component({
  selector: 'jems-forgot-password-page',
  templateUrl: './forgot-password-page.component.html',
  styleUrls: ['./forgot-password-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ForgotPasswordPageComponent {

  Alert = Alert;
  requestSent$ = new BehaviorSubject(false);
  loginLink = '/no-auth/login';
  loading = false;

  form = this.formBuilder.group({
    email: ['', [Validators.email, Validators.required]]
  });

  constructor(private readonly formBuilder: FormBuilder, private securityService: SecurityService) { }

  requestPasswordResetLink() {
    this.loading = true;
    this.securityService.requestPasswordResetLink(this.email)
      .pipe(
        tap(() => this.requestSent$.next(true)),
        finalize(() => this.loading = false),
        untilDestroyed(this)
      )
      .subscribe();
  }

  get email(): string {
    return this.form.get('email')?.value as string;
  }
}
