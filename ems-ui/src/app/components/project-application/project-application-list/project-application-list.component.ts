import {Component, Input} from '@angular/core';
import { OutputProject } from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'app-project-application-list',
  templateUrl: 'project-application-list.component.html',
  styleUrls: ['project-application-list.component.scss']
})

export class ProjectApplicationListComponent {
  displayedColumns: string[] = ['Id', 'Acronym', 'Submission Date'];
  @Input()
  dataSource: MatTableDataSource<OutputProject>;

  isTableShown(): boolean {
    return this.dataSource && this.dataSource.data.length > 0;
  }
}
