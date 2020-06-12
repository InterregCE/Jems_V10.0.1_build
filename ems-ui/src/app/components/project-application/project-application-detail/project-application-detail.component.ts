import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {OutputProject, OutputProjectFile} from '@cat/api';
import {ProjectApplicationService} from '../../../services/project-application.service';
import {ActivatedRoute} from '@angular/router';
import {TableConfiguration} from '../../general/configurations/table.configuration';
import {ProjectFileService} from '../../../services/project-file.service';
import {MatTableDataSource} from '@angular/material/table';
import {ActionConfiguration} from '../../general/configurations/action.configuration';
import {MatDialog} from '@angular/material/dialog';
import {DeleteDialogComponent} from './delete-dialog.component';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss']
})
export class ProjectApplicationDetailComponent implements OnInit, OnChanges {
  configuration = new TableConfiguration();

  @Input()
  dataSource: MatTableDataSource<OutputProjectFile>;

  project = {} as OutputProject;
  fileNumber = 0;
  projectId = this.activatedRoute.snapshot.params.projectId;
  statusMessages: string[];

  STATUS_MESSAGE_SUCCESS = (filename: string) => `Upload of '${filename}' successful.`;
  ERROR_MESSAGE_UPLOAD = (filename: string) => `Upload of '${filename}' not successful.`;
  ERROR_MESSAGE_EXISTS = (filename: string) => `File '${filename}' already exists.`;

  constructor(private projectApplicationService: ProjectApplicationService,
              private projectFileStorageService: ProjectFileService,
              private dialog: MatDialog,
              private activatedRoute: ActivatedRoute) { }

  isTableShown(): boolean {
    return this.dataSource && this.dataSource.data.length > 0;
  }

  ngOnInit() {
    this.initTableConfiguration();
    if (this.projectId) {
      this.projectApplicationService.getProject(Number(this.projectId)).subscribe((result: OutputProject) => {
        if (result) {
          this.project = result;
        }
      });
      this.getFilesForProject(this.projectId);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.dataSource && changes.dataSource.currentValue) {
      this.configuration.dataSource = changes.dataSource.currentValue;
    }
  }

  addNewFilesForUpload($event: any) {
    this.projectFileStorageService.addProjectFile(this.projectId, $event.target.files[0]).toPromise()
      .then(() => {
        this.getFilesForProject(this.projectId);
        if ($event.target.files[0].name) {
          this.addMessageFromResponse(this.STATUS_MESSAGE_SUCCESS($event.target.files[0].name));
        }
      })
      .catch((error: any) => {
        this.addErrorFromResponse(error, $event.target.files[0].name);
      });
  }

  downloadFile(element: OutputProjectFile) {
    window.open(
      this.projectFileStorageService.getDownloadLink(this.projectId, element.id),
      '_blank',
    );
  }

  editFileDescription(element: any) {

  }

  deleteFile(element: OutputProjectFile) {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      minWidth: '30rem',
      data: { name: element.name }
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
        this.dataSource = new MatTableDataSource<OutputProjectFile>(results.content);
        this.configuration.dataSource = this.dataSource;
        this.fileNumber = results.totalElements;
      });
  }

  initTableConfiguration(): void {
    this.configuration.displayedColumns = ['Filename', 'Timestamp', 'Username', 'Description'];
    this.configuration.elementProperties = ['name', 'updated', 'creator', 'description'];
    this.configuration.isTableClickable = false;
    this.configuration.dataSource = this.dataSource;

    this.configuration.actionColumn = true;
    this.configuration.actions = [
      // TODO activate actions with MP2-22
      // new ActionConfiguration('fas fa-edit', (element: any) => this.editFileDescription(element)),
      new ActionConfiguration('fas fa-file-download', (element: OutputProjectFile) => this.downloadFile(element)),
      new ActionConfiguration('fas fa-trash', (element: OutputProjectFile) => this.deleteFile(element)),
    ];
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
}
