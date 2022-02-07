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
  currentVersion$: Observable<ProjectVersionDTO | undefined>;
  selectedVersionParam$: Observable<string | undefined>;
  selectedVersion$: Observable<ProjectVersionDTO | undefined>;
  isSelectedVersionCurrent$: Observable<boolean>;
  lastApprovedVersion$: Observable<ProjectVersionDTO | undefined>;

  private versionsChanged$ = new Subject<void>();

  private projectId$ = new ReplaySubject<number>();

  constructor(private router: RoutingService,
              private projectService: ProjectService) {
    this.versions$ = this.versions();
    this.selectedVersionParam$ = this.selectedVersionParam();
    this.selectedVersion$ = this.selectedProjectVersion();
    this.isSelectedVersionCurrent$ = this.isSelectedVersionCurrent();
    this.currentVersion$ = this.versions$.pipe(
      map(it => it.find(version => version.current)),
      distinctUntilChanged((o, n) => o?.version === n?.version),
    );
    this.router.routeParameterChanges(ProjectPaths.PROJECT_DETAIL_PATH, 'projectId')
      .pipe(
        tap(id => this.projectId$.next(id as number))
      ).subscribe();
    this.lastApprovedVersion$ = this.lastApprovedVersion();
  }

  changeVersion(versionDTO: ProjectVersionDTO): void {
    const queryParams = versionDTO.status && !versionDTO.current ? {version: versionDTO.version} : {};
    this.router.navigate([], {queryParams});
  }

  refreshVersions(): void {
    this.versionsChanged$.next();
  }

  private versions(): Observable<ProjectVersionDTO[]> {
    return combineLatest([this.projectId$, this.versionsChanged$.pipe(startWith(null))])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id]) => this.projectService.getProjectVersions(id)),
        tap(versions => Log.info('Fetched project versions', this, versions)),
        shareReplay(1)
      );
  }

  private selectedVersionParam(): Observable<string | undefined> {
    return this.router.routeParameterChanges(ProjectPaths.PROJECT_DETAIL_PATH, 'version')
      .pipe(
        distinctUntilChanged(),
        map(versionParam => versionParam ? String(versionParam) : undefined),
        shareReplay(1),
      );
  }

  private selectedProjectVersion(): Observable<ProjectVersionDTO | undefined> {
    return combineLatest([this.versions$, this.router.routeParameterChanges(ProjectPaths.PROJECT_DETAIL_PATH, 'version').pipe(distinctUntilChanged())])
      .pipe(
        map(([versions, versionParam]) => versionParam ? versions.find(it => it.version === versionParam) : versions.find(it => it.current)),
        shareReplay(1),
      );
  }

  private isSelectedVersionCurrent(): Observable<boolean> {
    return this.selectedVersion$.pipe(
      map(selectedVersion => selectedVersion?.current === true),
      shareReplay(1)
    );
  }

  private lastApprovedVersion(): Observable<ProjectVersionDTO | undefined> {
    return this.versions$
      .pipe(
        map(versions => versions.find(version => version.status === ProjectVersionDTO.StatusEnum.APPROVED))
      );
  }

}
