import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationStore {

  private authenticationProblem$: ReplaySubject<I18nValidationError | null> = new ReplaySubject();

  getAuthenticationProblem(): Observable<I18nValidationError | null> {
    return this.authenticationProblem$.asObservable();
  }

  newAuthenticationError(error: I18nValidationError | null): void {
    this.authenticationProblem$.next(error);
  }
}
