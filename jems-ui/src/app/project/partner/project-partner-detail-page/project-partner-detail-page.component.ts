import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ProgrammeLegalStatusService} from '@cat/api';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {TabService} from '../../../common/services/tab.service';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {APPLICATION_FORM} from '@project/application-form-model';

@Component({
  templateUrl: './project-partner-detail-page.component.html',
  styleUrls: ['./project-partner-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerDetailPageComponent implements OnDestroy {

  APPLICATION_FORM = APPLICATION_FORM;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  activeTab$ = this.tabService.currentTab(
    ProjectPartnerDetailPageComponent.name + this.partnerId
  );
  legalStatuses$ = this.programmeLegalStatusService.getProgrammeLegalStatusList();

  constructor(private programmeLegalStatusService: ProgrammeLegalStatusService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore,
              private router: Router,
              private tabService: TabService) {
  }

  ngOnDestroy(): void {
    this.tabService.cleanupTab(ProjectPartnerDetailPageComponent.name + this.partnerId);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(ProjectPartnerDetailPageComponent.name + this.partnerId, tabIndex);
  }

}
