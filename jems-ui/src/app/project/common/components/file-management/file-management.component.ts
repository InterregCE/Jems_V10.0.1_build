import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {Observable} from 'rxjs';
import {I18nMessage} from '@common/models/I18nMessage';

@Component({
  selector: 'jems-file-management',
  templateUrl: './file-management.component.html',
  styleUrls: ['./file-management.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileManagementComponent implements OnInit {

  @Input()
  section: CategoryInfo;

  canReadFiles$: Observable<boolean>;
  selectedCategoryPath$: Observable<I18nMessage[]>;

  constructor(public fileManagementStore: FileManagementStore) {
    this.canReadFiles$ = fileManagementStore.canReadFiles$;
    this.selectedCategoryPath$ = fileManagementStore.selectedCategoryPath$;
  }

  ngOnInit(): void {
    this.fileManagementStore.setSection(this.section);
  }
}
