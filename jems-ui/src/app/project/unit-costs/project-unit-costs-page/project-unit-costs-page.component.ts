import {Component} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {ProjectUnitCostDTO} from '@cat/api';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {Observable} from 'rxjs';
import {ProjectUnitCostsStore} from '@project/unit-costs/project-unit-costs-page/project-unit-costs-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';

@Component({
  selector: 'app-project-unit-costs-page',
  templateUrl: './project-unit-costs-page.component.html',
  styleUrls: ['./project-unit-costs-page.component.scss']
})
export class ProjectUnitCostsPageComponent {

  tableConfig: TableConfig[];
  data$: Observable<ProjectUnitCostDTO[]>;

  APPLICATION_FORM = APPLICATION_FORM;

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private pageStore: ProjectPartnerDetailPageStore,
              private visibilityStatusService: FormVisibilityStatusService,
              private projectUnitCostsStore: ProjectUnitCostsStore) {
    this.data$ = this.projectUnitCostsStore.projectUnitCosts$;
  }

}
