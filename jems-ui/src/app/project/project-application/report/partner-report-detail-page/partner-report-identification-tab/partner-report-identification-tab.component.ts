import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {PartnerReportDetailPageStore} from '../partner-report-detail-page-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';

@Component({
  selector: 'jems-partner-report-identification-tab',
  templateUrl: './partner-report-identification-tab.component.html',
  styleUrls: ['./partner-report-identification-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportIdentificationTabComponent {
  APPLICATION_FORM = APPLICATION_FORM;

  constructor(public pageStore: PartnerReportDetailPageStore,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
  }

}
