import {
  ChangeDetectionStrategy,
  Component
} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ProgrammeLegalStatusService} from '@cat/api';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {APPLICATION_FORM} from '@project/application-form-model';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';

@Component({
  templateUrl: './project-partner-detail-page.component.html',
  styleUrls: ['./project-partner-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerDetailPageComponent {
  APPLICATION_FORM = APPLICATION_FORM;

  constructor(private programmeLegalStatusService: ProgrammeLegalStatusService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore,
              private router: RoutingService,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }
}
