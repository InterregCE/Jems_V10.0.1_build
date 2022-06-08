import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SecurityService} from '../../security/security.service';
import {ActivatedRoute} from '@angular/router';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, Subject} from 'rxjs';
import {catchError, finalize, tap} from 'rxjs/operators';
import {AbstractFormComponent} from '@common/components/forms/abstract-form.component';
import {TranslateService} from '@ngx-translate/core';
import {APIError} from '@common/models/APIError';

@UntilDestroy()
@Component({
  selector: 'jems-reset-password-page',
  templateUrl: './reset-password-page.component.html',
  styleUrls: ['./reset-password-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResetPasswordPageComponent extends AbstractFormComponent{

  Alert = Alert;
  email = this.activatedRoute?.snapshot?.queryParams?.email;
  token = this.activatedRoute?.snapshot?.queryParams?.token;
  loading = false;

  loginLink = '/no-auth/login';
  passwordResetSuccessfully$ = new BehaviorSubject(false);
  hide = true;

  form = this.formBuilder.group({
    password: ['', Validators.required]
  });

  constructor(
    protected changeDetectorRef: ChangeDetectorRef, protected translateService: TranslateService,
    private activatedRoute: ActivatedRoute, private formBuilder: FormBuilder, private securityService: SecurityService
  ) {
    super(changeDetectorRef, translateService);
    this.error$ = new Subject<APIError>();
  }

  resetPassword() {
    this.loading = true;
    this.securityService.resetPasswordByToken(this.token, this.password)
      .pipe(
        tap(() => this.passwordResetSuccessfully$.next(true)),
        catchError(error => {
          (this.error$ as Subject<APIError>).next(error.error);
           throw error;
        }),
        finalize(() => this.loading = false),
        untilDestroyed(this)
      ).subscribe();
  }

  get password(): string {
    return this.form.get('password')?.value as string;
  }

  getForm(): FormGroup | null {
    return this.form;
  }
}
