import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {FlatTreeControl} from '@angular/cdk/tree';
import {FlatTreeNode} from '@common/models/flat-tree-node';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';

@Component({
  selector: 'jems-category-tree',
  templateUrl: './category-tree.component.html',
  styleUrls: ['./category-tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CategoryTreeComponent implements OnChanges {

  @Input()
  selectedCategory: CategoryInfo;
  @Input()
  categories: CategoryNode;
  @Output()
  categorySelected = new EventEmitter<CategoryInfo>();

  treeControl: FlatTreeControl<FlatTreeNode<CategoryNode>>;
  dataSource: MatTreeFlatDataSource<CategoryNode, FlatTreeNode<CategoryNode>>;

  constructor() {
    this.initializeDataSource();
  }

  hasChild(_: number, node: FlatTreeNode<CategoryNode>): boolean {
    return !!node.data.children?.length;
  }

  selectCategory(node: CategoryNode): void {
    this.selectedCategory = node.info;
    this.categorySelected.emit(node.info);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.categories) {
      this.dataSource.data = [changes.categories.currentValue];
      this.treeControl.expandAll();
    }
  }

  private initializeDataSource(): void {
    this.treeControl = new FlatTreeControl<FlatTreeNode<CategoryNode>>(
      node => node.level, node => node.expandable);
    const treeFlattener = new MatTreeFlattener(
      (node: CategoryNode, level: number) => ({
        expandable: !!node?.children?.length,
        level,
        data: node
      }),
      node => node.level,
      node => node.expandable,
      node => node?.children
    );
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, treeFlattener);
  }
}
