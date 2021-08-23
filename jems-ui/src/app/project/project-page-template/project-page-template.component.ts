import {AfterViewInit, ChangeDetectionStrategy, Component, Input, TemplateRef, ViewChild} from '@angular/core';
import {ProjectApplicationFormSidenavService} from '../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {combineLatest, Observable} from 'rxjs';
import {ProjectVersionDTO, UserRoleDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectPageTemplateStore} from './project-page-template-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-project-page-template',
  templateUrl: './project-page-template.component.html',
  styleUrls: ['./project-page-template.component.scss'],
  providers: [ProjectPageTemplateStore],
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
    current: ProjectVersionDTO | undefined,
    latest: ProjectVersionDTO | undefined,
    currentIsLatest: boolean
  }>;

  versionSelectData$: Observable<{
    versions: ProjectVersionDTO[],
    current: ProjectVersionDTO,
  }>;

  constructor(public projectSidenavService: ProjectApplicationFormSidenavService,
              public pageStore: ProjectPageTemplateStore) {
    this.versionWarnData$ = combineLatest([
      this.pageStore.currentVersion$,
      this.pageStore.latestVersion$,
      this.pageStore.currentVersionIsLatest$
    ]).pipe(
      map(([current, latest, currentIsLatest]) => ({current, latest, currentIsLatest}))
    );

    this.versionSelectData$ = combineLatest([
      this.pageStore.versions$,
      this.pageStore.currentVersion$
    ]).pipe(
      map(([versions, current]) => ({versions, current})),
    );
  }

  ngAfterViewInit(): void {
    this.projectSidenavService.versionSelectTemplate$.next(this.sidenavVersionSelect);
  }
}
