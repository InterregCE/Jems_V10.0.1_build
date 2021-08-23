import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {FileCategoryEnum, FileCategoryInfo} from '@project/common/components/file-management/file-category';

@Component({
  selector: 'app-application-annexes',
  templateUrl: './application-annexes.component.html',
  styleUrls: ['./application-annexes.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationAnnexesComponent {

  fileManagementSection = {type: FileCategoryEnum.APPLICATION} as FileCategoryInfo;
  constructor(public projectStore: ProjectStore) {
  }

}
