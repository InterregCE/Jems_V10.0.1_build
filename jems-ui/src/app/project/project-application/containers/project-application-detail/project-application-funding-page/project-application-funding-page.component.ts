import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectStore} from '../services/project-store.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Permission} from '../../../../../security/permissions/permission';
import {InputProjectStatus, OutputProject, OutputProjectStatus} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {map, takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-funding-page',
  templateUrl: './project-application-funding-page.component.html',
  styleUrls: ['./project-application-funding-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFundingPageComponent extends BaseComponent implements OnInit {
  Permission = Permission;
  OutputProjectStatus = OutputProjectStatus;

  projectId = this.activatedRoute.snapshot.params.projectId;
  details$ = this.projectStore.getProject()
    .pipe(
      takeUntil(this.destroyed$),
      map(project => ({
        project,
        acronym: project.acronym,
        status: project.fundingDecision,
        statusOptions: [
          InputProjectStatus.StatusEnum.APPROVED,
          InputProjectStatus.StatusEnum.APPROVEDWITHCONDITIONS,
          InputProjectStatus.StatusEnum.NOTAPPROVED
        ],
        submitLabel: 'project.assessment.fundingDecision.submit.label',
        showWithConditions: project.fundingDecision?.status === OutputProjectStatus.StatusEnum.APPROVEDWITHCONDITIONS,
        withConditionsStatus: this.getApprovedWithConditionsStatus(project),
        withConditionsStatusOptions: [
          InputProjectStatus.StatusEnum.APPROVED,
          InputProjectStatus.StatusEnum.NOTAPPROVED
        ],
        withConditionsSubmitLabel: 'project.assessment.fundingDecision.submit.finalize.label',
      }))
    )
  ;

  constructor(public projectStore: ProjectStore,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
    super();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
  }

  redirectToProject(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

  private getApprovedWithConditionsStatus(project: OutputProject): OutputProjectStatus | null {
    return [
      OutputProjectStatus.StatusEnum.APPROVED,
      OutputProjectStatus.StatusEnum.NOTAPPROVED
    ]
      .some(stat => stat === project.projectStatus.status) ? project.projectStatus : null;
  }
}
