import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import { OutputProject } from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {TableConfiguration} from '../../general/configurations/table.configuration';

@Component({
  selector: 'app-project-application-list',
  templateUrl: 'project-application-list.component.html',
  styleUrls: ['project-application-list.component.scss']
})

export class ProjectApplicationListComponent implements OnInit, OnChanges {
  configuration = new TableConfiguration();

  @Input()
  dataSource: MatTableDataSource<OutputProject>;

  isTableShown(): boolean {
    return this.dataSource && this.dataSource.data.length > 0;
  }

  ngOnInit() {
    this.initTableConfiguration();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.dataSource && changes.dataSource.currentValue) {
      this.configuration.dataSource = changes.dataSource.currentValue;
    }
  }

  initTableConfiguration(): void {
    this.configuration.displayedColumns = ['Id', 'Acronym', 'Submission Date'];
    this.configuration.elementProperties = ['id', 'acronym', 'submissionDate'];
    this.configuration.isTableClickable = true;
    this.configuration.dataSource = this.dataSource;
    this.configuration.routerLink = '/project/';
  }
}
