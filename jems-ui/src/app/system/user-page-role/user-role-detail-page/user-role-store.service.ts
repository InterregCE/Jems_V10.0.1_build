import {Injectable} from '@angular/core';
import {merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  OutputCurrentUser,
  UserRoleDTO,
  UserRoleService,
  UserRoleCreateDTO,
} from '@cat/api';
import {catchError, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {SecurityService} from '../../../security/security.service';
import {RoutingService} from '../../../common/services/routing.service';
import {filter, take} from 'rxjs/internal/operators';
import {APIError} from '../../../common/models/APIError';

@Injectable()
export class UserRoleStore {
  public static USER_ROLE_DETAIL_PATH = '/app/system/userRole/detail/';

  userRoleSaveError$ = new Subject<APIError | null>();
  userRoleSaveSuccess$ = new Subject<boolean>();

  userRole$: Observable<UserRoleDTO>;
  currentUser$: Observable<OutputCurrentUser | null>;
  saveUserRole$ = new Subject<UserRoleDTO>();

  userRoleName$: Observable<string>;

  private savedUserRole$ = this.saveUserRole$
    .pipe(
      switchMap(userUpdate => this.roleService.updateUserRole(userUpdate)),
      tap(saved => Log.info('Updated user role:', this, saved)),
      tap(() => this.userRoleSaveSuccess$.next(true)),
      tap(() => this.userRoleSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.userRoleSaveError$.next(error.error);
        throw error;
      }),
    );

  constructor(private roleService: UserRoleService,
              private router: RoutingService,
              private securityService: SecurityService) {
    this.userRole$ = this.userRole();
    this.userRoleName$ = this.userRoleName();
    this.currentUser$ = this.securityService.currentUser;
  }

  createUserRole(user: UserRoleCreateDTO): Observable<UserRoleDTO> {
    return this.roleService.createUserRole(user)
      .pipe(
        take(1),
        tap(() => this.userRoleSaveError$.next(null)),
        tap(saved => Log.info('Created user role:', this, saved)),
        catchError((error: HttpErrorResponse) => {
          this.userRoleSaveError$.next(error.error);
          throw error;
        })
      );
  }

  private userRole(): Observable<UserRoleDTO> {
    const initialUserRole$ = this.router.routeParameterChanges(UserRoleStore.USER_ROLE_DETAIL_PATH, 'roleId')
      .pipe(
        filter(roleId => roleId !== null),
        switchMap(roleId => roleId ? this.roleService.getById(Number(roleId)) : of({} as UserRoleDTO)),
        tap(user => Log.info('Fetched the user role:', this, user))
      );

    return merge(initialUserRole$, this.savedUserRole$).pipe(
      shareReplay(1),
    );
  }

  private userRoleName(): Observable<string> {
    return this.userRole$.pipe(
      filter(role => !!role.name),
      map(role => role.name),
    );
  }

}
