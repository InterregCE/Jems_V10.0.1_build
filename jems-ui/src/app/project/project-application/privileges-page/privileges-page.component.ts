import {ChangeDetectionStrategy, Component} from '@angular/core';
import { FormService } from '@common/components/section/form/form.service';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {ProjectApplicationFormSidenavService} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {
  PartnerUserCollaboratorDTO,
  ProjectPartnerSummaryDTO,
  ProjectUserCollaboratorDTO
} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-privileges-page',
  templateUrl: './privileges-page.component.html',
  styleUrls: ['./privileges-page.component.scss'],
  providers: [PrivilegesPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PrivilegesPageComponent {
  Alert = Alert;

  projectCollaborators$: Observable<{
    projectTitle: string;
    projectCollaborators: ProjectUserCollaboratorDTO[];
  }>;

  partnerCollaboratorTeams$: Observable<Map<ProjectPartnerSummaryDTO, PartnerUserCollaboratorDTO[]>>;

  constructor(public pageStore: PrivilegesPageStore,
              public formService: FormService,
              public projectStore: ProjectStore,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
    this.projectCollaborators$ = combineLatest([this.pageStore.projectTitle$, this.pageStore.projectCollaborators$])
      .pipe(
        map(([projectTitle, projectCollaborators]) => ({
          projectTitle,
          projectCollaborators
        }))
      );

    this.partnerCollaboratorTeams$ = combineLatest([
      this.pageStore.partnerSummariesOfLastApprovedVersion$,
      this.pageStore.partnerCollaborators$,
      this.projectStore.collaboratorLevel$
    ])
      .pipe(
        map(([partners, partnerCollaborators, managementLevel]) =>
          this.getPartnerTeams(partners, partnerCollaborators, managementLevel)
        )
      );
  }

  private getPartnerTeams(partners: ProjectPartnerSummaryDTO[], partnersCollaboratorData: PartnerUserCollaboratorDTO[], managementLevel: ProjectUserCollaboratorDTO.LevelEnum): Map<ProjectPartnerSummaryDTO, PartnerUserCollaboratorDTO[]> {
    const teams = new Map<ProjectPartnerSummaryDTO, PartnerUserCollaboratorDTO[]>();
    partners.forEach(partner => {
      const partnerCollaborators = partnersCollaboratorData.filter(partnerCollaborator => partnerCollaborator.partnerId === partner.id);
      teams.set(partner, partnerCollaborators.length || managementLevel === ProjectUserCollaboratorDTO.LevelEnum.MANAGE ? partnerCollaborators : null as any);
    });
    return teams;
}
}
