import {AfterViewInit, ChangeDetectionStrategy, Component, Input, TemplateRef, ViewChild} from '@angular/core';
import {ProjectApplicationFormSidenavService} from '../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {combineLatest, Observable} from 'rxjs';
import {ProjectUserDTO, ProjectVersionDTO, UserRoleDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectPageTemplateStore} from './project-page-template-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import ProjectStatusEnum = ProjectUserDTO.ProjectStatusEnum;

@UntilDestroy()
@Component({
  selector: 'app-project-page-template',
  templateUrl: './project-page-template.component.html',
  styleUrls: ['./project-page-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPageTemplateComponent implements AfterViewInit {
  Alert = Alert;
  PermissionsEnum = PermissionsEnum;
  ProjectStatusEnum = ProjectStatusEnum;

  @ViewChild('sidenavVersionSelect', {static: true})
  sidenavVersionSelect: TemplateRef<any>;

  @Input() needsCard = false;
  @Input() isVersionedData = true;

  @Input() titleText: string;
  @Input() titleKey: string;

  @Input() subTitleText: string;
  @Input() subTitleKey: string;

  @Input() descriptionText: string;
  @Input() descriptionKey: string;

  versionWarnData$: Observable<{
    selected: ProjectVersionDTO | undefined;
    current: ProjectVersionDTO | undefined;
    selectedIsCurrent: boolean;
    versions: ProjectVersionDTO[];
  }>;

  versionSelectData$: Observable<{
    versions: ProjectVersionDTO[];
    selectedVersion: ProjectVersionDTO | undefined;
  }>;

  constructor(public projectSidenavService: ProjectApplicationFormSidenavService,
              public pageStore: ProjectPageTemplateStore) {
    this.versionWarnData$ = combineLatest([
      this.pageStore.selectedVersion$,
      this.pageStore.currentVersion$,
      this.pageStore.isSelectedVersionCurrent$,
      this.pageStore.versions$
    ]).pipe(
      map(([selectedVersion, currentVersion, selectedIsCurrent, versions]) => ({selected: selectedVersion, current: currentVersion, selectedIsCurrent, versions}))
    );

    this.versionSelectData$ = combineLatest([
      this.pageStore.versions$,
      this.pageStore.selectedVersion$
    ]).pipe(
      map(([versions, selectedVersion]) => ({versions, selectedVersion})),
    );
  }

  ngAfterViewInit(): void {
    this.pageStore.versions$.pipe(untilDestroyed(this)).subscribe(() =>
      this.projectSidenavService.versionSelectTemplate$.next(this.sidenavVersionSelect)
    );
  }
}
