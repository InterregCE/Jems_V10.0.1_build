import {Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable, of} from 'rxjs';
import {
  ProjectReportSpfContributionClaimDTO, ProjectReportSpfContributionClaimUpdateDTO
} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {AbstractControl, FormArray, FormBuilder, Validators} from '@angular/forms';
import {
  ProjectReportSpfContributionsTabStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-spf-contributions-tab/project-report-spf-contributions-tab.store';
import {catchError, map, take, tap} from 'rxjs/operators';
import {MatTableDataSource} from '@angular/material/table';
import {ReportUtil} from '@project/common/report-util';

@Component({
  selector: 'jems-project-report-spf-contributions-tab',
  templateUrl: './project-report-spf-contributions-tab.component.html',
  styleUrls: ['./project-report-spf-contributions-tab.component.scss'],
  providers: [FormService],
})
export class ProjectReportSpfContributionsTabComponent {

  data$: Observable<{
    projectId: number;
    projectReportId: number;
    reportEditable: boolean;
    spfContributions: ProjectReportSpfContributionClaimDTO[];
  }>;
  dataSource: MatTableDataSource<AbstractControl>;
  displayedColumns: string[] = [
    'nameOfOrganization',
    'legalStatus',
    'amountInAf',
    'previouslyReported',
    'currentReport',
    'totalReported'
  ];

  form = this.formBuilder.group({
    contributions: this.formBuilder.array([]),
  });
  MAX_VALUE = 999_999_999.99;
  MIN_VALUE = -999_999_999.99;

  constructor(
    private projectStore: ProjectStore,
    private projectReportPageStore: ProjectReportPageStore,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
    private projectReportSpfContributionsTabStore: ProjectReportSpfContributionsTabStore,
    private formBuilder: FormBuilder,
    private formService: FormService,
  ) {
    this.data$ = combineLatest([
      this.projectReportDetailPageStore.projectReport$,
      this.projectReportDetailPageStore.reportEditable$,
      this.projectReportSpfContributionsTabStore.spfContributions$
    ]).pipe(
      map(([projectReport, reportEditable, spfContributions]) => ({
        projectId: projectReport.projectId,
        projectReportId: projectReport.id,
        reportEditable: reportEditable && !ReportUtil.isProjectReportLimitedReopened(projectReport.status),
        spfContributions,
      })),
      tap(data => this.formService.init(this.form, of(data.reportEditable))),
      tap(data => this.resetForm(data.spfContributions, data.reportEditable)),
    );
  }

  save() {
    this.projectReportSpfContributionsTabStore.updateSpfContributions(this.convertFormToDTO())
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.project.report.spf.contributions.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  resetForm(spfContributions: ProjectReportSpfContributionClaimDTO[], reportEditable: boolean) {
    this.contributions.clear();
    spfContributions.forEach(c => {
      this.contributions.push(this.formBuilder.group({
        id: c.id,
        programmeFund: [c.programmeFund?.abbreviation],
        sourceOfContribution: c.sourceOfContribution,
        legalStatus: c.legalStatus,
        amountInAf: c.amountInAf,
        previouslyReported: c.previouslyReported,
        currentReport: [c.currentlyReported, Validators.compose([
          Validators.min(this.MIN_VALUE),
          Validators.max(this.MAX_VALUE)])
        ],
        totalReported: c.totalReportedSoFar
      }));
    });
    this.dataSource = new MatTableDataSource<AbstractControl>(this.contributions.controls);
  }

  get contributions(): FormArray {
    return this.form.get('contributions') as FormArray;
  }

  getTotalSpfAmount(control: string): number {
    let totalAmount = 0;
    this.contributions.controls.forEach((element) => {
      totalAmount += element.get(control)?.value;
    });
    return totalAmount;
  }

  private convertFormToDTO(): ProjectReportSpfContributionClaimUpdateDTO[] {
    const claimUpdateDTOs = [];
    for (const item of this.contributions.getRawValue()) {
      claimUpdateDTOs.push({
        id: item.id,
        currentlyReported: item.currentReport,
      } as ProjectReportSpfContributionClaimUpdateDTO);
    }
    return claimUpdateDTOs;
  }

}
