import {Injectable} from '@angular/core';
import {combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {ProjectService, ProjectVersionDTO} from '@cat/api';
import {distinctUntilChanged, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';
import {RoutingService} from '../../common/services/routing.service';
import {ProjectStore} from '../project-application/containers/project-application-detail/services/project-store.service';
import {filter} from 'rxjs/internal/operators';

@Injectable()
export class ProjectVersionStore {
  versions$: Observable<ProjectVersionDTO[]>;
  currentRouteVersion$: Observable<string | undefined>;
  currentVersion$: Observable<ProjectVersionDTO | undefined>;
  latestVersion$: Observable<ProjectVersionDTO | undefined>;
  currentIsLatest$: Observable<boolean>;

  versionChanged$ = new Subject<void>();

  private projectId$ = new ReplaySubject<number>();

  constructor(private router: RoutingService,
              private projectService: ProjectService,
              private routingService: RoutingService) {
    this.versions$ = this.versions();
    this.currentRouteVersion$ = this.currentRouteVersion();
    this.currentVersion$ = this.currentVersion();
    this.latestVersion$ = this.latestVersion();
    this.currentIsLatest$ = this.currentIsLatest();

    this.router.routeParameterChanges(ProjectStore.PROJECT_DETAIL_PATH, 'projectId')
      .pipe(
        tap(id => this.projectId$.next(id as number))
      ).subscribe();
  }

  changeVersion(versionDTO: ProjectVersionDTO): void {
    const queryParams = versionDTO.status ? {version: versionDTO.version} : {};
    this.routingService.navigate([], {queryParams});
  }

  private versions(): Observable<ProjectVersionDTO[]> {
    return combineLatest([this.projectId$, this.versionChanged$.pipe(startWith(null))])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id]) => this.projectService.getProjectVersions(id)),
        map(versions => [this.nextVersion(versions), ...versions]),
        tap(versions => Log.info('Fetched project versions', this, versions)),
        shareReplay(1)
      );
  }

  private currentRouteVersion(): Observable<string | undefined> {
    return this.router.routeParameterChanges(ProjectStore.PROJECT_DETAIL_PATH, 'version')
      .pipe(
        distinctUntilChanged(),
        map(version => version ? String(version) : undefined)
      );
  }

  private currentVersion(): Observable<ProjectVersionDTO | undefined> {
    return combineLatest([this.versions$, this.currentRouteVersion$])
      .pipe(
        switchMap(([versions, routeVersion]) => {
            const latestVersion = routeVersion || this.latest(versions)?.version;
            return of(versions.find(version => version.version === latestVersion));
          }
        )
      );
  }

  private latestVersion(): Observable<ProjectVersionDTO | undefined> {
    return this.versions$
      .pipe(
        map(versions => this.latest(versions)),
      );
  }

  private currentIsLatest(): Observable<boolean> {
    return combineLatest([this.currentVersion$, this.latestVersion$])
      .pipe(
        map(([current, latest]) => current?.version === latest?.version),
        shareReplay(1)
      );
  }

  private latest(versions?: ProjectVersionDTO[]): ProjectVersionDTO | undefined {
    return versions?.length ? versions[0] : undefined;
  }

  private nextVersion(versions: ProjectVersionDTO[]): ProjectVersionDTO {
    return {
      version: (Number(versions?.length ? versions[0].version : '0') + 1).toFixed(1),
      createdAt: null as any,
      status: null as any
    };
  }
}
