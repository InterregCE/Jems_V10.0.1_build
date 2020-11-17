import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProgrammeLegalStatusService} from '@cat/api';

@Component({
  selector: 'app-project-application-partner-identity',
  templateUrl: './project-application-partner-identity.component.html',
  styleUrls: ['./project-application-partner-identity.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerIdentityComponent extends BaseComponent implements OnInit {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  legalStatuses$ = this.programmeLegalStatusService.getProgrammeLegalStatusList();

  constructor(public partnerStore: ProjectPartnerStore,
              public projectStore: ProjectStore,
              private programmeLegalStatusService: ProgrammeLegalStatusService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    super();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);

    if (this.partnerId) {
      return;
    }
    // creating a new partner
    this.partnerStore.init(null, this.projectId);
  }
}
