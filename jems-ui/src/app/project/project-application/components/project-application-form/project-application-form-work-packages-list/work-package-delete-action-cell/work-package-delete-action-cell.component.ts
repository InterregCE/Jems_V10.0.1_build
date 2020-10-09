import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputWorkPackageSimple} from '@cat/api'

@Component({
  selector: 'app-work-package-delete-action-cell',
  templateUrl: './work-package-delete-action-cell.component.html',
  styleUrls: ['./work-package-delete-action-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkPackageDeleteActionCellComponent {

  @Input()
  element: OutputWorkPackageSimple;

  @Input()
  disabled: boolean;

  @Output()
  delete = new EventEmitter<OutputWorkPackageSimple>();
}
