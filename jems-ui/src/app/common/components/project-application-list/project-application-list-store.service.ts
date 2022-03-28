import {Injectable} from '@angular/core';
import {
  CallService,
  IdNamePairDTO,
  PageOutputProjectSimple,
  ProgrammePriorityService,
  ProgrammeSpecificObjectiveDTO,
  ProjectSearchRequestDTO,
  ProjectService
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';

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
              private callService: CallService) {
    this.page$ = this.page(false);
    this.pageFilteredByOwner$ = this.page(true);
    this.policyObjectives$ = this.policyObjectives();
    this.publishedCalls$ = this.publishedCalls();
  }

  private page(filterByOwner: boolean): Observable<PageOutputProjectSimple> {
    return combineLatest([
      this.filter$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT)
      )
    ])
      .pipe(
        switchMap(([filter, pageIndex, pageSize, sort]) => filterByOwner ? this.projectService.getMyProjects(pageIndex, pageSize, `${sort.active},${sort.direction}`)
          : this.projectService.getAllProjects(`${sort.direction}` , `${sort.active}`, filter, pageIndex, pageSize)
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
    return this.callService.listCalls('PUBLISHED')
      .pipe(
        tap(calls => Log.info('Fetched the found calls:', this, calls)),
    );
  }
}
