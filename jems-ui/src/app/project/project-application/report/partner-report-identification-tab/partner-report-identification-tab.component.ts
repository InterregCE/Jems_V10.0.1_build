import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormBuilder, FormGroup} from '@angular/forms';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {
  ProjectPartnerReportPageStore
} from '@project/project-application/report/project-partner-report-page-store.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-partner-report-identification-tab',
  templateUrl: './partner-report-identification-tab.component.html',
  styleUrls: ['./partner-report-identification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportIdentificationTabComponent {

  form: FormGroup = this.formBuilder.group({
  });

  constructor(public partnerStore: ProjectPartnerStore,
              public projectStore: ProjectStore,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              public projectPartnerReportPageStore: ProjectPartnerReportPageStore,) {
    this.formService.init(this.form);
  }

}
