import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {InputProject, OutputProject, PageOutputProject, ProjectService} from '@cat/api';

@Injectable()
export class ProjectApplicationService {

  constructor(private service: ProjectService) {
  }

  public getProjects(size: number): Observable<PageOutputProject> {
    return this.service.getProjects(0, size, 'id,desc');
  }

  public addProject(project: InputProject): Observable<OutputProject> {
    return this.service.createProject(project);
  }

  public getProject(id: number): Observable<OutputProject> {
    return this.service.getProjectById(id);
  }

}
