import {Component, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {
  OutputWorkPackage,
  OutputWorkPackageSimple,
  ProjectPeriodDTO,
  WorkPackageActivityDTO,
  WorkPackageInvestmentDTO,
  WorkPackageOutputDTO
} from '@cat/api';
import {ProjectPartner} from '@project/model/ProjectPartner';
import {
  PartnerReportWorkpackagePanelConstantsConstants
} from '@project/project-application/report/partner-report-work-plan-progress-tab/partner-report-workpackage-panel/partner-report-workpackage-panel.constants';

@Component({
  selector: 'jems-partner-report-workpackage-panel',
  templateUrl: './partner-report-workpackage-panel.component.html',
  styleUrls: ['./partner-report-workpackage-panel.component.scss']
})
export class PartnerReportWorkpackagePanelComponent implements OnInit {

  constants = PartnerReportWorkpackagePanelConstantsConstants;

  @Input()
  workPackage: OutputWorkPackageSimple;

  constructor() { }

  data$: Observable<{
    outputs: WorkPackageOutputDTO[];
    activities: WorkPackageActivityDTO[];
    investments: WorkPackageInvestmentDTO[];
    objectives: OutputWorkPackage;
    periods: ProjectPeriodDTO[];
    partners: ProjectPartner[];
    projectTitle: string;
  }>;
  ngOnInit(): void {

  }

}
