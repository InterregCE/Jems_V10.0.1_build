import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {map} from 'rxjs/operators';
import {ProjectFundingDecisionStore} from './project-funding-decision-store.service';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest} from 'rxjs';
import {ProjectStepStatus} from '../project-step-status';
import {ProjectStatusDTO} from '@cat/api';
import StatusEnum = ProjectStatusDTO.StatusEnum;

@Component({
  selector: 'app-project-application-funding-page',
  templateUrl: './project-application-funding-page.component.html',
  styleUrls: ['./project-application-funding-page.component.scss'],
  providers: [ProjectFundingDecisionStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFundingPageComponent {

  projectId = this.activatedRoute.snapshot.params.projectId;
  step = this.activatedRoute.snapshot.params.step;
  stepStatus = new ProjectStepStatus(this.step);

  details$ = combineLatest([
    this.fundingDecisionStore.project$,
    this.fundingDecisionStore.preFundingDecision(this.step),
    this.fundingDecisionStore.finalFundingDecision(this.step),
    this.fundingDecisionStore.eligibilityDecisionDate(this.step),
  ])
    .pipe(
      map(([project, preFundingDecision, finalFundingDecision, eligibilityDecisionDate]) => ({
        project,
        preFundingDecision,
        finalFundingDecision,
        eligibilityDecisionDate,
        showSecondDecision: !!project.secondStepDecision?.preFundingDecision && project.projectStatus.status !== StatusEnum.RETURNEDTOAPPLICANT,
        fullOptions: [this.stepStatus.approved, this.stepStatus.approvedWithConditions, this.stepStatus.notApproved],
        optionsForSecondDecision: [this.stepStatus.approved, this.stepStatus.notApproved],
      })),
    )
  ;

  constructor(public projectStore: ProjectStore,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public fundingDecisionStore: ProjectFundingDecisionStore) {
  }

}
