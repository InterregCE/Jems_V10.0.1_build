import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';

@Component({
  selector: 'jems-application-annexes',
  templateUrl: './application-annexes.component.html',
  styleUrls: ['./application-annexes.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationAnnexesComponent {

  fileManagementSection = {type: FileCategoryTypeEnum.APPLICATION} as CategoryInfo;
  constructor(public projectStore: ProjectStore) {
  }

}
