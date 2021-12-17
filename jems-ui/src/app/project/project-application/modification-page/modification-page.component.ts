import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {combineLatest, Observable} from 'rxjs';
import {map, take, tap} from 'rxjs/operators';
import {ModificationPageStore} from '@project/project-application/modification-page/modification-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';

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
    currentVersionOfProjectTitle: string;
    currentVersionOfProjectStatus: ProjectStatusDTO.StatusEnum;
    modificationDecisions: ProjectStatusDTO[];
    canOpenModification: boolean;
    canHandBackModification: boolean;
  }>;

  constructor(private pageStore: ModificationPageStore,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute) {
    this.data$ = combineLatest([
      this.pageStore.currentVersionOfProjectTitle$,
      this.pageStore.currentVersionOfProjectStatus$,
      this.pageStore.modificationDecisions$,
      this.pageStore.hasOpenPermission$,
    ]).pipe(
        map(([currentVersionOfProjectTitle, currentVersionOfProjectStatus, modificationDecisions, hasOpenPermission]) => ({
          currentVersionOfProjectTitle,
          currentVersionOfProjectStatus,
          modificationDecisions,
          canOpenModification: this.canOpenModification(currentVersionOfProjectStatus, hasOpenPermission),
          canHandBackModification: this.canHandBackModification(currentVersionOfProjectStatus, hasOpenPermission)
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
