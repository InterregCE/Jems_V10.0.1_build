import {Injectable} from '@angular/core';
import {OutputUser, PageOutputUser, UserService, OutputUserRole, InputUserCreate, PageOutputUserRole, UserRoleService} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable()
export class UserPageService {

  private page$ = new ReplaySubject<any>(1);
  private userSaveError$ = new ReplaySubject<I18nValidationError | null>();
  private userSaveSuccess$ = new ReplaySubject<boolean>();
  private userRoles$ = new ReplaySubject<OutputUserRole[]>();
  private filtered$ = this.page$
    .pipe(
      flatMap((page) => this.userService.list(page.page, page.size, page.sort)),
      map((page: PageOutputUser) => page.content)
    );

  constructor(private userService: UserService,
              private userRolesService: UserRoleService) {
  }

  filtered(): Observable<OutputUser[]> {
    return this.filtered$;
  }

  saveError(): Observable<I18nValidationError | null> {
    return this.userSaveError$.asObservable();
  }

  saveSuccess(): Observable<boolean> {
    return this.userSaveSuccess$.asObservable();
  }

  userRoles(): Observable<OutputUserRole[]> {
    return this.userRoles$.asObservable();
  }

  newPage(page?: number, size?: number, sort?: string): void {
    this.page$.next({page, size, sort});
  }

  saveUser(user: InputUserCreate): void {
    this.userService.createUser(user).pipe(
      tap(() => this.userSaveSuccess$.next(true)),
      tap(() => this.newPage(0, 100, 'id,desc')),
      catchError((error: HttpErrorResponse) => {
        this.userSaveError$.next(error.error);
        throw error;
      })
    ).subscribe(() => this.userSaveError$.next(null));
  }

  getUserRoles(): void {
    this.userRolesService.list().subscribe((results: PageOutputUserRole) => {
      this.userRoles$.next(results.content);
    })
  }
}
