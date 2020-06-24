import {Injectable} from '@angular/core';
import {LoginRequest} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';
import {catchError, take} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {SecurityService} from '../../../security/security.service';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable()
export class LoginPageService {

  private authenticationProblem$: ReplaySubject<I18nValidationError | null> = new ReplaySubject();

  constructor(private securityService: SecurityService,
              private router:Router) {}

  get authenticationError(): Observable<I18nValidationError | null> {
    return this.authenticationProblem$.asObservable();
  }

  login(loginRequest:LoginRequest): void {
    this.authenticationProblem$.next(null);
    this.securityService.login(loginRequest)
      .pipe(
        take(1),
        catchError((error: HttpErrorResponse) => {
          this.authenticationProblem$.next(error.error);
          throw error;
        })
      )
      .subscribe(() => this.router.navigate(['/']));
  }

  newAuthenticationError(error: I18nValidationError): void {
    this.authenticationProblem$.next(error);
  }
}
