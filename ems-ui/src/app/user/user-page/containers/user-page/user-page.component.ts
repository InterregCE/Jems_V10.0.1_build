import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UserPageService} from '../../services/user-page/user-page.service';
import {InputUserCreate, UserService} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {UserDetailService} from '../../services/user-detail/user-detail.service';
import {RolePageService} from '../../../user-role/services/role-page/role-page.service';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPageComponent extends BaseComponent {
  Permission = Permission;

  userList$ = this.userPageService.userList();
  userRoles$ = this.rolePageService.userRoles();
  userSaveError$ = new Subject<I18nValidationError | null>();
  userSaveSuccess$ = new Subject<boolean>();

  constructor(private userPageService: UserPageService,
              private userDetailService: UserDetailService,
              private userService: UserService,
              private rolePageService: RolePageService) {
    super();
  }

  createUser(user: InputUserCreate): void {
    this.userSaveSuccess$.next(false);
    this.userService.createUser(user)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.userSaveSuccess$.next(true)),
        tap(() => this.userSaveError$.next(null)),
        tap(saved => this.userDetailService.userSaved(saved)),
        tap(saved => console.log('Created user:', saved)),
        catchError((error: HttpErrorResponse) => {
          this.userSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }
}
