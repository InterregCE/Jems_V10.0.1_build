import {Injectable} from '@angular/core';
import {LoginRequest} from '@cat/api';
import {Observable, ReplaySubject, Subject} from 'rxjs';
import {catchError, take} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {SecurityService} from '../../../security/security.service';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable()
export class LoginPageService {

  private authenticationProblem$: ReplaySubject<I18nValidationError | null> = new ReplaySubject();
  private disableButton$: Subject<boolean> = new Subject<boolean>();

  constructor(private securityService: SecurityService,
              private router: Router) {
  }

  authenticationError(): Observable<I18nValidationError | null> {
    return this.authenticationProblem$.asObservable();
  }

  disableButton(): Observable<boolean> {
    return this.disableButton$.asObservable();
  }

  login(loginRequest: LoginRequest): void {
    this.disableButton$.next(true);
    this.authenticationProblem$.next(null);
    this.securityService.login(loginRequest)
      .pipe(
        take(1),
        catchError((error: HttpErrorResponse) => {
          this.authenticationProblem$.next(error.error);
          this.disableButton$.next(false);
          throw error;
        })
      )
      .subscribe(() => {
        this.disableButton$.next(false);
        this.router.navigate(['/']);
      });
  }

  newAuthenticationError(error: I18nValidationError): void {
    this.authenticationProblem$.next(error);
  }
}
