import {Component, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {InputProjectFileDescription, OutputProject, OutputProjectFile, ProjectService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ProjectFileService} from '../../services/project-file.service';
import {MatTableDataSource} from '@angular/material/table';
import {ActionConfiguration} from '@common/components/table/model/action.configuration';
import {ColumnConfiguration} from '@common/components/table/model/column.configuration';
import {ComponentType} from '@angular/cdk/overlay';
import {DescriptionCellComponent} from '@common/components/table/cell-renderers/description-cell/description-cell.component';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {TableComponent} from '@common/components/table/table.component';
import {MatDialog} from '@angular/material/dialog';
import {DeleteDialogComponent} from './delete-dialog.component';
import {Permission} from '../../../../security/permissions/permission';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {combineLatest, Subject} from 'rxjs';
import {take, takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss']
})
export class ProjectApplicationDetailComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild(TableComponent) table: TableComponent;

  Permission = Permission;
  dataSource: MatTableDataSource<OutputProjectFile>;
  configuration = new TableConfiguration();
  project = {} as OutputProject;
  fileNumber = 0;
  projectId = this.activatedRoute.snapshot.params.projectId;
  statusMessages: string[];
  destroyed$ = new Subject();

  editAction = new ActionConfiguration('fas fa-edit', (element: any, index: number) => this.editFileDescription(element, index));
  downloadAction = new ActionConfiguration('fas fa-file-download', (element: OutputProjectFile) => this.downloadFile(element));
  deleteAction = new ActionConfiguration('fas fa-trash', (element: OutputProjectFile) => this.deleteFile(element));

  STATUS_MESSAGE_SUCCESS = (filename: string) => `Upload of '${filename}' successful.`;
  ERROR_MESSAGE_UPLOAD = (filename: string) => `Upload of '${filename}' not successful.`;
  ERROR_MESSAGE_EXISTS = (filename: string) => `File '${filename}' already exists.`;

  constructor(private projectService: ProjectService,
              private projectFileStorageService: ProjectFileService,
              private dialog: MatDialog,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService) {
  }

  isTableShown(): boolean {
    return this.dataSource && this.dataSource.data.length > 0;
  }

  ngOnInit() {
    this.assignActionsToUser();
    if (this.projectId) {
      this.projectService.getProjectById(Number(this.projectId)).subscribe((result: OutputProject) => {
        if (result) {
          this.project = result;
        }
      });
      this.dataSource = new MatTableDataSource<OutputProjectFile>();
      this.getFilesForProject(this.projectId);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.dataSource && changes.dataSource.currentValue) {
      this.configuration.dataSource = changes.dataSource.currentValue;
    }
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  addNewFilesForUpload($event: any) {
    const file: File = $event.target.files[0];
    this.projectFileStorageService.addProjectFile(this.projectId, file).toPromise()
      .then(() => {
        this.getFilesForProject(this.projectId);
        this.addMessageFromResponse(this.STATUS_MESSAGE_SUCCESS(file.name));
      })
      .catch((error: any) => {
        this.addErrorFromResponse(error, file.name);
      });
  }

  downloadFile(element: OutputProjectFile) {
    window.open(
      this.projectFileStorageService.getDownloadLink(this.projectId, element.id),
      '_blank',
    );
  }

  editFileDescription(element: any, rowIndex: number) {
    this.table.changeCustomColumnData(rowIndex, {
      onSave: (value: string, index: number, fileId: number) => this.onSave(value, index, fileId),
      onCancel: (index: number) => this.onCancel(index),
      readOnly: false,
    });
  }

  deleteFile(element: OutputProjectFile) {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      minWidth: '30rem',
      data: {name: element.name}
    });

    dialogRef.afterClosed().subscribe((clickedYes: boolean) => {
      if (clickedYes) {
        this.projectFileStorageService.deleteFile(this.projectId, element.id).subscribe(() => {
          this.getFilesForProject(this.projectId);
        });
      }
    });
  }

  getFilesForProject(projectId: number) {
    this.projectFileStorageService.getProjectFiles(projectId, 100).toPromise()
      .then((results) => {
        this.dataSource.data = results.content;
        this.configuration.dataSource = this.dataSource;
        this.fileNumber = results.totalElements;
        if (this.table) {
          setTimeout(() => this.table.createCustomComponents(), 0);
        }
      });
  }

  initTableConfiguration(actions: ActionConfiguration[]): void {
    this.configuration.columns = [];
    this.configuration.columns.push(this.createNewColumnConfig('Filename', 'name', ColumnType.String));
    this.configuration.columns.push(this.createNewColumnConfig('Timestamp', 'updated', ColumnType.Date));
    this.configuration.columns.push(this.createNewColumnConfig('Username', 'author.email', ColumnType.String));
    const columnAffected = this.createNewColumnConfig('Description',
      'description',
      ColumnType.CustomComponent,
      DescriptionCellComponent,
      {
        onSave: (value: string, index: number, fileId: number) => this.onSave(value, index, fileId),
        onCancel: (index: number) => this.onCancel(index),
        readOnly: true,
      });
    this.configuration.columns.push(columnAffected);
    this.configuration.isTableClickable = false;
    this.configuration.dataSource = this.dataSource;

    this.configuration.actionColumn = true;
    this.configuration.actions = actions;
  }

  private addMessageFromResponse(status: string) {
    if (!this.statusMessages) {
      this.statusMessages = [];
    }
    this.statusMessages.unshift(status);
  }

  private addErrorFromResponse(status: any, filename: string) {
    if (status.error && status.status === 422) {
      this.addMessageFromResponse(this.ERROR_MESSAGE_EXISTS(filename));
    } else {
      this.addMessageFromResponse(this.ERROR_MESSAGE_UPLOAD(filename));
    }
  }

  createNewColumnConfig(displayColumn: string, elementProperties: string, columnType: ColumnType, component?: ComponentType<any>, extraProps?: any): ColumnConfiguration {
    if (columnType === ColumnType.CustomComponent) {
      return new ColumnConfiguration({
        displayedColumn: displayColumn,
        elementProperty: elementProperties,
        columnType,
        component,
        extraProps,
      });
    }
    return new ColumnConfiguration({
      displayedColumn: displayColumn,
      elementProperty: elementProperties,
      columnType,
    });
  }

  onCancel(rowIndex: number): void {
    this.closeInputFieldAndMakeReadonly(rowIndex);
  }

  onSave(saveValue: string, rowIndex: number, fileIdentifier: number): void {
    const description = {description: saveValue} as InputProjectFileDescription;
    this.projectFileStorageService.setDescriptionToFile(this.projectId, fileIdentifier, description).subscribe(() => {
      this.getFilesForProject(this.projectId);
    });
    this.closeInputFieldAndMakeReadonly(rowIndex);
  }

  closeInputFieldAndMakeReadonly(rowIndex: number): void {
    this.table.changeCustomColumnData(rowIndex, {
      onSave: (value: string, index: number, fileId: number) => this.onSave(value, index, fileId),
      onCancel: (index: number) => this.onCancel(index),
      readOnly: true,
    });
  }

  assignActionsToUser(): void {
    combineLatest([
      this.permissionService.hasPermission(Permission.APPLICANT_USER),
      this.permissionService.hasPermission(Permission.PROGRAMME_USER),
      this.permissionService.hasPermission(Permission.ADMINISTRATOR)
    ])
      .pipe(
        take(1),
        takeUntil(this.destroyed$)
      )
      .subscribe(([isApplicant, isProgramme,isAdmin]) => {
        if(isApplicant || isAdmin) {
          const actions = [this.editAction, this.downloadAction, this.deleteAction];
          this.initTableConfiguration(actions);
          return;
        }
        if (isProgramme) {
          const actions = [this.downloadAction];
          this.initTableConfiguration(actions);
          return;
        }
      });
  }
}
