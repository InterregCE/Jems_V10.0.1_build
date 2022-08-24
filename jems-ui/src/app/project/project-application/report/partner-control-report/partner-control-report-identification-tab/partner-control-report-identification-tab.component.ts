import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectContractingMonitoringDTO,
  ProjectDetailDTO,
  ProjectPartnerControlReportDTO,
  ProjectPartnerDetailDTO
} from '@cat/api';
import {FormBuilder, FormGroup} from '@angular/forms';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
  ContractMonitoringExtensionStore
} from '@project/project-application/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.store';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';
import {SelectionModel} from '@angular/cdk/collections';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

@Component({
  selector: 'jems-partner-control-report-identification-tab',
  templateUrl: './partner-control-report-identification-tab.component.html',
  styleUrls: ['./partner-control-report-identification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportIdentificationTabComponent {

  ProjectPartnerControlReportDTO = ProjectPartnerControlReportDTO;

  data$: Observable<{
    partnerControlReport: ProjectPartnerControlReportDTO;
    project: ProjectDetailDTO;
    contracting: ProjectContractingMonitoringDTO;
    partner: ProjectPartnerDetailDTO;
  }>;

  form: FormGroup = this.formBuilder.group({
    formats: [[]],
    partnerType: []
  });

  selection = new SelectionModel<ProjectPartnerControlReportDTO.ControllerFormatsEnum>(true, []);

  formats = [
    ProjectPartnerControlReportDTO.ControllerFormatsEnum.Originals,
    ProjectPartnerControlReportDTO.ControllerFormatsEnum.Copy,
    ProjectPartnerControlReportDTO.ControllerFormatsEnum.Electronic
  ];

  constructor(public pageStore: PartnerReportDetailPageStore,
              public store: PartnerControlReportStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              private projectStore: ProjectStore,
              private contractMonitoringExtensionStore: ContractMonitoringExtensionStore,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              private localeDatePipe: LocaleDatePipe) {
    this.data$ = combineLatest([
      store.partnerControlReport$,
      projectStore.project$,
      contractMonitoringExtensionStore.projectContractingMonitoring$,
      store.partner$
    ]).pipe(
      map(([partnerControlReport, project, contracting, partner]) => ({
        partnerControlReport,
        project,
        contracting,
        partner
      })),
      tap((data) => this.resetForm(data.partnerControlReport)),
    );

    this.formService.init(this.form, this.store.controlReportEditable$);
  }

  transformData(date: Date): any {
    return this.localeDatePipe.transform(date, 'L', 'LT');
  }

  resetForm(data: ProjectPartnerControlReportDTO) {
    this.selection.clear();
    if (data.controllerFormats) {
      data.controllerFormats.forEach((select: ProjectPartnerControlReportDTO.ControllerFormatsEnum) => {
        this.selection.select(select);
      });
      this.form.controls.formats.patchValue(this.selection.selected);
    }
    if (data.type) {
      this.form.controls.partnerType.patchValue(data.type);
    }
    this.formService.resetEditable();
  }

  checkSelection(element: ProjectPartnerControlReportDTO.ControllerFormatsEnum): void {
    this.selection.toggle(element);
    this.form.controls.formats.patchValue(this.selection.selected);
    this.formService.setDirty(true);
    this.formService.setValid(true);
  }

  saveIdentification(): void {
    const data = {
      controllerFormats: this.form.value.formats,
      type: this.form.value.partnerType,
    };
    this.store.saveIdentification(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.report.partner.identification.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

}
