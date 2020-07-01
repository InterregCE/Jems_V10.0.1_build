import {Injectable} from '@angular/core';
import {InputUserCreate, InputUserUpdate, OutputUser, UserService} from '@cat/api';
import {Observable, Subject} from 'rxjs';
import {catchError, take, tap} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable()
export class UserDetailService {

  private userSaveError$ = new Subject<I18nValidationError | null>();
  private userSaveSuccess$ = new Subject<boolean>();
  private disableSaveButton$ = new Subject<boolean>();
  private disableEditButton$ = new Subject<boolean>();

  constructor(private userService: UserService) {
  }

  getById(id: number): Observable<OutputUser> {
    return this.userService.getById(id);
  }

  saveError(): Observable<I18nValidationError | null> {
    return this.userSaveError$.asObservable();
  }

  saveSuccess(): Observable<boolean> {
    return this.userSaveSuccess$.asObservable();
  }

  disableSaveButton(): Observable<boolean> {
    return this.disableSaveButton$.asObservable();
  }

  disableEditButton(): Observable<boolean> {
    return this.disableEditButton$.asObservable();
  }

  createUser(user: InputUserCreate): void {
    this.disableSaveButton$.next(true);
    this.userService.createUser(user)
      .pipe(
        take(1),
        tap(() => this.userSaveSuccess$.next(true)),
        tap(() => this.disableSaveButton$.next(false)),
        tap(saved => console.log('Created user:', saved)),
        catchError((error: HttpErrorResponse) => {
          this.userSaveError$.next(error.error);
          this.disableSaveButton$.next(false);
          throw error;
        })
      )
      .subscribe(() => this.userSaveError$.next(null));
  }

  updateUser(user: InputUserUpdate): void {
    this.disableEditButton$.next(true);
    this.userService.update(user)
      .pipe(
        take(1),
        tap(() => this.userSaveSuccess$.next(true)),
        tap(() => this.disableEditButton$.next(false)),
        tap(saved => console.log('Updated user:', saved)),
        catchError((error: HttpErrorResponse) => {
          this.userSaveError$.next(error.error);
          this.disableEditButton$.next(false);
          throw error;
        })
      )
      .subscribe(() => this.userSaveError$.next(null));
  }
}
