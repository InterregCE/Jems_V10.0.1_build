import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {WorkPackageService} from '@cat/api';
import {ProjectWorkPackagePageStore} from './project-work-package-page-store.service';
import {TabService} from '../../../../common/services/tab.service';

@Component({
  selector: 'app-project-work-package-detail-page',
  templateUrl: './project-work-package-detail-page.component.html',
  styleUrls: ['./project-work-package-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageDetailPageComponent {

  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;

  activeTab$ = this.tabService.currentTab(
    'ProjectWorkPackageDetailPageComponent' + this.workPackageId
  );

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              public workPackageStore: ProjectWorkPackagePageStore,
              private tabService: TabService) {
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab('ProjectWorkPackageDetailPageComponent' + this.workPackageId, tabIndex);
  }
}
