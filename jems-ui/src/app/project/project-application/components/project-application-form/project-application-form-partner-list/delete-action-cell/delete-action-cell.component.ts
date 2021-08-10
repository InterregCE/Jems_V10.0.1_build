import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProjectPartnerDTO} from '@cat/api';

@Component({
  selector: 'app-delete-action-cell',
  templateUrl: './delete-action-cell.component.html',
  styleUrls: ['./delete-action-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeleteActionCellComponent {

  @Input()
  element: ProjectPartnerDTO;
  @Input()
  disabled: boolean;
  @Output()
  delete = new EventEmitter<ProjectPartnerDTO>();

}
