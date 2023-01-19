import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  InputTranslation,
  ProjectReportIdentificationDTO, ProjectReportIdentificationTargetGroupDTO
} from '@cat/api';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {LanguageStore} from '@common/services/language-store.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
  ProjectReportIdentificationExtensionStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-identification-tab/project-report-identification-extension/project-report-identification-extension-store.service';

@Component({
  selector: 'jems-project-report-identification-extension',
  templateUrl: './project-report-identification-extension.component.html',
  styleUrls: ['./project-report-identification-extension.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportIdentificationExtensionComponent {
  LANGUAGE = InputTranslation.LanguageEnum;
  reportIdentification$: Observable<ProjectReportIdentificationDTO>;

  form: FormGroup = this.formBuilder.group({
    highlightsEn: [],
    highlights: [[]],
    partnerProblems: [[]],
    deviations: [[]],
    targetGroups: this.formBuilder.array([]),
  });

  constructor(public pageStore: ProjectReportDetailPageStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              public languageStore: LanguageStore,
              private identificationExtensionStore: ProjectReportIdentificationExtensionStore) {
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

  resetForm(reportIdentification: ProjectReportIdentificationDTO) {
    this.form.patchValue(reportIdentification);
    this.targetGroups.clear();

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
