import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {OutputProject, OutputProjectFile} from '@cat/api';
import {ProjectApplicationService} from '../../../services/project-application.service';
import {ActivatedRoute} from '@angular/router';
import {TableConfiguration} from '../../general/configurations/table.configuration';
import {ProjectFileService} from '../../../services/project-file.service';
import {MatTableDataSource} from '@angular/material/table';
import {ActionConfiguration} from '../../general/configurations/action.configuration';

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

  constructor(private projectApplicationService: ProjectApplicationService,
              private projectFileStorageService: ProjectFileService,
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
    this.projectFileStorageService.addProjectFile(this.projectId, $event.target.files[0]).subscribe(() => {
      this.getFilesForProject(this.projectId);
    });
  }

  downloadFile(element: any) {

  }

  editFileDescription(element: any) {

  }

  deleteFile(element: any) {
    // get files at the end, also in the subscribe
    this.getFilesForProject(this.projectId);
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
    this.configuration.elementProperties = ['identifier', 'updated', 'creator', 'description'];
    this.configuration.isTableClickable = false;
    this.configuration.dataSource = this.dataSource;

    // TODO activate actions with MP2-22
    this.configuration.actionColumn = false;
    this.configuration.actions = [
      new ActionConfiguration('fas fa-edit', (element: any) => this.editFileDescription(element)),
      new ActionConfiguration('fas fa-file-download', (element: any) => this.downloadFile(element)),
      new ActionConfiguration('fas fa-trash', (element: any) => this.deleteFile(element)),
    ];
  }
}
