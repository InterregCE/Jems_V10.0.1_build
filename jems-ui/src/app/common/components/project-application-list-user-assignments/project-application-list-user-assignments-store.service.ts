import {Injectable} from '@angular/core';
import {
  PageProjectUserDTO,
  ProjectUserService, UserPermissionFilterDTO, UserRoleCreateDTO, UserService
} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable()
export class ProjectApplicationListUserAssignmentsStore {

  defaultUserPermissions: PermissionsEnum[] = [
    PermissionsEnum.ProjectRetrieve,
    PermissionsEnum.ProjectRetrieveEditUserAssignments,
  ];
  // those are creating MONITOR checkbox:
  availableUsersPermissions: PermissionsEnum[] = [
    PermissionsEnum.ProjectFormRetrieve,
    PermissionsEnum.ProjectFileApplicationRetrieve,
    PermissionsEnum.ProjectCheckApplicationForm,
    PermissionsEnum.ProjectAssessmentView,
    PermissionsEnum.ProjectStatusDecisionRevert,
    PermissionsEnum.ProjectStatusReturnToApplicant,
    PermissionsEnum.ProjectStartStepTwo,
    PermissionsEnum.ProjectFileAssessmentRetrieve,
  ];

  page$: Observable<PageProjectUserDTO>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(
    private projectUserService: ProjectUserService,
    private userService: UserService,
  ) {
    this.page$ = this.page();
  }

  private page(): Observable<PageProjectUserDTO> {
    return combineLatest([
      combineLatest([
        this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
        this.newSort$.pipe(
          startWith(Tables.DEFAULT_INITIAL_SORT),
          map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
          map(sort => `${sort.active},${sort.direction}`)
        ),
      ]).pipe(
        switchMap(([pageIndex, pageSize, sort]) => this.projectUserService.listProjectsWithAssignedUsers(pageIndex, pageSize, sort)),
      ),
      this.userService.listUsersByPermissions({
        needsToHaveAtLeastOneFrom: this.availableUsersPermissions,
        needsNotToHaveAnyOf: this.defaultUserPermissions,
      } as UserPermissionFilterDTO),
      this.userService.listUsersByPermissions({
        needsToHaveAtLeastOneFrom: this.defaultUserPermissions,
        needsNotToHaveAnyOf: [],
      } as UserPermissionFilterDTO),
    ])
      .pipe(
        map(([page, availableUsers, defaultUsers]) => ({
          ...page,
          content: page.content.map(project => ({
            ...project,
            defaultUsers,
            availableUsers,
          })),
        })),
        tap(page => Log.info('Fetched the projects:', this, page.content)),
      );
  }
}
