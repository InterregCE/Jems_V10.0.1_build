import {ChangeDetectionStrategy, Component} from '@angular/core';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';

@Component({
  selector: 'app-lumpsum-budget-table',
  templateUrl: './lumpsum-budget-table.component.html',
  styleUrls: ['./lumpsum-budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LumpsumBudgetTableComponent {

  APPLICATION_FORM = APPLICATION_FORM;
  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;
  constructor(public projectPartnerDetailStore: ProjectPartnerDetailPageStore) {
  }

}
