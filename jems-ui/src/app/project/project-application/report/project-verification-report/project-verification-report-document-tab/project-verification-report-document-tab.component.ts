import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {finalize, map, take, tap} from 'rxjs/operators';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {
  ProjectReportVerificationFileStore
} from '@project/project-application/report/project-verification-report/project-verification-report-document-tab/project-report-verification-communication-file-store.service';
import {Alert} from '@common/components/forms/alert';
import {JemsFileDTO} from '@cat/api';
import {SecurityService} from '../../../../../security/security.service';
import {TranslateService} from '@ngx-translate/core';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {FormService} from '@common/components/section/form/form.service';
import {ReportUtil} from '@project/common/report-util';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';

@Component({
  selector: 'jems-project-verification-report-document-tab',
  templateUrl: './project-verification-report-document-tab.component.html',
  styleUrls: ['./project-verification-report-document-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ProjectVerificationReportDocumentTabComponent {

  Alert = Alert;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  isUploadInProgress = false;

  categories: CategoryNode;
  selectedCategory: CategoryInfo;

  data$: Observable<{
    attachments: FileListItem[];
    canEdit: boolean;
  }>;

  constructor(
    private fileStore: ProjectReportVerificationFileStore,
    private projectReportStore: ProjectReportPageStore,
    private securityService: SecurityService,
    private translationService: TranslateService,
  ) {
    this.data$ = combineLatest([
      fileStore.fileList$,
      fileStore.report$,
      projectReportStore.userCanEditVerification$,
      securityService.currentUser.pipe(map(user => user?.id ?? 0)),
    ]).pipe(
      tap(([fileList, report, canEdit, currentUserId]) => this.initCategories(report.reportNumber)),
      map(([fileList, report, canEdit, currentUserId]) => ({
        attachments: fileList.map((file: JemsFileDTO) => ({
          id: file.id,
          name: file.name,
          type: file.type,
          uploaded: file.uploaded,
          author: file.author,
          sizeString: file.sizeString,
          description: file.description,
          editable: canEdit && ReportUtil.isProjectReportAfterVerificationStarted(report.status) && file.author.id === currentUserId,
          deletable: canEdit && ReportUtil.isProjectReportVerificationOngoing(report.status) && file.author.id === currentUserId,
          tooltipIfNotDeletable: '',
          iconIfNotDeletable: '',
        }) as FileListItem),
        canEdit,
      })),
    );
  }


  get error$() {
    return this.fileStore.error$;
  }

  get newSort$() {
    return this.fileStore.newSort$;
  }

  get filesChanged$() {
    return this.fileStore.filesChanged$;
  }

  uploadFile(target: any): void {
    this.isUploadInProgress = true;
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.fileStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.fileStore.uploadFile(file),
    ).add(() => this.isUploadInProgress = false);
  }

  downloadFile(file: FileListItem): void {
    this.fileStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return this.fileStore.updateDescription(data.id, data.description);
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return this.fileStore.delete(file.id);
  };

  private initCategories(reportNumber: number) {
    this.selectedCategory = {id: 2, type: 'docs'} as CategoryInfo;
    this.categories = {
      name: {
        i18nKey: this.translationService.instant(
          `project.application.project.reports.title.number`,
          {reportNumber: `${reportNumber}`},
        )
      },
      info: {id: 1, type: 'doc'},
      children: [{
        name: {i18nKey: 'project.application.project.verification.work.tab.document'},
        info: this.selectedCategory,
        children: [],
      }],
    };
  }
}
