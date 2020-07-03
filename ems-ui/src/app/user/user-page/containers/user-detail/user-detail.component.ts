import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, merge, Subject} from 'rxjs';
import {InputPassword, InputUserUpdate, UserService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {RolePageService} from '../../../user-role/services/role-page/role-page.service';
import {SecurityService} from '../../../../security/security.service';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, flatMap, map, take, takeUntil, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';
import {Log} from '../../../../common/utils/log';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailComponent extends BaseComponent {

  userSaveError$ = new Subject<I18nValidationError | null>();
  userSaveSuccess$ = new Subject<boolean>();
  passwordSaveSuccess$ = new Subject<boolean>();
  passwordSaveError$ = new Subject<I18nValidationError | null>();

  userId = this.activatedRoute?.snapshot?.params?.userId;
  saveUser$ = new Subject<InputUserUpdate>();

  private userById$ = this.userService.getById(this.userId)
    .pipe(
      tap(user => Log.info('Fetched user:', this, user))
    );

  private savedUser$ = this.saveUser$
    .pipe(
      flatMap(userUpdate => this.userService.update(userUpdate)),
      tap(saved => Log.info('Updated user:', this, saved)),
      tap(() => this.userSaveSuccess$.next(true)),
      tap(() => this.userSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.userSaveError$.next(error.error);
        throw error;
      })
    );

  details$ = combineLatest([
    this.rolePageService.userRoles(),
    merge(this.userById$, this.savedUser$),
    this.securityService.currentUser
  ])
    .pipe(
      map(([roles, user, currentUser]) => ({roles, user, currentUser}))
    );

  constructor(private userService: UserService,
              private rolePageService: RolePageService,
              private activatedRoute: ActivatedRoute,
              private securityService: SecurityService) {
    super();
  }

  changePassword(password: InputPassword): void {
    this.userService.changePassword(this.userId, password)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.passwordSaveSuccess$.next(true)),
        tap(() => this.passwordSaveError$.next(null)),
        tap(() => Log.info('User password changed successfully.', this)),
        catchError((error: HttpErrorResponse) => {
          this.passwordSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }

}
