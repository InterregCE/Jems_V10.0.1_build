import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {WorkPackageInvestmentDTO} from '@cat/api';

@Component({
  selector: 'app-work-package-investment-delete-action-cell',
  templateUrl: './work-package-investment-delete-action-cell.component.html',
  styleUrls: ['./work-package-investment-delete-action-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkPackageInvestmentDeleteActionCellComponent {

  @Input()
  element: WorkPackageInvestmentDTO;

  @Input()
  disabled: boolean;

  @Output()
  delete = new EventEmitter<WorkPackageInvestmentDTO>();

}
