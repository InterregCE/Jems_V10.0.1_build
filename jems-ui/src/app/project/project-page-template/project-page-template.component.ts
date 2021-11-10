import {AfterViewInit, ChangeDetectionStrategy, Component, Input, TemplateRef, ViewChild} from '@angular/core';
import {ProjectApplicationFormSidenavService} from '../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {combineLatest, Observable} from 'rxjs';
import {ProjectStatusDTO, ProjectVersionDTO, UserRoleDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectPageTemplateStore} from './project-page-template-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

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

  @ViewChild('sidenavVersionSelect', {static: true})
  sidenavVersionSelect: TemplateRef<any>;

  @Input() needsCard = false;

  @Input() titleText: string;
  @Input() titleKey: string;

  @Input() subTitleText: string;
  @Input() subTitleKey: string;

  @Input() descriptionText: string;
  @Input() descriptionKey: string;

  versionWarnData$: Observable<{
    current: ProjectVersionDTO | undefined;
    latest: ProjectVersionDTO | undefined;
    currentIsLatest: boolean;
    versions: ProjectVersionDTO[];
    projectStatus: ProjectStatusDTO.StatusEnum
  }>;

  versionSelectData$: Observable<{
    versions: ProjectVersionDTO[];
    current: ProjectVersionDTO;
    projectStatus: ProjectStatusDTO.StatusEnum
  }>;

  constructor(public projectSidenavService: ProjectApplicationFormSidenavService,
              public pageStore: ProjectPageTemplateStore) {
    this.versionWarnData$ = combineLatest([
      this.pageStore.currentVersion$,
      this.pageStore.latestVersion$,
      this.pageStore.currentVersionIsLatest$,
      this.pageStore.versions$,
      this.pageStore.projectStatus$
    ]).pipe(
      map(([current, latest, currentIsLatest, versions, status]) => ({current, latest, currentIsLatest, versions, projectStatus: status.status}))
    );

    this.versionSelectData$ = combineLatest([
      this.pageStore.versions$,
      this.pageStore.currentVersion$,
      this.pageStore.projectStatus$
    ]).pipe(
      map(([versions, current, status]) => ({versions, current, projectStatus: status.status})),
    );
  }

  ngAfterViewInit(): void {
    this.pageStore.versions$.pipe(untilDestroyed(this)).subscribe(() =>
      this.projectSidenavService.versionSelectTemplate$.next(this.sidenavVersionSelect)
    );
  }
}
