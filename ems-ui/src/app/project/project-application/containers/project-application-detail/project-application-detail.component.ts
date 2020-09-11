import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from './services/project-store.service';
import {Permission} from '../../../../security/permissions/permission';
import {OutputProjectFile, OutputProjectStatus} from '@cat/api';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {combineLatest} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {ProjectApplicationFormSidenavService} from '../project-application-form-page/services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDetailComponent extends BaseComponent {
  Permission = Permission;
  fileType = OutputProjectFile.TypeEnum;

  projectId = this.activatedRoute.snapshot.params.projectId;

  assessmentFilesVisible$ = combineLatest([
    this.projectStore.getProject(),
    this.permissionService.permissionsChanged()
  ])
    .pipe(
      tap(([project, permissions]) => this.projectApplicationFormSidenavService.setAcronym(project.acronym)),
      map(([project, permissions]) =>
        permissions[0] !== Permission.APPLICANT_USER
        && project.projectStatus.status !== OutputProjectStatus.StatusEnum.DRAFT
      )
    );

  constructor(private projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private permissionService: PermissionService) {
    super();
    this.projectStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);
  }

}
