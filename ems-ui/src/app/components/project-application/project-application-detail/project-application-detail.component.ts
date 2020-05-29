import { Component, OnInit } from '@angular/core';
import { OutputProject } from '@cat/api';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss']
})
export class ProjectApplicationDetailComponent implements OnInit {

  project = {} as OutputProject;
  fileNumber = 0;

  constructor() { }

  ngOnInit() {
  }

  addNewFilesForUpload() {
  }

}
