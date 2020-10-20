import {ChangeDetectionStrategy, Component} from '@angular/core';
import {InputUserCreate, UserService} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {RolePageService} from '../../../user-role/services/role-page/role-page.service';
import {catchError, mergeMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {combineLatest, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {BaseComponent} from '@common/components/base-component';
import {Log} from '../../../../common/utils/log';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../../common/utils/tables';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPageComponent extends BaseComponent {
  Permission = Permission;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  currentPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.userService.list(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the users:', this, page.content)),
      );

  userRoles$ = this.rolePageService.userRoles();
  userSaveError$ = new Subject<I18nValidationError | null>();
  userSaveSuccess$ = new Subject<boolean>();

  constructor(private userService: UserService,
              private rolePageService: RolePageService) {
    super();
  }

  createUser(user: InputUserCreate): void {
    this.userService.createUser(user)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.userSaveSuccess$.next(true)),
        tap(() => this.userSaveError$.next(null)),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(saved => Log.info('Created user:', this, saved)),
        catchError((error: HttpErrorResponse) => {
          this.userSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }
}
