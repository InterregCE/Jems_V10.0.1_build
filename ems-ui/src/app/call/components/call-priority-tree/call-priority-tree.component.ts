import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  ViewChild
} from '@angular/core';
import {SelectionModel} from '@angular/cdk/collections';
import {MatTree, MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {Observable} from 'rxjs';
import {FlatTreeControl} from '@angular/cdk/tree';
import {BaseComponent} from '@common/components/base-component';
import {takeUntil} from 'rxjs/operators';

export class TreeNode {
  children: TreeNode[];
  code: string;
  isPriority: boolean;
  isPolicy: boolean;
  item: string;
}

/** Flat item node with expandable and level information */
export class TreeFlatNode {
  item: string;
  code: string;
  isPriority: boolean;
  isPolicy: boolean;
  level: number;
  expandable: boolean;
}

@Component({
  selector: 'app-call-priority-tree',
  templateUrl: './call-priority-tree.component.html',
  styleUrls: ['./call-priority-tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallPriorityTreeComponent extends BaseComponent implements OnInit, AfterViewInit {
  @Input()
  data: Observable<TreeNode[]>;

  @ViewChild('tree') tree: MatTree<TreeNode>;

  flatNodeMap = new Map<TreeFlatNode, TreeNode>();
  nestedNodeMap = new Map<TreeNode, TreeFlatNode>();
  treeControl: FlatTreeControl<TreeFlatNode>;
  treeFlattener: MatTreeFlattener<TreeNode, TreeFlatNode>;
  dataSource: MatTreeFlatDataSource<TreeNode, TreeFlatNode>;
  dataChanged$: EventEmitter<void> = new EventEmitter<void>();
  checklistSelection = new SelectionModel<TreeFlatNode>(true);

  constructor() {
    super();
    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel,
      this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<TreeFlatNode>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.treeControl.dataNodes = [];
  }

  ngOnInit(): void {
    this.data
      .pipe(
        takeUntil(this.destroyed$)
      ).subscribe(data => {
      this.dataSource.data = data;
      this.treeControl.dataNodes = this.flattenNodes(data);
      this.dataChanged$.emit();
    });
  }

  ngAfterViewInit() {
    this.dataChanged$
      .asObservable()
      .pipe(
        takeUntil(this.destroyed$)
      ).subscribe(() => {
      this.tree.treeControl.expandAll();
    })
  }

  getLevel = (node: TreeFlatNode) => node.level;
  isExpandable = (node: TreeFlatNode) => node.expandable;
  getChildren = (node: TreeNode): TreeNode[] => node.children;
  hasChild = (_: number, _nodeData: TreeFlatNode) => _nodeData.expandable;

  /**
   * Transformer to convert nested node to flat node. Record the nodes in maps for later use.
   */
  transformer = (node: TreeNode, level: number) => {
    const existingNode = this.nestedNodeMap.get(node);
    const flatNode = existingNode && existingNode.item === node.item
      ? existingNode
      : new TreeFlatNode();
    flatNode.item = node.item;
    flatNode.code = node.code;
    flatNode.level = level;
    flatNode.isPolicy = node.isPolicy;
    flatNode.isPriority = node.isPriority;
    flatNode.expandable = node.children.length > 0;
    this.flatNodeMap.set(flatNode, node);
    this.nestedNodeMap.set(node, flatNode);
    return flatNode;
  }

  /** Whether all the descendants of the node are selected. */
  descendantsAllSelected(node: TreeFlatNode): boolean {
    const descendants = this.treeControl.getDescendants(node);
    return descendants.every(child =>
      this.checklistSelection.isSelected(child)
    );
  }

  /** Whether part of the descendants are selected */
  descendantsPartiallySelected(node: TreeFlatNode): boolean {
    const descendants = this.treeControl.getDescendants(node);
    const result = descendants.some(child => this.checklistSelection.isSelected(child));
    return result && !this.descendantsAllSelected(node);
  }

  /** Toggle the to-do item selection. Select/deselect all the descendants node */
  todoItemSelectionToggle(node: TreeFlatNode): void {
    this.checklistSelection.toggle(node);
    const descendants = this.treeControl.getDescendants(node);
    this.checklistSelection.isSelected(node)
      ? this.checklistSelection.select(...descendants)
      : this.checklistSelection.deselect(...descendants);

    // Force update for the parent
    descendants.every(child =>
      this.checklistSelection.isSelected(child)
    );
    this.checkAllParentsSelection(node);
  }

  /** Toggle a leaf to-do item selection. Check all the parents to see if they changed */
  todoLeafItemSelectionToggle(node: TreeFlatNode): void {
    this.checklistSelection.toggle(node);
    this.checkAllParentsSelection(node);
  }

  /* Checks all the parents when a leaf node is selected/unselected */
  checkAllParentsSelection(node: TreeFlatNode): void {
    let parent: TreeFlatNode | null = this.getParentNode(node);
    while (parent) {
      this.checkRootNodeSelection(parent);
      parent = this.getParentNode(parent);
    }
  }

  /** Check root node checked state and change it accordingly */
  checkRootNodeSelection(node: TreeFlatNode): void {
    const nodeSelected = this.checklistSelection.isSelected(node);
    const descendants = this.treeControl.getDescendants(node);
    const descAllSelected = descendants.every(child =>
      this.checklistSelection.isSelected(child)
    );
    if (nodeSelected && !descAllSelected) {
      this.checklistSelection.deselect(node);
    } else if (!nodeSelected && descAllSelected) {
      this.checklistSelection.select(node);
    }
  }

  /* Get the parent node of a node */
  getParentNode(node: TreeFlatNode): TreeFlatNode | null {
    const currentLevel = this.getLevel(node);
    if (currentLevel < 1) {
      return null;
    }
    const startIndex = this.treeControl.dataNodes.indexOf(node) - 1;
    for (let i = startIndex; i >= 0; i--) {
      const currentNode = this.treeControl.dataNodes[i];
      if (this.getLevel(currentNode) < currentLevel) {
        return currentNode;
      }
    }
    return null;
  }

  private flattenNodes(items: TreeNode[]): TreeFlatNode[] {
    const flattenedNodes: TreeFlatNode[] = [];
    items.forEach(node => {
      flattenedNodes.push(this.transformer(node, 0));
      node.children.forEach(childNode => {
        flattenedNodes.push(this.transformer(childNode, 1));
      })
    })
    return flattenedNodes;
  }
}
