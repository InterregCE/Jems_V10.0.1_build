import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
    ProjectReportAnnexesFileManagementStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-annexes-tab/project-report-annexes-file-management-store';
import {
    ProjectReportCategoryTypeEnum
} from '@project/project-application/report/project-report/project-report-category-type';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';

@Component({
    selector: 'jems-project-report-annexes-tab',
    templateUrl: './project-report-annexes-tab.component.html',
    styleUrls: ['./project-report-annexes-tab.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [FormService]
})
export class ProjectReportAnnexesTabComponent implements OnInit {

    constructor(public projectReportFileManagementStore: ProjectReportAnnexesFileManagementStore) {}

    ngOnInit(): void {
        this.projectReportFileManagementStore.setSectionInit({type: ProjectReportCategoryTypeEnum.PROJECT_REPORT} as CategoryInfo);
    }
}
