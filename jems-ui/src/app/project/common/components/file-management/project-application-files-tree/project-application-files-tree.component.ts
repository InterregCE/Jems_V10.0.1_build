import {ChangeDetectionStrategy, Component} from '@angular/core';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {FlatTreeControl} from '@angular/cdk/tree';
import {FlatTreeNode} from '@common/models/flat-tree-node';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {FileCategoryInfo, FileCategoryNode} from '@project/common/components/file-management/file-category';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';

@Component({
  selector: 'app-project-application-files-tree',
  templateUrl: './project-application-files-tree.component.html',
  styleUrls: ['./project-application-files-tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFilesTreeComponent {

  selectedCategory: FileCategoryInfo | undefined;
  treeControl: FlatTreeControl<FlatTreeNode<FileCategoryNode>>;
  dataSource: MatTreeFlatDataSource<FileCategoryNode, FlatTreeNode<FileCategoryNode>>;

  fileCategories$: Observable<FileCategoryNode>;

  constructor(private fileManagementStore: FileManagementStore) {
    this.initializeDataSource();
    this.fileCategories$ = this.fileManagementStore.fileCategories$
      .pipe(
        tap(fileCategories => this.dataSource.data = [fileCategories]),
        tap(() => this.treeControl.expandAll())
      );
  }

  hasChild(_: number, node: FlatTreeNode<FileCategoryNode>): boolean {
    return !!node.data.children?.length;
  }

  selectCategory(node: FileCategoryNode): void {
    this.selectedCategory = node.info;
    this.fileManagementStore.selectedCategory$.next(node.info);
  }

  private initializeDataSource(): void {
    this.treeControl = new FlatTreeControl<FlatTreeNode<FileCategoryNode>>(
      node => node.level, node => node.expandable);
    const treeFlattener = new MatTreeFlattener(
      (node: FileCategoryNode, level: number) => ({
        expandable: !!node.children?.length,
        level,
        data: node
      }),
      node => node.level,
      node => node.expandable,
      node => node.children
    );
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, treeFlattener);
  }
}
