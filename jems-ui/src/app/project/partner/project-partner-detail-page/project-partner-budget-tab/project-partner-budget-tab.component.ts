import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectPartnerBudgetService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {ProjectPartnerStore} from '../../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectStore} from '../../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerDetailPageStore} from '../project-partner-detail-page.store';

@Component({
  selector: 'app-project-partner-budget-tab',
  templateUrl: './project-partner-budget-tab.component.html',
  styleUrls: ['./project-partner-budget-tab.component.scss'],
  providers: [ProjectPartnerDetailPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetTabComponent {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  constructor(private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore) {
    this.projectStore.init(this.projectId);
  }

}
