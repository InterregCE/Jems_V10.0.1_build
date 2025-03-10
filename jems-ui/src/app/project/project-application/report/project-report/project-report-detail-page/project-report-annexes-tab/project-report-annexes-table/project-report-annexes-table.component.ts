import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {combineLatest, Observable, Subject} from 'rxjs';
import {Alert} from '@common/components/forms/alert';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {PageJemsFileDTO, ProjectReportAnnexesService, JemsFileDTO} from '@cat/api';
import {map, switchMap, take} from 'rxjs/operators';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {
    ProjectReportAnnexesFileManagementStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-annexes-tab/project-report-annexes-file-management-store';
import {
    ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';

@UntilDestroy()
@Component({
    selector: 'jems-project-report-annexes-table',
    templateUrl: './project-report-annexes-table.component.html',
    styleUrls: ['./project-report-annexes-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportAnnexesTableComponent {

    Alert = Alert;
    acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
    maximumAllowedFileSizeInMB: number;
    fileSizeOverLimitError$ = new Subject<boolean>();
    isUploadInProgress = false;

    data$: Observable<{
        files: PageJemsFileDTO;
        fileList: FileListItem[];
        canUserEdit: boolean;
        selectedCategory: CategoryInfo | undefined;
    }>;

    constructor(
        public projectReportFileStore: ProjectReportAnnexesFileManagementStore,
        private projectReportService: ProjectReportAnnexesService,
        private projectReportStore: ProjectReportPageStore
    ) {
        this.data$ = combineLatest([
            this.projectReportFileStore.fileList$,
            this.projectReportFileStore.selectedCategory$,
            this.projectReportStore.userCanEditReport$,
            this.projectReportFileStore.isReportOpen$,
            this.projectReportFileStore.isInLimitedReopened$
        ]).pipe(
            map(([files, selectedCategory, canEdit, isReportOpen, isInLimitedReopened]) => ({
                files,
                fileList: files.content.map((file: JemsFileDTO) => ({
                    id: file.id,
                    name: file.name,
                    type: file.type,
                    uploaded: file.uploaded,
                    author: file.author,
                    sizeString: file.sizeString,
                    description: file.description,
                    editable: isReportOpen && canEdit,
                    deletable: isReportOpen && canEdit && (file.type == JemsFileDTO.TypeEnum.ProjectReport) && !isInLimitedReopened,
                    tooltipIfNotDeletable: isReportOpen && canEdit && (file.type != JemsFileDTO.TypeEnum.ProjectReport) ? 'file.table.action.delete.disabled.for.tab.tooltip' : '',
                    iconIfNotDeletable: isReportOpen && canEdit && (file.type != JemsFileDTO.TypeEnum.ProjectReport) ? 'delete_forever' : ''
                })),
                selectedCategory,
                canUserEdit: canEdit
            })),
        );
        this.projectReportFileStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this))
            .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
    }

    uploadFile(target: any): void {
        this.isUploadInProgress = true;
        FileListComponent.doFileUploadWithValidation(
            target,
            this.fileSizeOverLimitError$,
            this.projectReportFileStore.error$,
            this.maximumAllowedFileSizeInMB,
            file => this.projectReportFileStore.uploadFile(file),
        ).add(() => this.isUploadInProgress = false);
    }

    downloadFile(file: FileListItem): void {
        this.projectReportFileStore.downloadFile(file.id)
            .pipe(take(1))
            .subscribe();
    }

    setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
        return combineLatest([
            this.projectReportFileStore.projectId$.pipe(map(id => Number(id))),
            this.projectReportFileStore.reportId$.pipe(map(id => Number(id))),
        ]).pipe(
            switchMap(([projectId, reportId]) =>
                this.projectReportService.updateProjectReportAnnexesFileDescription(data.id, projectId, reportId, data.description)
            ),
        );
    };

    deleteCallback = (file: FileListItem): Observable<void> => {
        return combineLatest([
            this.projectReportFileStore.projectId$.pipe(map(id => Number(id))),
            this.projectReportFileStore.reportId$.pipe(map(id => Number(id))),
        ]).pipe(
            switchMap(([projectId, reportId]) =>
                this.projectReportService.deleteProjectReportAnnexesFile(file.id, projectId, reportId)
            ),
        );
    };
}
