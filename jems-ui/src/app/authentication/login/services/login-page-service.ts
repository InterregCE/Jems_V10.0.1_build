import {Injectable} from '@angular/core';
import {LoginRequest} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {SecurityService} from '../../../security/security.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';

@Injectable({providedIn: 'root'})
export class LoginPageService extends BaseComponent {

  private authenticationProblem$: ReplaySubject<I18nValidationError | null> = new ReplaySubject();

  constructor(private securityService: SecurityService,
              private _route: ActivatedRoute,
              private _router: Router) {
    super();
  }

  authenticationError(): Observable<I18nValidationError | null> {
    return this.authenticationProblem$.asObservable();
  }

  login(loginRequest: LoginRequest): void {

    const queryRef = this._route.snapshot.queryParamMap.get('ref')
    const redirectTo = queryRef ? [queryRef] : ['app']

    this.securityService.login(loginRequest)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.authenticationProblem$.next(null)),
        tap(() => this._router.navigate(redirectTo)),
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
