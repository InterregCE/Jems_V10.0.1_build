import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FileCategoryEnum, FileCategoryNode} from '@project/common/components/file-management/file-category';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-file-management',
  templateUrl: './file-management.component.html',
  styleUrls: ['./file-management.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FileManagementStore]
})
export class FileManagementComponent {

  selectedCategory: FileCategoryNode | undefined;

  FileCategoryEnum = FileCategoryEnum;

  canReadFiles$: Observable<boolean>;

  constructor(public fileManagementStore: FileManagementStore) {
    this.canReadFiles$ = fileManagementStore.canReadFiles$;
  }

  getSelectedCategoryPath(): string[] {
    const path: string[] = [];
    let root = this.selectedCategory;
    while (root) {
      path.push(root.name as any);
      root = root.parent;
    }
    path.push('Projects');
    return path.reverse();
  }
}
