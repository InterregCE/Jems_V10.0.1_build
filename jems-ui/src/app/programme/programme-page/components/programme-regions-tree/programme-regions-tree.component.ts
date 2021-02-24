import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {NestedTreeControl} from '@angular/cdk/tree';
import {ProgrammeRegionCheckbox} from '../../model/programme-region-checkbox';
import {BaseComponent} from '@common/components/base-component';
import {takeUntil, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';

@UntilDestroy()
@Component({
  selector: 'app-programme-regions-tree',
  templateUrl: './programme-regions-tree.component.html',
  styleUrls: ['./programme-regions-tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeRegionsTreeComponent extends BaseComponent implements OnInit {
  @Input()
  disabled = false;
  @Input()
  dataSource: MatTreeNestedDataSource<ProgrammeRegionCheckbox>;

  @Output()
  selectionChanged = new EventEmitter<void>();

  treeControl = new NestedTreeControl<ProgrammeRegionCheckbox>(node => node.children);

  constructor(public programmeEditableStateStore: ProgrammeEditableStateStore) {
    super();

    this.programmeEditableStateStore.init();
  }

  ngOnInit(): void {
    this.dataSource._data
      .pipe(
        takeUntil(this.destroyed$),
        tap(data => data.forEach(node => this.expandIfChecked(node)))
      ).subscribe();
  }

  hasChild = (_: number, node: ProgrammeRegionCheckbox) => !!node.children && node.children.length > 0;

  leafChanged(checkbox: ProgrammeRegionCheckbox): void {
    checkbox.updateChecked();
    this.selectionChanged.emit();
  }

  parentChanged(checkbox: ProgrammeRegionCheckbox): void {
    checkbox.checkOrUncheckAll(checkbox.checked);
    this.selectionChanged.emit();
  }

  expandIfChecked(node: ProgrammeRegionCheckbox): void {
    if (node.someChecked && node.children.length > 0) {
      this.treeControl.expand(node);
    }
    node.children.forEach(child => this.expandIfChecked(child));
  }
}
