import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {AuditControlCorrectionDTO, ProjectDetailDTO, ProjectModificationDecisionDTO, ProjectStatusDTO, ProjectVersionDTO, UserRoleDTO} from '@cat/api';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {combineLatest, Observable} from 'rxjs';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
import {ModificationPageStore} from '@project/project-application/modification-page/modification-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {Alert} from '@common/components/forms/alert';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'jems-modification-page',
  templateUrl: './modification-page.component.html',
  styleUrls: ['./modification-page.component.scss'],
  providers: [ModificationPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModificationPageComponent {
  Alert = Alert;
  PermissionsEnum = UserRoleDTO.PermissionsEnum;
  ProjectStatusEnum = ProjectStatusDTO.StatusEnum;

  fileManagementSection = {type: FileCategoryTypeEnum.MODIFICATION} as CategoryInfo;
  pendingButtonProgress: boolean;
  successMessage: boolean;

  data$: Observable<{
    currentVersionOfProject: ProjectDetailDTO;
    currentVersionOfProjectTitle: string;
    currentVersionOfProjectStatus: ProjectStatusDTO.StatusEnum;
    modificationDecisions: ProjectModificationDecisionDTO[];
    canOpenModification: boolean;
    canHandBackModification: boolean;
    versions: ProjectVersionDTO[] | ProjectVersionDTO | undefined;
  }>;
  availableCorrections$: Observable<AuditControlCorrectionDTO[]>;

  constructor(public projectStore: ProjectStore,
              private pageStore: ModificationPageStore,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private projectVersionStore: ProjectVersionStore,
              private changeDetectorRef: ChangeDetectorRef,
              private dialog: MatDialog) {
    this.availableCorrections$ = this.pageStore.availableCorrections$;
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
        versions: this.isModificationOpenedOrSubmitted(currentVersionOfProjectStatus) ? versions.slice(1, versions.length - 1) : versions
      }))
    );
  }

  startModification(): void {
    Forms.confirm(
      this.dialog,
      {
        title: 'application.action.open.modification.confirmation.dialog',
        warnMessage: 'application.action.open.modification.confirmation.dialog.warning'
      }
    ).pipe(
      take(1),
      filter(yes => !!yes),
      take(1),
      switchMap(() => this.pageStore.startModification()),
      tap(() => this.showSuccessMessage())
    ).subscribe();
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
    return hasOpenPermission &&
      [this.ProjectStatusEnum.APPROVED, this.ProjectStatusEnum.NOTAPPROVED, this.ProjectStatusEnum.CONTRACTED].includes(projectStatus);
  }

  private canHandBackModification(projectStatus: ProjectStatusDTO.StatusEnum, hasOpenPermission: boolean): boolean {
    return hasOpenPermission &&
      [this.ProjectStatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED, this.ProjectStatusEnum.MODIFICATIONSUBMITTED].includes(projectStatus);
  }

  private showSuccessMessage(): void {
    this.successMessage = true;
    setTimeout(() => {
      this.successMessage = false;
      this.changeDetectorRef.markForCheck();
    }, 4000);
  }

  isModificationOpenedOrSubmitted(currentStatus: ProjectStatusDTO.StatusEnum) {
    return [
      ProjectVersionDTO.StatusEnum.MODIFICATIONPRECONTRACTING,
      ProjectVersionDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED,
      ProjectStatusDTO.StatusEnum.INMODIFICATION,
      ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED,
    ].includes(currentStatus)
  }
}
