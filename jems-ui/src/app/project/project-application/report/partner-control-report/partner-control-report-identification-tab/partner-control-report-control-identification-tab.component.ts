import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectDetailDTO,
  ProjectPartnerControlReportDTO,
  ProjectPartnerDetailDTO,
  ReportDesignatedControllerDTO, ReportVerificationDTO
} from '@cat/api';
import {FormBuilder, FormGroup} from '@angular/forms';
import {
    PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {
    ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {SelectionModel} from '@angular/cdk/collections';
import {
    PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

@Component({
    selector: 'jems-partner-control-report-identification-tab',
    templateUrl: './partner-control-report-control-identification-tab.component.html',
    styleUrls: ['./partner-control-report-control-identification-tab.component.scss'],
    providers: [FormService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportControlIdentificationTabComponent {

    ProjectPartnerControlReportDTO = ProjectPartnerControlReportDTO;

    data$: Observable<{
        partnerControlReport: ProjectPartnerControlReportDTO;
        project: ProjectDetailDTO;
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

    constructor(
        public pageStore: PartnerReportDetailPageStore,
        public store: PartnerControlReportStore,
        public formService: FormService,
        private formBuilder: FormBuilder,
        private projectStore: ProjectStore
    ) {
        this.data$ = combineLatest([
            store.partnerControlReport$,
            projectStore.project$,
            store.partner$
        ]).pipe(
            map(([partnerControlReport, project, partner]) => ({
                partnerControlReport,
                project,
                partner
            })),
            tap((data) => this.resetForm(data.partnerControlReport)),
        );

        this.formService.init(this.form, this.store.controlReportEditable$);
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
          designatedController: {} as ReportDesignatedControllerDTO,
          reportVerification: {} as ReportVerificationDTO
        };
        this.store.saveIdentification(data)
            .pipe(
                take(1),
                tap(() => this.formService.setSuccess('project.application.partner.report.tab.identification.saved')),
                catchError(err => this.formService.setError(err))
            ).subscribe();
    }

}
