import {Injectable} from '@angular/core';
import {LoginRequest} from '@cat/api';
import {combineLatest, Observable, ReplaySubject} from 'rxjs';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {SecurityService} from '../../../security/security.service';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';
import {Permission} from '../../../security/permissions/permission';

@Injectable()
export class LoginPageService extends BaseComponent {

  private authenticationProblem$: ReplaySubject<I18nValidationError | null> = new ReplaySubject();

  constructor(private securityService: SecurityService,
              private permissionService: PermissionService,
              private router: Router) {
    super();
  }

  authenticationError(): Observable<I18nValidationError | null> {
    return this.authenticationProblem$.asObservable();
  }

  login(loginRequest: LoginRequest): void {
    combineLatest([
      this.securityService.login(loginRequest),
      this.permissionService.permissionsChanged()
    ])
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.authenticationProblem$.next(null)),
        tap(([user, permissions]) => this.router.navigate(
          permissions[0] === Permission.APPLICANT_USER ? ['/calls'] : [''])),
        catchError((error: HttpErrorResponse) => {
          this.authenticationProblem$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }

  newAuthenticationError(error: I18nValidationError): void {
    this.authenticationProblem$.next(error);
  }
}
