import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {NestedTreeControl} from '@angular/cdk/tree';
import {BaseComponent} from '@common/components/base-component';
import {
  ProgrammeEditableStateStore
} from '../../../programme/programme-page/services/programme-editable-state-store.service';
import {JemsRegionCheckbox} from '@common/models/jems-region-checkbox';

@Component({
  selector: 'jems-regions-tree',
  templateUrl: './jems-regions-tree.component.html',
  styleUrls: ['./jems-regions-tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class JemsRegionsTreeComponent extends BaseComponent implements OnInit {
  @Input()
  disabled = false;

  @Input()
  isLocked = false;

  @Input()
  dataSource: MatTreeNestedDataSource<JemsRegionCheckbox>;

  @Output()
  selectionChanged = new EventEmitter<void>();

  treeControl = new NestedTreeControl<JemsRegionCheckbox>(node => node.children);

  constructor(public programmeEditableStateStore: ProgrammeEditableStateStore) {
    super();
  }

  ngOnInit(): void {
    this.dataSource.data.forEach(node => this.expandIfChecked(node));
  }

  hasChild = (_: number, node: JemsRegionCheckbox) => !!node.children && node.children.length > 0;

  leafChanged(checkbox: JemsRegionCheckbox): void {
    checkbox.updateChecked();
    this.selectionChanged.emit();
  }

  parentChanged(checkbox: JemsRegionCheckbox): void {
    checkbox.checkOrUncheckAll(checkbox.checked);
    this.selectionChanged.emit();
  }

  expandIfChecked(node: JemsRegionCheckbox): void {
    if (node.someChecked && node.children.length > 0) {
      this.treeControl.expand(node);
    }
    node.children.forEach(child => this.expandIfChecked(child));
  }
}
