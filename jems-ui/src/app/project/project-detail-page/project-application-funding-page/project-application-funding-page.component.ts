import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectFundingDecisionStore} from './project-funding-decision-store.service';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {combineLatest} from 'rxjs';

@Component({
  selector: 'app-project-application-funding-page',
  templateUrl: './project-application-funding-page.component.html',
  styleUrls: ['./project-application-funding-page.component.scss'],
  providers: [ProjectFundingDecisionStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFundingPageComponent {

  projectId = this.activatedRoute.snapshot.params.projectId;

  details$ = combineLatest([
    this.fundingDecisionStore.project$,
    this.fundingDecisionStore.fundingDecision$,
    this.fundingDecisionStore.eligibilityDecisionDate$,
  ])
    .pipe(
      map(([project, fundingDecision, eligibilityDecisionDate]) => ({
        project,
        fundingDecision,
        eligibilityDecisionDate,
        acronym: project.acronym,
        statusOptions: [
          ProjectStatusDTO.StatusEnum.APPROVED,
          ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS,
          ProjectStatusDTO.StatusEnum.NOTAPPROVED
        ],
        showWithConditions: fundingDecision?.status === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS,
        withConditionsStatus: this.getApprovedWithConditionsStatus(project),
        withConditionsStatusOptions: [
          ProjectStatusDTO.StatusEnum.APPROVED,
          ProjectStatusDTO.StatusEnum.NOTAPPROVED
        ],
        submitLabel: 'project.assessment.fundingDecision.submit.label',
        withConditionsSubmitLabel: 'project.assessment.fundingDecision.submit.finalize.label',
      }))
    )
  ;

  constructor(public projectStore: ProjectStore,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public fundingDecisionStore: ProjectFundingDecisionStore,
              private sidenavService: ProjectApplicationFormSidenavService) {
  }

  private getApprovedWithConditionsStatus(project: ProjectDetailDTO): ProjectStatusDTO | null {
    return [
      ProjectStatusDTO.StatusEnum.APPROVED,
      ProjectStatusDTO.StatusEnum.NOTAPPROVED
    ]
      .some(stat => stat === project.projectStatus.status) ? project.projectStatus : null;
  }
}
