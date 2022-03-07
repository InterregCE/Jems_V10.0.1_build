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
  ProjectPeriodDTO
} from '@cat/api';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-partner-report-identification-tab',
  templateUrl: './partner-report-identification-tab.component.html',
  styleUrls: ['./partner-report-identification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportIdentificationTabComponent {
  APPLICATION_FORM = APPLICATION_FORM;

  dateNameArgs = {
    startDate: 'start date',
    endDate: 'end date'
  };

  data$: Observable<{
    partnerReport: ProjectPartnerReportDTO,
    periods: ProjectPeriodDTO[],
    identification: ProjectPartnerReportIdentificationDTO
  }>;

  form: FormGroup = this.formBuilder.group({
    startDate: [''],
    endDate: [''],
    period: [''],
    summary: [[]],
    problemsAndDeviations: [[]],
    targetGroups: this.formBuilder.array([]),
  });

  inputErrorMessages = {
    matDatetimePickerParse: 'common.date.should.be.valid',
    matDatetimePickerMin: 'common.error.field.start.before.end',
    matDatetimePickerMax: 'common.error.field.end.after.start'
  };

  constructor(public pageStore: PartnerReportDetailPageStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              private projectStore: ProjectStore,
              private projectSidenavService: ProjectApplicationFormSidenavService) {
    this.data$ = combineLatest([
      pageStore.partnerReport$,
      projectStore.projectForm$,
      pageStore.partnerIdentification$
    ]).pipe(
      map(([partnerReport, projectForm, identification]) => ({
        partnerReport,
        periods: projectForm.periods,
        identification
      })),
      tap((data) => this.resetForm(data.identification)),
    )
  }

  get targetGroups(): FormArray {
    return this.form.get('targetGroups') as FormArray;
  }

  resetForm(identification: ProjectPartnerReportIdentificationDTO) {
    this.form.reset();
    this.targetGroups.clear();
    this.form = this.formBuilder.group({
      startDate: [identification.startDate],
      endDate: [identification.endDate],
      period: [identification.period],
      summary: [identification.summary],
      problemsAndDeviations: [identification.problemsAndDeviations],
      targetGroups: this.formBuilder.array([]),
    });

    if (identification.targetGroups) {
      identification.targetGroups.forEach((targetGroup: ProjectPartnerReportIdentificationTargetGroupDTO, index: number) => {
        this.resetTargetGroup(targetGroup, index);
      });
    } else {
      this.formService.init(this.form, this.pageStore.isReportEditable());
    }
  }

  resetTargetGroup(targetGroup: ProjectPartnerReportIdentificationTargetGroupDTO, targetGroupIndex: number): void {
    this.targetGroups.push(this.formBuilder.group({
      description: this.formBuilder.control(targetGroup.description),
    }));

    this.formService.init(this.form, this.pageStore.isReportEditable());
  }

  saveIdentification(): void {
    const data = {
      ...this.form.value,
      targetGroups: this.form.value.targetGroups?.map((tg: any) => tg.description),
    };
    this.pageStore.saveIdentification(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.report.identification.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }
}
