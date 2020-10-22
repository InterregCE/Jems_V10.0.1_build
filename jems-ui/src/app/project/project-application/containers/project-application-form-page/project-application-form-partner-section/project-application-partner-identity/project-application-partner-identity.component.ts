import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';

@Component({
  selector: 'app-project-application-partner-identity',
  templateUrl: './project-application-partner-identity.component.html',
  styleUrls: ['./project-application-partner-identity.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerIdentityComponent extends BaseComponent implements OnInit {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId

  constructor(public partnerStore: ProjectPartnerStore,
              private projectStore: ProjectStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    super();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);

    if (this.partnerId) {
      return;
    }
    // creating a new partner
    this.partnerStore.init(null);
  }

  redirectToPartnerOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationForm']);
  }
}
