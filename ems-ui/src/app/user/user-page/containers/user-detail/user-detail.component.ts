import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, of, Subject} from 'rxjs';
import {InputPassword, UserService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {RolePageService} from '../../../user-role/services/role-page/role-page.service';
import {SecurityService} from '../../../../security/security.service';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, filter, map, take, takeUntil, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';
import {Log} from '../../../../common/utils/log';
import {FormState} from '@common/components/forms/form-state';
import {UserStore} from '../../services/user-store.service';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailComponent extends BaseComponent implements OnInit {

  passwordSaveSuccess$ = new Subject<boolean>();
  passwordSaveError$ = new Subject<I18nValidationError | null>();

  userEditDisabled = false;
  passwordEditDisabled = true;

  userId = this.activatedRoute?.snapshot?.params?.userId;
  details$ = of();

  constructor(private userService: UserService,
              public userStore: UserStore,
              private rolePageService: RolePageService,
              private activatedRoute: ActivatedRoute,
              private securityService: SecurityService) {
    super();
    this.userStore.init(this.userId);
  }

  ngOnInit(): void {
    this.details$ = combineLatest([
      this.rolePageService.userRoles(),
      this.userStore.getUser(),
      this.securityService.currentUser
    ])
      .pipe(
        filter(([roles, user, currentUser]) => !!user),
        map(([roles, user, currentUser]) => ({
          roles: roles.length ? roles : [user.userRole],
          user,
          currentUser
        }))
      );

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

  passwordSwitchedMode(formState: FormState): void {
    this.userEditDisabled = formState === FormState.EDIT;
  }

  editSwitchedMode(formState: FormState): void {
    this.passwordEditDisabled = formState === FormState.EDIT;
  }
}
