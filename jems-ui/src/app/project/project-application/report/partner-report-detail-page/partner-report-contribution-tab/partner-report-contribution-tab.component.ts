import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectPartnerContributionDTO,
  ProjectPartnerReportContributionDTO,
  ProjectPartnerReportContributionWrapperDTO,
  UpdateProjectPartnerReportContributionCustomDTO,
  UpdateProjectPartnerReportContributionDTO,
} from '@cat/api';
import {catchError, debounceTime, distinctUntilChanged, map, tap} from 'rxjs/operators';
import {
  PartnerReportContributionStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-contribution-tab/partner-report-contribution-store.service';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-partner-report-contribution-tab',
  templateUrl: './partner-report-contribution-tab.component.html',
  styleUrls: ['./partner-report-contribution-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportContributionTabComponent {

  PartnerContributionStatus = ProjectPartnerContributionDTO.StatusEnum;

  savedContribution$: Observable<ProjectPartnerReportContributionWrapperDTO>;
  toBeDeletedIds: number[] = [];

  contributionForm: FormGroup = this.formBuilder.group({
    contributions: this.formBuilder.array([]),
    overview: this.formBuilder.group({
      publicContribution: this.formBuilder.group({
        amount: this.formBuilder.control(0),
        previouslyReported: this.formBuilder.control(0),
        currentlyReported: this.formBuilder.control(0),
        totalReportedSoFar: this.formBuilder.control(0),
      }),
      automaticPublicContribution: this.formBuilder.group({
        amount: this.formBuilder.control(0),
        previouslyReported: this.formBuilder.control(0),
        currentlyReported: this.formBuilder.control(0),
        totalReportedSoFar: this.formBuilder.control(0),
      }),
      privateContribution: this.formBuilder.group({
        amount: this.formBuilder.control(0),
        previouslyReported: this.formBuilder.control(0),
        currentlyReported: this.formBuilder.control(0),
        totalReportedSoFar: this.formBuilder.control(0),
      }),
      total: this.formBuilder.group({
        amount: this.formBuilder.control(0),
        previouslyReported: this.formBuilder.control(0),
        currentlyReported: this.formBuilder.control(0),
        totalReportedSoFar: this.formBuilder.control(0),
      }),
    }),
  });

  columns: string[] = [];
  widths: TableConfig[] = [
    {},
    {},
    {maxInRem: 8, minInRem: 8},
    {maxInRem: 8, minInRem: 8},
    {maxInRem: 9, minInRem: 9},
    {maxInRem: 8, minInRem: 8},
    {maxInRem: 3, minInRem: 3},
  ];
  tableData: AbstractControl[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private projectSidenavService: ProjectApplicationFormSidenavService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private pageStore: PartnerReportContributionStore,
  ) {
    this.savedContribution$ = combineLatest([
      this.pageStore.partnerContribution$,
      this.partnerReportDetailPageStore.reportEditable$,
    ]).pipe(
      tap(([contribution]) => this.resetForm(contribution)),
      tap(([, editable]) => this.generateColumns(editable)),
      map(([contribution]) => contribution),
    );
    this.formService.init(this.contributionForm, this.partnerReportDetailPageStore.reportEditable$);
  }

  resetForm(contribution: ProjectPartnerReportContributionWrapperDTO) {
    this.contributions.clear();
    this.toBeDeletedIds = [];

    contribution.contributions.forEach((contrib: ProjectPartnerReportContributionDTO, index: number) => {
        this.addContribution(contrib);
    });
    this.overview.patchValue(contribution.overview);
    this.formService.resetEditable();
  }

  saveForm() {
    const toBeUpdated: UpdateProjectPartnerReportContributionDTO[] = this.getContributionsToUpdate(this.contributions)
    const toBeAdded: UpdateProjectPartnerReportContributionCustomDTO[] = this.getContributionsToAdd(this.contributions)

    this.pageStore.saveContribution(toBeUpdated, this.toBeDeletedIds, toBeAdded).pipe(
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe(contribution => {
      this.resetForm(contribution);
      this.formService.setSuccess('project.application.partner.report.contribution.save.success');
    });
  }

  get contributions(): FormArray {
    return this.contributionForm.get('contributions') as FormArray;
  }

  get overview(): FormGroup {
    return this.contributionForm.get('overview') as FormGroup;
  }

  addContribution(contrib: ProjectPartnerReportContributionDTO | null = null) {
    const item = this.formBuilder.group({
      id: this.formBuilder.control(contrib?.id),
      sourceOfContribution: this.formBuilder.control(contrib?.sourceOfContribution),
      legalStatus: this.formBuilder.control(contrib?.legalStatus),
      createdInThisReport: this.formBuilder.control(contrib ? contrib.createdInThisReport : true),
      amount: this.formBuilder.control(contrib?.numbers.amount || 0),
      previouslyReported: this.formBuilder.control(contrib?.numbers.previouslyReported || 0),
      currentlyReported: this.formBuilder.control(contrib?.numbers.currentlyReported || 0),
      totalReportedSoFar: this.formBuilder.control(contrib?.numbers.totalReportedSoFar || 0),
    });

    this.contributions.push(item);
    this.tableData = [...this.contributions.controls];
  }

  removeContribution(index: number) {
    const id = this.contributions.at(index).value.id;
    this.contributions.removeAt(index);
    if (id) {
      this.toBeDeletedIds.push(id);
    }
    this.tableData = [...this.contributions.controls];
  }

  private generateColumns(isEditable: boolean) {
    this.columns = ['sourceOfContribution', 'legalStatus', 'amount', 'previouslyReported', 'currentlyReported', 'totalReportedSoFar']
    if (isEditable) {
      this.columns.push('delete');
    }
  }

  private getContributionsToUpdate(contributions: FormArray): UpdateProjectPartnerReportContributionDTO[] {
    return contributions.controls
      .filter(contribution => contribution.value.id)
      .map(contribution => ({
        id: contribution.value.id,
        currentlyReported: contribution.value.currentlyReported,
        sourceOfContribution: contribution.value.sourceOfContribution,
        legalStatus: contribution.value.legalStatus,
      }));
  }

  private getContributionsToAdd(contributions: FormArray): UpdateProjectPartnerReportContributionCustomDTO[] {
    return contributions.controls
      .filter(contribution => !contribution.value.id)
      .map(contribution => ({
        currentlyReported: contribution.value.currentlyReported,
        sourceOfContribution: contribution.value.sourceOfContribution,
        legalStatus: contribution.value.legalStatus,
      }));
  }

}
