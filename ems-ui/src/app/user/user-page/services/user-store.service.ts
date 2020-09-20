import {Injectable} from '@angular/core';
import {merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {InputUserUpdate, OutputUserWithRole, UserService} from '@cat/api';
import {catchError, distinctUntilChanged, flatMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable()
export class UserStore {
  private userId$ = new ReplaySubject<number>(1);
  private userName$ = new ReplaySubject<string>(1);

  userSaveError$ = new Subject<I18nValidationError | null>();
  userSaveSuccess$ = new Subject<boolean>();
  saveUser$ = new Subject<InputUserUpdate>();

  private userById$ = this.userId$
    .pipe(
      distinctUntilChanged(),
      flatMap(id => this.userService.getById(id)),
      tap(user => Log.info('Fetched user:', this, user)),
      tap(user => this.userName$.next(user.name))
    );

  private savedUser$ = this.saveUser$
    .pipe(
      flatMap(userUpdate => this.userService.update(userUpdate)),
      tap(saved => Log.info('Updated user:', this, saved)),
      tap(user => this.userName$.next(user.name)),
      tap(() => this.userSaveSuccess$.next(true)),
      tap(() => this.userSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.userSaveError$.next(error.error);
        throw error;
      })
    );

  constructor(private userService: UserService) {
  }

  init(userId: number | string) {
    this.userId$.next(Number(userId));
  }

  getUser(): Observable<OutputUserWithRole> {
    return merge(this.userById$, this.savedUser$);
  }

  getUserName(): Observable<string> {
    return this.userName$.asObservable();
  }

}
