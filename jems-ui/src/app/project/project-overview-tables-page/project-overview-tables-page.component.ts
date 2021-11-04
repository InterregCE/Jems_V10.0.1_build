import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectOverviewTablesPageStore} from '@project/project-overview-tables-page/project-overview-tables-page-store.service';

@Component({
  selector: 'app-project-overview-tables-page',
  templateUrl: './project-overview-tables-page.component.html',
  styleUrls: ['./project-overview-tables-page.component.scss'],
  providers: [ProjectOverviewTablesPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectOverviewTablesPageComponent {
  APPLICATION_FORM = APPLICATION_FORM;

  constructor(public projectStore: ProjectStore) {
  }

}
