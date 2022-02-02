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
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {
  PartnersCollaborationDataPerPartner
} from '@project/project-application/privileges-page/partnersCollaborationDataPerPartner';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

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

  partnerCollaborators$: Observable<{
    partnerCollaborators: PartnerUserCollaboratorDTO[];
    managementLevel: ProjectUserCollaboratorDTO.LevelEnum;
  }>;

  constructor(public pageStore: PrivilegesPageStore,
              public formService: FormService,
              public partnerStore: ProjectPartnerStore,
              public projectStore: ProjectStore,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
    this.projectCollaborators$ = combineLatest([this.pageStore.projectTitle$, this.pageStore.projectCollaborators$])
      .pipe(
        map(([projectTitle, projectCollaborators]) => ({
          projectTitle,
          projectCollaborators
        }))
      );

    this.partnerCollaborators$ = combineLatest([
      this.pageStore.partnerCollaborators$,
      this.projectStore.collaboratorLevel$
    ])
      .pipe(
        map(([partnerCollaborators, managementLevel]) => ({
          partnerCollaborators,
          managementLevel
        }))
      );
  }

  getPartnerCollaboratorData(partner: ProjectPartnerSummaryDTO, partnersCollaboratorData: PartnerUserCollaboratorDTO[], managementLevel: ProjectUserCollaboratorDTO.LevelEnum): PartnersCollaborationDataPerPartner | null {
    const collaboratorsPerPartner = {
      partner: partner,
      partnerCollaborators: partnersCollaboratorData.filter(collaborator => collaborator.partnerId === partner.id)
    } as PartnersCollaborationDataPerPartner;
    return collaboratorsPerPartner.partnerCollaborators.length > 0 || managementLevel === ProjectUserCollaboratorDTO.LevelEnum.MANAGE ? collaboratorsPerPartner : null;
  }
}
