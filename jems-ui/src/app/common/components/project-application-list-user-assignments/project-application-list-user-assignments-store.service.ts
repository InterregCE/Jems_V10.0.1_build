import {Injectable} from '@angular/core';
import {
  CallService,
  IdNamePairDTO,
  PageProjectUserDTO,
  ProjectUserService,
  ProjectWithUsersDTO,
  UserRoleCreateDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject, of} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;


@Injectable()
export class ProjectApplicationListUserAssignmentsStore {

  page$: Observable<PageProjectUserDTO>;
  filter$ = new BehaviorSubject<ProjectWithUsersDTO>(null as any);
  publishedCalls$: Observable<IdNamePairDTO[]>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(
    private projectUserService: ProjectUserService,
    private callService: CallService,
    private permissionService: PermissionService,
  ) {
    this.page$ = this.page();
    this.publishedCalls$ = this.publishedCalls();
  }

  private page(): Observable<PageProjectUserDTO> {
    return combineLatest([
      this.filter$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
    ]).pipe(
      switchMap(([filter, pageIndex, pageSize, sort]) => this.projectUserService.listProjectsWithAssignedUsers(filter, pageIndex, pageSize, sort)),
      tap(page => Log.info('Fetched project-user assignments:', this, page.content)),
    );
  }

  private publishedCalls(): Observable<IdNamePairDTO[]> {
    return this.permissionService.hasPermission(PermissionsEnum.CallRetrieve)
      .pipe(
        switchMap(hasPermission => hasPermission ? this.callService.listCalls('PUBLISHED') : of([])),
        tap(data => Log.info('Fetched the found calls:', this, data))
      );
  }

  refresh(): void {
    this.newPageIndex$.next(this.newPageIndex$.value);
  }
}
