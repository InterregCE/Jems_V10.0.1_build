import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from './services/project-store.service';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    ProjectStore
  ]
})
export class ProjectApplicationDetailComponent extends BaseComponent {
  projectId = this.activatedRoute.snapshot.params.projectId;

  constructor(private projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute) {
    super();
    this.projectStore.init(this.projectId);
  }
}
