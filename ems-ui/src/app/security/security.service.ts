import {Injectable} from '@angular/core';
import {AuthenticationService, LoginRequest, OutputCurrentUser} from '@cat/api';
import {AuthenticationHolder} from './authentication-holder.service';
import {from, Observable, ReplaySubject} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable({providedIn: 'root'})
export class SecurityService {

  private myCurrentUser: ReplaySubject<OutputCurrentUser | null> = new ReplaySubject(1);
  private authenticationProblem$: ReplaySubject<I18nValidationError | null> = new ReplaySubject();

  constructor(private authenticationHolder: AuthenticationHolder,
              private authenticationService: AuthenticationService) {
  }

  get currentUser(): Observable<OutputCurrentUser | null> {
    return this.myCurrentUser
      .pipe(
        map((user) => user && user.name ? user : null)
      );
  }

  get authenticationError(): Observable<I18nValidationError | null> {
    return this.authenticationProblem$.asObservable();
  }

  isLoggedIn(): Observable<boolean> {
    return from(this.myCurrentUser)
      .pipe(map((user) => !!(user && user.name)));
  }

  login(loginRequest: LoginRequest): Observable<OutputCurrentUser | null> {
    this.authenticationProblem$.next(null);
    return this.authenticationService.login(loginRequest)
      .pipe(
        tap((user: OutputCurrentUser) => {
          this.authenticationHolder.currentUsername = user.name;
          this.myCurrentUser.next(user);
        }),
        catchError((error: HttpErrorResponse) => {
          this.authenticationProblem$.next(error.error);
          throw error;
        })
      );
  }

  reloadCurrentUser(): void {
    this.authenticationService.getCurrentUser()
      .subscribe(
        (value: OutputCurrentUser) => this.myCurrentUser.next(value),
        () => this.myCurrentUser.next(null)
      );
  }

  clearAuthentication(): void {
    this.authenticationHolder.currentUsername = null;
    this.myCurrentUser.next(null);
  }

  async logout() {
    this.clearAuthentication();
    await this.authenticationService.logout().toPromise();
  }

  newAuthenticationError(error: I18nValidationError): void {
    this.authenticationProblem$.next(error);
  }
}
