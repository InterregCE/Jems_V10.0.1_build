import {Injectable} from '@angular/core';
import {ProjectVersionStore} from '../common/services/project-version-store.service';
import {ProjectStore} from '../project-application/containers/project-application-detail/services/project-store.service';
import {Observable} from 'rxjs';
import {ProjectStatusDTO, ProjectVersionDTO} from '@cat/api';

@Injectable({
  providedIn: 'root'
})
export class ProjectPageTemplateStore {

  versions$: Observable<ProjectVersionDTO[]>;
  selectedVersion$: Observable<ProjectVersionDTO | undefined>;
  currentVersion$: Observable<ProjectVersionDTO | undefined>;
  isSelectedVersionCurrent$: Observable<boolean>;
  isThisUserOwner$: Observable<boolean>;
  projectStatus$: Observable<ProjectStatusDTO>;

  constructor(private projectVersionStore: ProjectVersionStore,
              private projectStore: ProjectStore) {
    this.versions$ = projectVersionStore.versions$;
    this.selectedVersion$ = this.projectVersionStore.selectedVersion$;
    this.currentVersion$ = projectVersionStore.currentVersion$;
    this.isSelectedVersionCurrent$ = this.projectVersionStore.isSelectedVersionCurrent$;
    this.isThisUserOwner$ = this.projectStore.userIsProjectOwner$;
    this.projectStatus$ = this.projectStore.projectStatus$;
  }

  changeVersion(versionDTO: ProjectVersionDTO): void {
      this.projectVersionStore.changeVersion(versionDTO);
  }

}
