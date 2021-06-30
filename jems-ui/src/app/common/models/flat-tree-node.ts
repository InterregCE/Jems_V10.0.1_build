export class FlatTreeNode<T> {
  data: T;
  expandable: boolean;
  level: number;
  parentIndex?: number;
}
