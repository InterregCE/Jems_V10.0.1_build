import { Component, OnInit } from '@angular/core';
import { OutputProject } from '@cat/api';
import {ProjectApplicationService} from '../../../services/project-application.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss']
})
export class ProjectApplicationDetailComponent implements OnInit {

  project = {} as OutputProject;
  fileNumber = 0;

  constructor(private projectApplicationService: ProjectApplicationService,
              private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    const projectId = this.activatedRoute.snapshot.params.projectId;
    if (projectId) {
      this.projectApplicationService.getProject(Number(projectId)).subscribe((result: OutputProject) => {
        if (result) {
          this.project = result;
        }
      });
    }
  }

  addNewFilesForUpload() {
  }

}
