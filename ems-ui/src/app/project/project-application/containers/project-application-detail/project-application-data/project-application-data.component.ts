import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
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

  @Input()
  fileNumber: number;
  @Input()
  statusMessages: string[];
  @Output()
  uploadFile: EventEmitter<File> = new EventEmitter<File>();

  projectChanged$ = this.projectStore.getStatus()
    .pipe(
      map(() => true)
    )
  project$ = this.projectStore.getProject();

  constructor(private projectStore: ProjectStore) {
    super();
  }

  changeProjectStatus(newStatus: InputProjectStatus): void {
    this.projectStore.changeStatus(newStatus);
  }
}
