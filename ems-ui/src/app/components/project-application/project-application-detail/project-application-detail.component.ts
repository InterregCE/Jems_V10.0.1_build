import {Component, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import { OutputProject } from '@cat/api';
import {ProjectApplicationService} from '../../../services/project-application.service';
import { ActivatedRoute } from '@angular/router';
import {ActionConfiguration} from '../../general/configurations/action.configuration';
import {TableConfiguration} from '../../general/configurations/table.configuration';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss']
})
export class ProjectApplicationDetailComponent implements OnInit, OnChanges {

  project = {} as OutputProject;
  fileNumber = 0;
  configuration = new TableConfiguration();
  dataSource: any;
  projectId = this.activatedRoute.snapshot.params.projectId;

  constructor(private projectApplicationService: ProjectApplicationService,
              private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    if (this.projectId) {
      this.projectApplicationService.getProject(Number(this.projectId)).subscribe((result: OutputProject) => {
        if (result) {
          this.project = result;
        }
      });
    }
    this.initTableConfiguration();

    this.getFilesForProject(this.projectId);
  }

  ngOnChanges(changes: SimpleChanges) {
    // if (changes.dataSource && changes.dataSource.currentValue) {
    //   this.configuration.dataSource = changes.dataSource.currentValue;
    // }
  }

  addNewFilesForUpload() {
    // this should be done in the subscribe
    this.getFilesForProject(this.projectId);
  }

  downloadFile(element: any) {

  }

  editFileDescription(element: any) {

  }

  deleteFile(element: any) {
    // get files at the end, also in the subscribe
    this.getFilesForProject(this.projectId);
  }

  getFilesForProject(projectId: string) {
    // here we set the datasource like in the project-application.component
    // we should also update the fileNumber variable
  }

  initTableConfiguration(): void {
    this.configuration.displayedColumns = ['File name', 'File type', 'Timestamp', 'Username', 'Description'];
    this.configuration.elementProperties = ['filename', 'filetype', 'timestamp', 'username', 'description'];
    this.configuration.isTableClickable = false;
    this.configuration.dataSource = this.dataSource;
    this.configuration.actionColumn = true;
    this.configuration.actions = [
      new ActionConfiguration('fas fa-edit', (element: any) => this.editFileDescription(element)),
      new ActionConfiguration('fas fa-file-download', (element: any) => this.downloadFile(element)),
      new ActionConfiguration('fas fa-trash', (element: any) => this.deleteFile(element)),
    ];
  }
}
