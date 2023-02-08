import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  InputTranslation,
  ProjectPeriodDTO, ProjectReportDTO, ProjectReportIdentificationDTO, ProjectReportUpdateDTO
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {catchError, filter, map, take, tap} from 'rxjs/operators';
import { APPLICATION_FORM } from '@project/common/application-form-model';
import { ProjectReportDetailPageStore } from '../project-report-detail-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {LanguageStore} from '@common/services/language-store.service';

@Component({
  selector: 'jems-project-report-identification-tab',
  templateUrl: './project-report-identification-tab.component.html',
  styleUrls: ['./project-report-identification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportIdentificationTabComponent {
  APPLICATION_FORM = APPLICATION_FORM;
  ProjectReportDTO = ProjectReportDTO;

  public reportId = this.router.getParameter(this.activatedRoute, 'reportId');

  data$: Observable<{
    projectReport: ProjectReportDTO;
    periods: ProjectPeriodDTO[];
  }>;

  form: FormGroup = this.formBuilder.group({
    startDate: [''],
    endDate: [''],
    periodNumber: [null, Validators.required],
    deadlineId: [null, Validators.required],
    type:[null, Validators.required],
    reportingDate: ['', Validators.required],
  });

  dateNameArgs = {
    startDate: 'start date',
    endDate: 'end date'
  };

  inputErrorMessages = {
    matDatetimePickerParse: 'common.date.should.be.valid',
    matDatetimePickerMin: 'common.error.field.start.before.end',
    matDatetimePickerMax: 'common.error.field.end.after.start'
  };

  selectedType: ProjectReportDTO.TypeEnum;
  selectedPeriod: ProjectPeriodDTO | undefined = undefined;
  availablePeriods: ProjectPeriodDTO[] = [];
  availableDeadlines: string[] = [];

  constructor(public pageStore: ProjectReportDetailPageStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              private projectStore: ProjectStore,
              private router: RoutingService,
              private activatedRoute: ActivatedRoute,
              private projectReportPageStore: ProjectReportPageStore,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              public languageStore: LanguageStore) {
    this.formService.init(this.form, this.reportId ? this.pageStore.reportEditable$ : this.projectReportPageStore.userCanEditReport$,);
    this.data$ = combineLatest([
      pageStore.projectReport$,
      this.projectStore.projectPeriods$,
    ]).pipe(
      tap(([projectReport, availablePeriods]) =>
        this.availablePeriods = availablePeriods
      ),
      map(([projectReport, availablePeriods]) => ({
        projectReport,
        periods: availablePeriods,
      })),
      tap((data) => this.resetForm(data.projectReport)),
    );

    this.form.get('period')?.valueChanges.pipe(
      filter(period => !!period),
      tap(periodNumber => this.selectedPeriod = this.availablePeriods.find(period => period.number === periodNumber)),
    ).subscribe();
  }

  resetForm(identification?: ProjectReportDTO) {
    if (!this.reportId) {
      this.formService.setCreation(true);
    }
    if (identification) {
      this.form.patchValue(identification);
      this.selectedType = identification.type;
    }
    this.form.patchValue({
      periodNumber: identification?.periodDetail?.number,
    });
    if (identification?.deadlineId === null || !this.reportId){
      this.form.get('deadlineId')?.patchValue(0);
    }
  }

  saveBaseInformation(): void {
    const data = {
      ...this.form.value,
      deadlineId: this.form.get('deadlineId')?.value < 1 ? null : this.form.get('deadlineId')?.value,
    } as ProjectReportUpdateDTO;
    if (!this.reportId) {
      this.projectReportPageStore.createProjectReport(data)
        .pipe(
          take(1),
          tap(created => this.redirectToProjectReportDetail(created)),
          catchError(err => this.formService.setError(err))
        ).subscribe();
    } else {
      this.pageStore.saveIdentification(data)
        .pipe(
          take(1),
          tap(() => this.formService.setSuccess('project.application.project.report.identification.saved')),
          catchError(err => this.formService.setError(err))
        ).subscribe();
    }
  }

  discard(report?: ProjectReportDTO): void {
    if (!this.reportId) {
      this.redirectToProjectReportsOverview();
    } else {
      this.resetForm(report);
    }
  }

  private redirectToProjectReportsOverview(): void {
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  private redirectToProjectReportDetail(report: any): void {
    this.router.navigate(
      ['..', report.id, 'identification'],
      {relativeTo: this.activatedRoute}
    );
  }

}
