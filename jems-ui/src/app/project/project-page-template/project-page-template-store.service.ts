import {Injectable} from '@angular/core';
import {ProjectVersionStore} from '../common/services/project-version-store.service';
import {ProjectStore} from '../project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectVersionDTO} from '@cat/api';
import {distinctUntilChanged, map, shareReplay} from 'rxjs/operators';
import {ProjectUtil} from '../common/project-util';

@Injectable()
export class ProjectPageTemplateStore {

  versions$: Observable<ProjectVersionDTO[]>;
  currentVersion$: Observable<ProjectVersionDTO>;
  latestVersion$: Observable<ProjectVersionDTO | undefined>;
  currentVersionIsLatest$: Observable<boolean>;
  isThisUserOwner$: Observable<boolean>;

  constructor(private projectVersionStore: ProjectVersionStore,
              private projectStore: ProjectStore) {
    this.versions$ = this.versions();
    this.currentVersion$ = this.currentVersion();
    this.latestVersion$ = this.latestVersion();
    this.currentVersionIsLatest$ = this.projectStore.currentVersionIsLatest$;
    this.isThisUserOwner$ = this.projectStore.userIsProjectOwner$;
  }

  private static latest(versions?: ProjectVersionDTO[]): ProjectVersionDTO | undefined {
    return versions?.length ? versions[0] : undefined;
  }

  private static nextVersion(versions: ProjectVersionDTO[]): ProjectVersionDTO {
    return {
      version: (Number(versions?.length ? versions[0].version : '0') + 1).toFixed(1),
      createdAt: null as any,
      status: null as any
    };
  }

  changeVersion(versionDTO: ProjectVersionDTO): void {
    this.projectVersionStore.changeVersion(versionDTO);
  }

  private versions(): Observable<ProjectVersionDTO[]> {
    const project$ = this.projectStore.project$
      .pipe(
        distinctUntilChanged((o, n) => o.projectStatus.status === n.projectStatus.status)
      );

    return combineLatest([this.projectVersionStore.versions$, project$])
      .pipe(
        map(([versions, project]) =>
          ProjectUtil.isOpenForModifications(project) ? [ProjectPageTemplateStore.nextVersion(versions), ...versions] : versions
        ),
        shareReplay(1)
      );
  }

  private currentVersion(): Observable<ProjectVersionDTO> {
    return combineLatest([this.versions$, this.projectVersionStore.currentRouteVersion$])
      .pipe(
        map(([versions, routeVersion]) => {
            const latestVersion = routeVersion || ProjectPageTemplateStore.latest(versions)?.version;
            return versions.find(version => version.version === latestVersion) || ProjectPageTemplateStore.nextVersion(versions);
          }
        ),
        shareReplay(1)
      );
  }

  private latestVersion(): Observable<ProjectVersionDTO | undefined> {
    return this.versions$
      .pipe(
        map(versions => ProjectPageTemplateStore.latest(versions)),
      );
  }

}
