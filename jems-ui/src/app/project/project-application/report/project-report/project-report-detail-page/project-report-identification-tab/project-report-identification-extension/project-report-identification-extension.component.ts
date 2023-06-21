import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  InputTranslation, ProjectReportDTO,
  ProjectReportIdentificationDTO, ProjectReportIdentificationTargetGroupDTO, ProjectReportSpendingProfileDTO
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
  reportIdentification$: Observable<ProjectReportIdentificationDTO>;
  displayedColumns = ['partnerNumber', 'periodBudget', 'currentReport', 'periodBudgetCumulative', 'totalReportedSoFar', 'differenceFromPlan', 'differenceFromPlanPercentage', 'nextReportForecast'];
  tableData: AbstractControl[] = [];

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

  constructor(public pageStore: ProjectReportDetailPageStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              public languageStore: LanguageStore,
              private identificationExtensionStore: ProjectReportIdentificationExtensionStore) {
  }

  ngOnInit(): void {
    this.reportIdentification$ = combineLatest([
      this.identificationExtensionStore.projectReportIdentification$,
      this.pageStore.reportEditable$,
    ]).pipe(
      map(([projectReportIdentification, _]) => (projectReportIdentification)),
      tap((data) => this.resetForm(data)),
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

    if (reportIdentification.spendingProfiles) {
      reportIdentification.spendingProfiles.forEach((spendingProfile: ProjectReportSpendingProfileDTO) => {
        this.spendingProfiles.push(this.formBuilder.group({
          partnerRole: this.formBuilder.control(spendingProfile.partnerRole),
          partnerNumber: this.formBuilder.control(spendingProfile.partnerNumber),
          currentReport: this.formBuilder.control(spendingProfile.currentReport),
          previouslyReported: this.formBuilder.control(spendingProfile.previouslyReported),
          differenceFromPlan: this.formBuilder.control(spendingProfile.differenceFromPlan),
          differenceFromPlanPercentage: this.formBuilder.control(spendingProfile.differenceFromPlanPercentage),
          nextReportForecast: this.formBuilder.control(spendingProfile.nextReportForecast),
          periodBudget: this.formBuilder.control(spendingProfile.periodDetail?.periodBudget),
          periodBudgetCumulative: this.formBuilder.control(spendingProfile.periodDetail?.periodBudgetCumulative),
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
}
