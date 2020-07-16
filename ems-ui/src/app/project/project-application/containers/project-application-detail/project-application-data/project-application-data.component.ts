import {ChangeDetectionStrategy, Component} from '@angular/core';
import {InputProjectStatus} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import {Alert} from '@common/components/forms/alert';
import {BaseComponent} from '@common/components/base-component';
import {map} from 'rxjs/operators';
import {ProjectStore} from '../services/project-store.service';

@Component({
  selector: 'app-project-application-data',
  templateUrl: './project-application-data.component.html',
  styleUrls: ['./project-application-data.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDataComponent extends BaseComponent {
  Alert = Alert;
  Permission = Permission;

  project$ = this.projectStore.getProject();
  projectChanged$ = this.projectStore.getStatus()
    .pipe(
      map(() => true)
    );

  constructor(private projectStore: ProjectStore) {
    super();
  }

  changeProjectStatus(newStatus: InputProjectStatus.StatusEnum): void {
    this.projectStore.changeStatus({status: newStatus, note: ''});
  }
}
