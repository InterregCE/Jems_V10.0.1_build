import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '../../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerBudgetTabService} from './project-partner-budget-tab.service';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import { APPLICATION_FORM } from '@project/common/application-form-model';

@Component({
  selector: 'app-project-partner-budget-tab',
  templateUrl: './project-partner-budget-tab.component.html',
  styleUrls: ['./project-partner-budget-tab.component.scss'],
  providers: [ProjectPartnerBudgetTabService, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetTabComponent {

  APPLICATION_FORM = APPLICATION_FORM;
  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  constructor(public projectStore: ProjectStore,
              public projectPartnerDetailStore: ProjectPartnerDetailPageStore) {
  }

}
