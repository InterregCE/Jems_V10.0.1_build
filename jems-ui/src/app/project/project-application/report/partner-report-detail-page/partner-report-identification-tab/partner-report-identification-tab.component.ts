import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {PartnerReportDetailPageStore} from '../partner-report-detail-page-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectPartnerReportDTO,
  ProjectPartnerReportIdentificationDTO,
  ProjectPartnerReportIdentificationTargetGroupDTO,
  ProjectPartnerReportPeriodDTO,
  ProjectPartnerSummaryDTO, ProjectPeriodDTO,
} from '@cat/api';
import {catchError, filter, map, take, tap} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {NumberService} from '@common/services/number.service';

@Component({
  selector: 'jems-partner-report-identification-tab',
  templateUrl: './partner-report-identification-tab.component.html',
  styleUrls: ['./partner-report-identification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportIdentificationTabComponent {
  displayedColumns = ['periodBudget', 'currentReport', 'periodBudgetCumulative', 'totalReportedSoFar', 'differenceFromPlan', 'differenceFromPlanPercentage', 'nextReportForecast'];
  APPLICATION_FORM = APPLICATION_FORM;

  dateNameArgs = {
    startDate: 'start date',
    endDate: 'end date'
  };

  data$: Observable<{
    partnerReport: ProjectPartnerReportDTO;
    periods: ProjectPeriodDTO[];
    identification: ProjectPartnerReportIdentificationDTO;
    partnerSummary: ProjectPartnerSummaryDTO;
  }>;

  form: FormGroup = this.formBuilder.group({
    startDate: [''],
    endDate: [''],
    period: [null],
    summary: [[]],
    problemsAndDeviations: [[]],
    targetGroups: this.formBuilder.array([]),
    spendingDeviations: [[]],
    spendingProfile: this.formBuilder.group({
      currentReport: [0],
      previouslyReported: [0],
      differenceFromPlan: [0],
      differenceFromPlanPercentage: [0],
      nextReportForecast: [0],
    }),
  });

  inputErrorMessages = {
    matDatetimePickerParse: 'common.date.should.be.valid',
    matDatetimePickerMin: 'common.error.field.start.before.end',
    matDatetimePickerMax: 'common.error.field.end.after.start'
  };

  selectedPeriod: ProjectPartnerReportPeriodDTO | undefined = undefined;
  availablePeriods: ProjectPartnerReportPeriodDTO[] = [];

  constructor(public pageStore: PartnerReportDetailPageStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              private projectStore: ProjectStore,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
    this.data$ = combineLatest([
      pageStore.partnerReport$,
      projectStore.projectForm$,
      pageStore.availablePeriods$,
      pageStore.partnerIdentification$,
      pageStore.partnerSummary$
    ]).pipe(
      tap(([partnerReport, projectForm, availablePeriods, identification, partnerSummary]) =>
        this.availablePeriods = availablePeriods
      ),
      map(([partnerReport, projectForm, availablePeriods, identification, partnerSummary]) => ({
        partnerReport,
        periods: availablePeriods.map(p => ({...p, projectId: projectForm.id} as ProjectPeriodDTO)),
        identification,
        partnerSummary
      })),
      tap((data) => this.resetForm(data.identification)),
    );

    this.form.get('period')?.valueChanges.pipe(
      filter(period => !!period),
      tap(periodNumber => {
        this.selectedPeriod = this.availablePeriods.find(period => period.number === periodNumber);
        const currentTotal = NumberService.truncateNumber(NumberService.sum([this.spendingProfile.value.previouslyReported, this.spendingProfile.value.currentReport]), 2);
        const periodBudgetCumulative = NumberService.truncateNumber(this.selectedPeriod?.periodBudgetCumulative || 0, 2);
        const differenceFromPlan = NumberService.minus(periodBudgetCumulative, currentTotal);
        const differenceFromPlanPercentage = NumberService.roundNumber(NumberService.divide(NumberService.product([currentTotal, 100]), periodBudgetCumulative), 2);

        this.spendingProfile.get('differenceFromPlan')?.setValue(differenceFromPlan);
        this.spendingProfile.get('differenceFromPlanPercentage')?.setValue(differenceFromPlanPercentage);
      }),
    ).subscribe();

    this.formService.init(this.form, this.pageStore.reportEditable$);
  }

  get targetGroups(): FormArray {
    return this.form.get('targetGroups') as FormArray;
  }

  get spendingProfile(): FormGroup {
    return this.form.get('spendingProfile') as FormGroup;
  }

  resetForm(identification: ProjectPartnerReportIdentificationDTO) {
    this.form.patchValue(identification);
    this.form.patchValue({
      period: identification?.spendingProfile?.periodDetail?.number,
    });
    this.targetGroups.clear();

    if (identification.targetGroups) {
      identification.targetGroups.forEach((targetGroup: ProjectPartnerReportIdentificationTargetGroupDTO, index: number) => {
        this.resetTargetGroup(targetGroup, index);
      });
    }
    this.formService.resetEditable();
  }

  resetTargetGroup(targetGroup: ProjectPartnerReportIdentificationTargetGroupDTO, targetGroupIndex: number): void {
    this.targetGroups.push(this.formBuilder.group({
      description: this.formBuilder.control(targetGroup.description),
    }));
  }

  saveIdentification(): void {
    const data = {
      ...this.form.value,
      targetGroups: this.form.value.targetGroups?.map((tg: any) => tg.description),
      nextReportForecast: this.spendingProfile.get('nextReportForecast')?.value,
    };
    this.pageStore.saveIdentification(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.report.partner.identification.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }
}
