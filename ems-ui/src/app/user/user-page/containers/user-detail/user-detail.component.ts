import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {InputPassword, InputUserUpdate, OutputCurrentUser, OutputUser, OutputUserRole, UserService} from '@cat/api';
import {UserDetailService} from '../../services/user-detail/user-detail.service';
import {ActivatedRoute} from '@angular/router';
import {RolePageService} from '../../../user-role/services/role-page/role-page.service';
import {SecurityService} from '../../../../security/security.service';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailComponent extends BaseComponent implements OnInit {

  userRoles$: Observable<OutputUserRole[]> = this.rolePageService.userRoles();
  id = this.activatedRoute?.snapshot?.params?.userId;

  userSaveError$ = new Subject<I18nValidationError | null>();
  userSaveSuccess$ = new Subject<boolean>();
  passwordSaveSuccess$ = new Subject<boolean>();
  passwordSaveError$ = new Subject<I18nValidationError | null>();
  user$ = new Subject<OutputUser>();

  constructor(private userDetailService: UserDetailService,
              private userService: UserService,
              private rolePageService: RolePageService,
              private activatedRoute: ActivatedRoute,
              private securityService: SecurityService) {
    super();
  }

  ngOnInit(): void {
    this.userService.getById(this.id)
      .pipe(
        take(1),
        tap(user => console.log('Fetched user detail:', user)),
      ).subscribe(user => this.user$.next(user))
  }

  get currentUser(): Observable<OutputCurrentUser | null> {
    return this.securityService.currentUser;
  }

  updateUser(user: InputUserUpdate): void {
    this.userSaveSuccess$.next(false);
    this.userService.update(user)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.userSaveSuccess$.next(true)),
        tap(() => this.userSaveError$.next(null)),
        tap(saved => this.userDetailService.userSaved(saved)),
        tap(saved => console.log('Updated user:', saved)),
        catchError((error: HttpErrorResponse) => {
          this.userSaveError$.next(error.error);
          throw error;
        })
      ).subscribe(saved => this.user$.next(saved))
  }

  changePassword(password: InputPassword): void {
    this.passwordSaveSuccess$.next(false);
    this.userService.changePassword(this.id, password)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.passwordSaveSuccess$.next(true)),
        tap(() => this.passwordSaveError$.next(null)),
        tap(() => console.log('User password changed successfully.')),
        catchError((error: HttpErrorResponse) => {
          this.passwordSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }

}
