import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject, Subject} from 'rxjs';
import {ProjectService, ProjectVersionDTO} from '@cat/api';
import {distinctUntilChanged, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {RoutingService} from '@common/services/routing.service';
import {filter} from 'rxjs/internal/operators';
import {ProjectPaths} from '@project/common/project-util';

@Injectable({
  providedIn: 'root'
})
export class ProjectVersionStore {
  versions$: Observable<ProjectVersionDTO[]>;
  currentRouteVersion$: Observable<string | undefined>;
  versionChanged$ = new Subject<void>();

  private projectId$ = new ReplaySubject<number>();

  constructor(private router: RoutingService,
              private projectService: ProjectService) {
    this.versions$ = this.versions();
    this.currentRouteVersion$ = this.currentRouteVersion();

    this.router.routeParameterChanges(ProjectPaths.PROJECT_DETAIL_PATH, 'projectId')
      .pipe(
        tap(id => this.projectId$.next(id as number))
      ).subscribe();
  }

  changeVersion(versionDTO: ProjectVersionDTO): void {
    const queryParams = versionDTO.status ? {version: versionDTO.version} : {};
    this.router.navigate([], {queryParams});
  }

  private versions(): Observable<ProjectVersionDTO[]> {
    return combineLatest([this.projectId$, this.versionChanged$.pipe(startWith(null))])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id]) => this.projectService.getProjectVersions(id)),
        tap(versions => Log.info('Fetched project versions', this, versions)),
        shareReplay(1)
      );
  }

  private currentRouteVersion(): Observable<string | undefined> {
    return this.router.routeParameterChanges(ProjectPaths.PROJECT_DETAIL_PATH, 'version')
      .pipe(
        distinctUntilChanged(),
        map(version => version ? String(version) : undefined),
        shareReplay(1),
      );
  }
}
