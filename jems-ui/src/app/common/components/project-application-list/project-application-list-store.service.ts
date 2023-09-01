import {Injectable} from '@angular/core';
import {
  CallService,
  IdNamePairDTO,
  PageOutputProjectSimple,
  ProgrammePriorityService,
  ProgrammeSpecificObjectiveDTO,
  ProjectSearchRequestDTO,
  ProjectService, UserRoleCreateDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable()
export class ProjectApplicationListStore {

  page$: Observable<PageOutputProjectSimple>;
  pageFilteredByOwner$: Observable<PageOutputProjectSimple>;
  filter$ = new BehaviorSubject<ProjectSearchRequestDTO>(null as any);
  policyObjectives$: Observable<ProgrammeSpecificObjectiveDTO[]>;
  publishedCalls$: Observable<IdNamePairDTO[]>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private projectService: ProjectService,
              private priorityService: ProgrammePriorityService,
              private permissionService: PermissionService,
              private callService: CallService) {
    this.page$ = this.page(false, 25);
    this.pageFilteredByOwner$ = this.page(true, 10);
    this.policyObjectives$ = this.policyObjectives();
    this.publishedCalls$ = this.publishedCalls();
  }

  private page(filterByOwner: boolean, defaultPageSize: number): Observable<PageOutputProjectSimple> {
    return combineLatest([
      this.filter$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(defaultPageSize)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT)
      )
    ])
      .pipe(
        switchMap(([filter, pageIndex, pageSize, sort]) => filterByOwner ? this.projectService.getMyProjects(pageIndex, pageSize, `${sort.active},${sort.direction}`)
          : this.projectService.getAllProjects(filter, pageIndex, pageSize, `${sort.direction}`, `${sort.active}`)
        ),
        tap(page => Log.info('Fetched the projects:', this, page.content)),
      );
  }

  private policyObjectives(): Observable<ProgrammeSpecificObjectiveDTO[]> {
    return this.priorityService.get()
      .pipe(
        map(priorities => priorities.flatMap(priority => priority.specificObjectives)),
        tap(setup => Log.info('Fetched the programme priority setup:', this, setup)),
      );
  }

  private publishedCalls(): Observable<IdNamePairDTO[]> {
    return this.permissionService.hasPermission(PermissionsEnum.CallRetrieve)
      .pipe(
        switchMap(hasPermission => hasPermission ? this.callService.listCalls('PUBLISHED') : of([])),
        tap(data => Log.info('Fetched the found calls:', this, data))
      );
  }
}
