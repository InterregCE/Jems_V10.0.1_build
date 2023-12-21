import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
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
    ProjectPartnerReportDTO
} from '@cat/api';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {
    PartnerReportContributionStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-contribution-tab/partner-report-contribution-store.service';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {NumberService} from '@common/services/number.service';
import {
    PartnerFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-file-management-store';
import {RoutingService} from '@common/services/routing.service';
import {v4 as uuid} from 'uuid';

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
  MAX_VALUE = 999_999_999.99;
  MIN_VALUE = -999_999_999.99;

  savedContribution$: Observable<ProjectPartnerReportContributionWrapperDTO>;
  toBeDeletedIds: number[] = [];
  columns: string[] = [];
  widths: TableConfig[] = [
    {minInRem: 10},  // name of org/contribution
    {minInRem:  8},  // legal status
    {minInRem:  8},  // amount in AF
    {minInRem:  8},  // previously reported
    {minInRem:  9},  // current report
    {minInRem:  8},  // total reported
    {minInRem: 15},  // attachment
    {minInRem:  3}   // actions
  ];
  tableData: AbstractControl[] = [];

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
  isUploadDone = false;
  isReportReopenedLimited = false;
  isDirectContributionsAllowed = true;

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private projectSidenavService: ProjectApplicationFormSidenavService,
    public partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private pageStore: PartnerReportContributionStore,
    private partnerFileManagementStore: PartnerFileManagementStore,
    private routingService: RoutingService
  ) {
    this.formService.init(this.contributionForm, this.partnerReportDetailPageStore.reportEditable$);
    this.savedContribution$ = combineLatest([
      this.pageStore.partnerContribution$,
      this.partnerReportDetailPageStore.reportEditable$,
      this.pageStore.currentReport$,
      this.pageStore.directContributionsAllowed$
    ]).pipe(
      tap(([contribution,,currentReport, directContributionsAllowed]) => {
        this.isReportReopenedLimited = currentReport.status === ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLimited || currentReport.status === ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited;
        this.isDirectContributionsAllowed = directContributionsAllowed;
        this.resetForm(contribution);
      }),
      tap(([,editable]) => this.generateColumns(editable)),
      map(([contribution]) => contribution),
    );
  }

  resetForm(contribution: ProjectPartnerReportContributionWrapperDTO) {
    this.contributions.clear();
    this.toBeDeletedIds = [];

    contribution.contributions.forEach(contrib => this.addContribution(contrib));
    this.overview.patchValue(contribution.overview);
    this.formService.resetEditable();
    if (this.isReportReopenedLimited) {
      for (const element of this.contributions.controls) {
        element.get('currentlyReported')?.disable();
      }
    }
  }

  saveForm() {
    if (this.isReportReopenedLimited) {
      for (const element of this.contributions.controls) {
        element.get('currentlyReported')?.enable();
      }
    }

    this.pageStore.saveContribution({
      toBeUpdated: this.contributions.controls.filter(contribution => contribution.value.id).map(contribution => contribution.value),
      toBeDeletedIds: this.toBeDeletedIds,
      toBeCreated: this.contributions.controls.filter(contribution => !contribution.value.id).map(contribution => contribution.value),
    }).pipe(
      tap(() => this.formService.setSuccess('project.application.partner.report.contribution.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  get contributions(): FormArray {
    return this.contributionForm.get('contributions') as FormArray;
  }

  get overview(): FormGroup {
    return this.contributionForm.get('overview') as FormGroup;
  }

  addContribution(contrib: ProjectPartnerReportContributionDTO | null = null) {
    const createdInThisReport = contrib ? contrib.createdInThisReport : true;

    const item = this.formBuilder.group({
      id: this.formBuilder.control(contrib?.id || 0),
      sourceOfContribution: this.formBuilder.control(contrib?.sourceOfContribution, createdInThisReport
        ? Validators.compose([Validators.maxLength(255), Validators.required, Validators.pattern(/(?!^\s+$)^.*$/m)])
        : []),
      legalStatus: this.formBuilder.control(contrib?.legalStatus, createdInThisReport ? Validators.required : []),
      createdInThisReport: this.formBuilder.control(createdInThisReport),
      amount: this.formBuilder.control(contrib?.numbers.amount || 0),
      previouslyReported: this.formBuilder.control(contrib?.numbers.previouslyReported || 0),
      currentlyReported: this.formBuilder.control(contrib?.numbers.currentlyReported || 0),
      totalReportedSoFar: this.formBuilder.control(contrib?.numbers.totalReportedSoFar || 0),
      removedInAf: this.formBuilder.control(contrib?.removedInAf),
      attachment: this.formBuilder.control(contrib?.attachment || null),
    });

    this.contributions.push(item);
    this.tableData = [...this.contributions.controls];
    this.formService.setDirty(true);
  }

  removeContribution(index: number) {
    const id = this.contributions.at(index).value.id;
    this.contributions.removeAt(index);
    if (id) {
      this.toBeDeletedIds.push(id);
    }
    this.tableData = [...this.contributions.controls];
    this.formService.setDirty(true);
    this.totalsChanged();
  }

  totalsChanged(): void {
    const currentlyPublic = this.getTotalsForStatus(ProjectPartnerContributionDTO.StatusEnum.Public);
    const publicContribution = this.overview.get('publicContribution');
    publicContribution?.get('currentlyReported')?.patchValue(currentlyPublic);
    publicContribution?.get('totalReportedSoFar')?.patchValue(
      NumberService.sum([currentlyPublic, publicContribution?.get('previouslyReported')?.value || 0])
    );

    const currentlyAutomatic = this.getTotalsForStatus(ProjectPartnerContributionDTO.StatusEnum.AutomaticPublic);
    const automaticContribution = this.overview.get('automaticPublicContribution');
    automaticContribution?.get('currentlyReported')?.patchValue(currentlyAutomatic);
    automaticContribution?.get('totalReportedSoFar')?.patchValue(
      NumberService.sum([currentlyAutomatic, automaticContribution?.get('previouslyReported')?.value || 0])
    );

    const currentlyPrivate = this.getTotalsForStatus(ProjectPartnerContributionDTO.StatusEnum.Private);
    const privateContribution = this.overview.get('privateContribution');
    privateContribution?.get('currentlyReported')?.patchValue(currentlyPrivate);
    privateContribution?.get('totalReportedSoFar')?.patchValue(
      NumberService.sum([currentlyPrivate, privateContribution?.get('previouslyReported')?.value || 0])
    );

    this.overview.get('total')?.get('currentlyReported')?.patchValue(
      NumberService.sum([currentlyPublic, currentlyAutomatic, currentlyPrivate])
    );
    this.overview.get('total')?.get('totalReportedSoFar')?.patchValue(NumberService.sum([
      (publicContribution?.get('totalReportedSoFar')?.value || 0),
      (automaticContribution?.get('totalReportedSoFar')?.value || 0),
       (privateContribution?.get('totalReportedSoFar')?.value || 0)
    ]));
  }

  attachment(index: number): FormControl {
    return this.contributions.at(index).get('attachment') as FormControl;
  }

  onUploadFile(target: any, procurementId: number, index: number): void {
    if (target && procurementId !== 0) {
      this.isUploadDone = false;
      const serviceId = uuid();
      this.routingService.confirmLeaveMap.set(serviceId, true);
      this.pageStore.uploadFile(target?.files[0], procurementId)
        .pipe(
          take(1),
          catchError(err => this.formService.setError(err)),
          finalize(() => this.isUploadDone = true)
    )
        .subscribe(value => {
          this.attachment(index)?.patchValue(value);
          this.routingService.confirmLeaveMap.delete(serviceId);
        });
    }
  }

  onDeleteFile(fileId: number, index: number): void {
    this.partnerFileManagementStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(_ => this.attachment(index)?.patchValue(null));
  }

  onDownloadFile(fileId: number): void {
    this.partnerFileManagementStore.downloadFile(fileId).pipe(take(1)).subscribe();
  }

  private getTotalsForStatus(status: ProjectPartnerContributionDTO.StatusEnum): number {
    return NumberService.sum(this.contributions.controls
      .filter(contribution => contribution.get('legalStatus')?.value === status)
      .map(contribution => contribution.get('currentlyReported')?.value || 0)
    );
  }

  private generateColumns(isEditable: boolean) {
    this.columns = ['sourceOfContribution', 'legalStatus', 'amount', 'previouslyReported', 'currentlyReported', 'totalReportedSoFar', 'attachment'];
    if (isEditable) {
      this.columns.push('delete');
    }
  }

  refreshContributions(): void {
    this.pageStore.refreshContributions$.next(undefined);
  }
}
