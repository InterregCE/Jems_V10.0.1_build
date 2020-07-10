import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {Forms} from '../../../../../common/utils/forms';
import {take, takeUntil} from 'rxjs/internal/operators';
import {MatDialog} from '@angular/material/dialog';
import {BaseComponent} from '@common/components/base-component';
import {OutputProject} from '@cat/api';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-project-application-assessment',
  templateUrl: './project-application-assessment.component.html',
  styleUrls: ['./project-application-assessment.component.scss']
})
export class ProjectApplicationAssessmentComponent extends BaseComponent implements OnInit {
  note: string;
  projectId = this.activatedRoute.snapshot.params.projectId;

  @Input()
  project: OutputProject;

  constructor(
    private dialog: MatDialog,
    private router: Router,
    private activatedRoute: ActivatedRoute) {
    super();
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    console.log("proj id", this.projectId);
    this.confirmRoleChange();
  }

  private confirmRoleChange(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.eligibilityCheck.dialog.title',
      'project.assessment.eligibilityCheck.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$)
    ).subscribe(selectEligibility => {
      const selectedEligibility = selectEligibility
        ? this.project
        : null
      if (selectEligibility) {
        this.router.navigate(['project', this.projectId]);
        console.log('Selected Eligibility')
      }
    });
  }
}
