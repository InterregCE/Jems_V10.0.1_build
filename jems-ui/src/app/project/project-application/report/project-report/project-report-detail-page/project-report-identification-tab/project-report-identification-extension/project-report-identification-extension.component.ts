import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
    InputTranslation,
    ProjectReportDTO,
    ProjectReportIdentificationDTO,
    ProjectReportIdentificationTargetGroupDTO,
    ProjectReportSpendingProfileLineDTO
} from '@cat/api';
import {AbstractControl, FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {
    ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {LanguageStore} from '@common/services/language-store.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
    ProjectReportIdentificationExtensionStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-identification-tab/project-report-identification-extension/project-report-identification-extension-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-project-report-identification-extension',
  templateUrl: './project-report-identification-extension.component.html',
  styleUrls: ['./project-report-identification-extension.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportIdentificationExtensionComponent implements OnInit {
  APPLICATION_FORM = APPLICATION_FORM;
  LANGUAGE = InputTranslation.LanguageEnum;
  TYPE_ENUM = ProjectReportDTO.TypeEnum;

  readonly Alert = Alert;

  private tableData: AbstractControl[] = [];

  @Input()
  reportType: ProjectReportDTO.TypeEnum;

  form: FormGroup = this.formBuilder.group({
    highlightsEn: [],
    highlights: [[]],
    partnerProblems: [[]],
    deviations: [[]],
    targetGroups: this.formBuilder.array([]),
    spendingProfiles: this.formBuilder.array([])
  });


  data$: Observable< {
      projectReportIdentification: ProjectReportIdentificationDTO;
      spendingProfileTable: {
          data: AbstractControl[];
          tableConfig: TableConfig[];
          tableColumns: string[];
      };
      partnerBudgetPeriodsVisible: boolean;
      isSpendingProfileTotalEligibleSetAtReportCreation: boolean;
  }>;


  constructor(public pageStore: ProjectReportDetailPageStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              public languageStore: LanguageStore,
              private identificationExtensionStore: ProjectReportIdentificationExtensionStore,
              private visibilityStatusService: FormVisibilityStatusService,) {
  }

  ngOnInit(): void {
    this.data$ =  combineLatest([
      this.identificationExtensionStore.projectReportIdentification$,
      this.visibilityStatusService.isVisible$(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS),
      this.pageStore.reportEditable$,
    ]).pipe(
         tap(([projectReportIdentification, partnerBudgetPeriodsVisible, reportEditable]) => this.resetForm(projectReportIdentification)),
         map(([projectReportIdentification, partnerBudgetPeriodsVisible, reportEditable]) => ({
             projectReportIdentification,
             spendingProfileTable: {
                 data: this.tableData,
                 tableConfig: this.getSpendingProfileTableConfig(partnerBudgetPeriodsVisible),
                 tableColumns: this.getSpendingProfileTableColumns(partnerBudgetPeriodsVisible),
             },
             partnerBudgetPeriodsVisible,
             isSpendingProfileTotalEligibleSetAtReportCreation: projectReportIdentification.spendingProfilePerPartner.total.totalEligibleBudget > 0
         }))
     );
    this.formService.init(this.form,  this.pageStore.reportEditable$);
  }

  get targetGroups(): FormArray {
    return this.form.get('targetGroups') as FormArray;
  }

  get spendingProfiles(): FormArray {
    return this.form.get('spendingProfiles') as FormArray;
  }

  resetForm(reportIdentification: ProjectReportIdentificationDTO) {
    this.form.patchValue(reportIdentification);
    this.targetGroups.clear();
    this.spendingProfiles.clear();

    if (!this.languageStore.isInputLanguageExist(this.LANGUAGE.EN)) {
      const enValue = reportIdentification.highlights.find(h => h.language === this.LANGUAGE.EN) ?
        [reportIdentification.highlights.find(h => h.language === this.LANGUAGE.EN)] :
        [{language : this.LANGUAGE.EN, translation : ''} as InputTranslation];
      this.form.controls.highlightsEn.setValue(enValue);
      this.form.controls.highlights.setValue(reportIdentification.highlights.filter(h => h.language !== this.LANGUAGE.EN));
    }

    if (reportIdentification.targetGroups) {
      reportIdentification.targetGroups.forEach((targetGroup: ProjectReportIdentificationTargetGroupDTO) => {
        this.targetGroups.push(this.formBuilder.group({
          description: this.formBuilder.control(targetGroup.description),
        }));
      });
    }

    if (reportIdentification.spendingProfilePerPartner.lines) {
      reportIdentification.spendingProfilePerPartner.lines.forEach((spendingProfile: ProjectReportSpendingProfileLineDTO) => {
        this.spendingProfiles.push(this.formBuilder.group({
            partnerRole: this.formBuilder.control(spendingProfile.partnerRole),
            partnerNumber: this.formBuilder.control(spendingProfile.partnerNumber),
            partnerAbbreviation: this.formBuilder.control(spendingProfile.partnerAbbreviation),
            partnerCountry: this.formBuilder.control(spendingProfile.partnerCountry),
            totalEligibleBudget: this.formBuilder.control(spendingProfile.totalEligibleBudget),
            previouslyReported: this.formBuilder.control(spendingProfile.previouslyReported),
            currentReport: this.formBuilder.control(spendingProfile.currentReport),
            totalReportedSoFar: this.formBuilder.control(spendingProfile.totalReportedSoFar),
            totalReportedSoFarPercentage: this.formBuilder.control(spendingProfile.totalReportedSoFarPercentage),
            remainingBudget: this.formBuilder.control(spendingProfile.remainingBudget),
            differenceFromPlan: this.formBuilder.control(spendingProfile.differenceFromPlan),
            differenceFromPlanPercentage: this.formBuilder.control(spendingProfile.differenceFromPlanPercentage),
            nextReportForecast: this.formBuilder.control(spendingProfile.nextReportForecast),
            periodBudget: this.formBuilder.control(spendingProfile.periodBudget ? spendingProfile.periodBudget : 0.00),
            periodBudgetCumulative: this.formBuilder.control(spendingProfile.periodBudgetCumulative? spendingProfile.periodBudgetCumulative : 0.00),
        }));
      });
    }
    this.tableData = [...this.spendingProfiles.controls];
    this.formService.resetEditable();
  }

  saveIdentificationExtension() {
    const data = {
      targetGroups: this.form.value.targetGroups?.map((tg: any) => tg.description),
      highlights: this.form.value.highlightsEn ? this.form.value.highlights.concat(this.form.value.highlightsEn) : this.form.value.highlights,
      partnerProblems: this.form.value.partnerProblems,
      deviations: this.form.value.deviations
    };
    this.identificationExtensionStore.saveIdentificationExtension(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.report.partner.identification.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  getSpendingProfileTableColumns(partnerBudgetPeriodsVisible: boolean): string[] {
      return [
          'partnerNumber',
          'partnerAbbreviation',
          'partnerCountry',
          'totalEligibleBudget',
          'previouslyReported',
          'currentReport',
          'totalReportedSoFar',
          'totalReportedSoFarPercentage',
          'remainingBudget',
          ... partnerBudgetPeriodsVisible ?
              ['periodBudget', 'periodBudgetCumulative', 'differenceFromPlan', 'differenceFromPlanPercentage', 'nextReportForecast'] : []
      ];
  }

    getSpendingProfileTableConfig(partnerBudgetPeriodsVisible: boolean): TableConfig[] {
      return [
          {minInRem: 4},
          {minInRem: 12},
          {minInRem: 4},
          {minInRem: 8},
          {minInRem: 8},
          {minInRem: 8},
          {minInRem: 8},
          {minInRem: 8},
          {minInRem: 8},
          ...partnerBudgetPeriodsVisible ?
              [{minInRem: 8}, {minInRem: 8}, {minInRem: 8}, {minInRem: 8}, {minInRem: 8}] : []
      ];
    }
}
