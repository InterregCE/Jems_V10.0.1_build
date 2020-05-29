import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import { PageOutputProject, ProjectService, InputProject, OutputProject } from '@cat/api';

@Injectable()
export class ProjectApplicationService {

  basePath = '';

  constructor(private http: HttpClient,
              private service: ProjectService) { }

  public getProjects(size: number): Observable<PageOutputProject> {
    return this.http.get<PageOutputProject>(`${this.basePath}/api/projects?size=${size}&sort=id,desc`);
  }

  public addProject(project: InputProject): Observable<OutputProject> {
    return this.service.createProject(project);
  }

  public getProject(id: number): Observable<OutputProject> {
    return this.service.getProjectById(id);
  }

}
