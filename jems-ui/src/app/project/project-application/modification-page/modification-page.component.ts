import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDetailDTO, ProjectStatusDTO, ProjectVersionDTO, UserRoleDTO} from '@cat/api';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {combineLatest, Observable} from 'rxjs';
import {map, take, tap} from 'rxjs/operators';
import {ModificationPageStore} from '@project/project-application/modification-page/modification-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Component({
  selector: 'app-modification-page',
  templateUrl: './modification-page.component.html',
  styleUrls: ['./modification-page.component.scss'],
  providers: [ModificationPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModificationPageComponent {
  PermissionsEnum = UserRoleDTO.PermissionsEnum;
  ProjectStatusEnum = ProjectStatusDTO.StatusEnum;

  fileManagementSection = {type: FileCategoryTypeEnum.MODIFICATION} as CategoryInfo;
  pendingButtonProgress: boolean;

  data$: Observable<{
    currentVersionOfProject: ProjectDetailDTO,
    currentVersionOfProjectTitle: string;
    currentVersionOfProjectStatus: ProjectStatusDTO.StatusEnum;
    modificationDecisions: ProjectStatusDTO[];
    canOpenModification: boolean;
    canHandBackModification: boolean;
    versions: ProjectVersionDTO[];
  }>;

  constructor(public projectStore: ProjectStore,
              private pageStore: ModificationPageStore,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private projectVersionStore: ProjectVersionStore) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionOfProject$,
      this.pageStore.currentVersionOfProjectTitle$,
      this.pageStore.currentVersionOfProjectStatus$,
      this.pageStore.modificationDecisions$,
      this.pageStore.hasOpenPermission$,
      this.projectVersionStore.versions$
    ]).pipe(
        map(([currentVersionOfProject, currentVersionOfProjectTitle, currentVersionOfProjectStatus, modificationDecisions, hasOpenPermission, versions]) => ({
          currentVersionOfProject,
          currentVersionOfProjectTitle,
          currentVersionOfProjectStatus,
          modificationDecisions,
          canOpenModification: this.canOpenModification(currentVersionOfProjectStatus, hasOpenPermission),
          canHandBackModification: this.canHandBackModification(currentVersionOfProjectStatus, hasOpenPermission),
          versions
        }))
      );
  }

  startModification(): void {
    this.pageStore.startModification()
      .pipe(take(1))
      .subscribe();
  }

  handBackToApplicant(): void {
    this.pageStore.handBackToApplicant()
      .pipe(
        take(1),
        tap(() => this.redirectToPartnerOverview())
      ).subscribe();
  }

  private redirectToPartnerOverview(): void {
    this.routingService.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  private canOpenModification(projectStatus: ProjectStatusDTO.StatusEnum, hasOpenPermission: boolean): boolean {
    return hasOpenPermission
      && (projectStatus === this.ProjectStatusEnum.APPROVED || projectStatus === this.ProjectStatusEnum.NOTAPPROVED);
  }

  private canHandBackModification(projectStatus: ProjectStatusDTO.StatusEnum, hasOpenPermission: boolean): boolean {
    return hasOpenPermission && projectStatus === this.ProjectStatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED;
  }
}
