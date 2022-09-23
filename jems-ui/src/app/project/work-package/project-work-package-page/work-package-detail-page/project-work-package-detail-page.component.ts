import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {WorkPackageService} from '@cat/api';
import {WorkPackagePageStore} from './work-package-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-project-work-package-detail-page',
  templateUrl: './project-work-package-detail-page.component.html',
  styleUrls: ['./project-work-package-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageDetailPageComponent {

  APPLICATION_FORM = APPLICATION_FORM;
  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService,
              public workPackageStore: WorkPackagePageStore,
              public projectStore: ProjectStore) {
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }
}
