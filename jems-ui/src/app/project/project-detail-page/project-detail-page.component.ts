import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDetailDTO} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'jems-project-detail-page',
  templateUrl: './project-detail-page.component.html',
  styleUrls: ['./project-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectDetailPageComponent {

  data$: Observable<{
    currentVersionOfProject: ProjectDetailDTO;
    currentVersionOfProjectTitle: string;
  }>;

  constructor(public projectStore: ProjectStore) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionOfProject$,
      this.projectStore.currentVersionOfProjectTitle$,
    ]).pipe(
      map(([currentVersionOfProject, currentVersionOfProjectTitle]) => ({
        currentVersionOfProject,
        currentVersionOfProjectTitle,
      }))
    );
  }

}
