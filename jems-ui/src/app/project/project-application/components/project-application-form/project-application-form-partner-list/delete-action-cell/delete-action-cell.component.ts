import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProjectBudgetPartner} from '@project/model/ProjectBudgetPartner';

@Component({
  selector: 'jems-delete-action-cell',
  templateUrl: './delete-action-cell.component.html',
  styleUrls: ['./delete-action-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeleteActionCellComponent {

  @Input()
  element: ProjectBudgetPartner;
  @Input()
  disabled: boolean;
  @Output()
  delete = new EventEmitter<ProjectBudgetPartner>();

}
