import {AfterViewInit, ChangeDetectionStrategy, Component, Input, TemplateRef, ViewChild} from '@angular/core';
import {ProjectApplicationFormSidenavService} from '../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectVersionStore} from '../services/project-version-store.service';
import {Alert} from '@common/components/forms/alert';
import {combineLatest, Observable} from 'rxjs';
import {ProjectVersionDTO} from '@cat/api';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-project-page-template',
  templateUrl: './project-page-template.component.html',
  styleUrls: ['./project-page-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPageTemplateComponent implements AfterViewInit {
  Alert = Alert;

  @ViewChild('sidenavVersionSelect', {static: true})
  sidenavVersionSelect: TemplateRef<any>;

  @Input() needsCard = false;

  @Input() titleText: string;
  @Input() titleKey: string;

  @Input() subTitleText: string;
  @Input() subTitleKey: string;

  versionWarnData$: Observable<{
    current: ProjectVersionDTO | undefined,
    latest: ProjectVersionDTO | undefined,
    currentIsLatest: boolean
  }>;

  constructor(public projectSidenavService: ProjectApplicationFormSidenavService,
              public projectVersionStore: ProjectVersionStore) {
    this.versionWarnData$ = combineLatest([
      this.projectVersionStore.currentVersion$,
      this.projectVersionStore.latestVersion$,
      this.projectVersionStore.currentIsLatest$
    ]).pipe(
      map(([current, latest, currentIsLatest]) => ({current, latest, currentIsLatest}))
    );
  }

  ngAfterViewInit(): void {
    this.projectSidenavService.versionSelectTemplate$.next(this.sidenavVersionSelect);
  }

}
