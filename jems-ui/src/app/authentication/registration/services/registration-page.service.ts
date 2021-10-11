import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {UserRegistrationService, UserRegistrationDTO} from '@cat/api';
import {Router} from '@angular/router';
import {AuthenticationStore} from '../../service/authentication-store.service';


@Injectable()
export class RegistrationPageService {
  private userSaveError$ = new Subject<I18nValidationError | null>();
  private userSaveSuccess$ = new Subject<boolean>();
  private confirmationSuccess$ = new Subject<boolean>();

  constructor(private userRegistrationService: UserRegistrationService,
              private authenticationStore: AuthenticationStore,
              private router: Router) {
  }

  saveError(): Observable<I18nValidationError | null> {
    return this.userSaveError$.asObservable();
  }

  saveSuccess(): Observable<boolean> {
    return this.userSaveSuccess$.asObservable();
  }

  confirmationSuccess(): Observable<boolean> {
    return this.confirmationSuccess$.asObservable();
  }

  registerApplicant(applicant: UserRegistrationDTO): void {
    this.userRegistrationService.registerApplicant(applicant).pipe(
      tap(() => this.userSaveSuccess$.next(true)),
      catchError((error: HttpErrorResponse) => {
        this.userSaveError$.next(error.error);
        throw error;
      })
    ).subscribe(() => this.userSaveError$.next(null));
  }

  redirectToLogin(): void {
    this.userSaveSuccess$.next(false);
    this.router.navigate(['login']);
  }

  confirmRegistration(token: string): void {
    this.userRegistrationService.confirmUserRegistration(token).pipe(
      tap(() => this.confirmationSuccess$.next(true)),
      catchError((error: HttpErrorResponse) => {
        this.authenticationStore.newAuthenticationError(error.error);
        this.router.navigate(['login']);
        throw error;
      })
    ).subscribe();
  }
}
