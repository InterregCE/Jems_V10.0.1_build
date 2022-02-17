import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  ProjectPartnerReportPageStore
} from '@project/project-application/report/project-partner-report-page-store.service';
import {combineLatest, Observable} from 'rxjs';
import {
  OutputWorkPackage, OutputWorkPackageSimple,
  ProjectPeriodDTO,
  WorkPackageActivityDTO,
  WorkPackageInvestmentDTO,
  WorkPackageOutputDTO
} from '@cat/api';
import {ProjectPartner} from '@project/model/ProjectPartner';
import {map, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-partner-report-work-plan-progress-tab',
  templateUrl: './partner-report-work-plan-progress-tab.component.html',
  styleUrls: ['./partner-report-work-plan-progress-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportWorkPlanProgressTabComponent {
  data$: Observable<{
    workPackages: OutputWorkPackageSimple[];
  }>;

  form: FormGroup = this.formBuilder.group({
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              public projectPartnerReportPageStore: ProjectPartnerReportPageStore) {

    // this.data$ = combineLatest([
    //   this.projectPartnerReportPageStore.getWorkPackages(),
    // ])
    //   .pipe(
    //     map(([workPackages]) => ({
    //       workPackages: workPackages
    //     }))
    //   );
  }

}
