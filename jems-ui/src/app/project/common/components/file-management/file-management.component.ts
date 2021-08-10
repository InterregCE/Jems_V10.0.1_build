import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FileCategoryEnum, FileCategoryInfo} from '@project/common/components/file-management/file-category';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {Observable} from 'rxjs';
import {I18nMessage} from '@common/models/I18nMessage';

@Component({
  selector: 'app-file-management',
  templateUrl: './file-management.component.html',
  styleUrls: ['./file-management.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FileManagementStore]
})
export class FileManagementComponent implements OnInit {

  @Input()
  defaultCategory: FileCategoryInfo = {type: FileCategoryEnum.ALL};

  canReadFiles$: Observable<boolean>;
  selectedCategoryPath$: Observable<I18nMessage[]>;

  constructor(public fileManagementStore: FileManagementStore) {
    this.canReadFiles$ = fileManagementStore.canReadFiles$;
    this.selectedCategoryPath$ = fileManagementStore.selectedCategoryPath$;
  }

  ngOnInit(): void {
    this.fileManagementStore.selectedCategory$.next(this.defaultCategory);
  }
}
