import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProgrammeLegalStatusService} from '@cat/api';

@Component({
  selector: 'app-project-application-partner-identity',
  templateUrl: './project-application-partner-identity.component.html',
  styleUrls: ['./project-application-partner-identity.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerIdentityComponent {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  legalStatuses$ = this.programmeLegalStatusService.getProgrammeLegalStatusList();

  constructor(public partnerStore: ProjectPartnerStore,
              public projectStore: ProjectStore,
              private programmeLegalStatusService: ProgrammeLegalStatusService,
              private activatedRoute: ActivatedRoute) {
  }
}
