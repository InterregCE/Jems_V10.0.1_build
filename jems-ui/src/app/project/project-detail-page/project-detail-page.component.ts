import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDetailDTO} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-project-detail-page',
  templateUrl: './project-detail-page.component.html',
  styleUrls: ['./project-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectDetailPageComponent {

  data$: Observable<{
    project: ProjectDetailDTO;
    projectTitle: string;
  }>;

  constructor(public projectStore: ProjectStore) {
    this.data$ = combineLatest([
      this.projectStore.project$,
      this.projectStore.projectTitle$,
    ]).pipe(
      map(([project, projectTitle]) => ({
        project,
        projectTitle,
      }))
    );
  }

}
