import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {UserRegistrationService, InputUserRegistration} from '@cat/api';
import {Router} from '@angular/router';


@Injectable()
export class RegistrationPageService {
  private userSaveError$ = new Subject<I18nValidationError | null>();
  private userSaveSuccess$ = new Subject<boolean>();
  private disableButton$ = new Subject<boolean>();

  constructor(private userRegistrationService: UserRegistrationService,
              private router: Router) {
  }

  saveError(): Observable<I18nValidationError | null> {
    return this.userSaveError$.asObservable();
  }

  saveSuccess(): Observable<boolean> {
    return this.userSaveSuccess$.asObservable();
  }

  disableButton(): Observable<boolean> {
    return this.disableButton$.asObservable();
  }

  registerApplicant(applicant: InputUserRegistration): void {
    this.disableButton$.next(true);
    this.userRegistrationService.registerApplicant(applicant).pipe(
      tap(() => this.userSaveSuccess$.next(true)),
      tap(() => this.disableButton$.next(false)),
      catchError((error: HttpErrorResponse) => {
        this.userSaveError$.next(error.error);
        this.disableButton$.next(false);
        throw error;
      })
    ).subscribe(() => this.userSaveError$.next(null));
  }

  redirectToLogin(): void {
    this.userSaveSuccess$.next(false);
    this.router.navigate(['login'])
  }
}
