import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProjectAssociatedOrganization} from '@cat/api'

@Component({
  selector: 'app-delete-action-cell-associated-organization',
  templateUrl: './delete-action-cell-associated-organization.component.html',
  styleUrls: ['./delete-action-cell-associated-organization.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeleteActionCellAssociatedOrganizationComponent {

  @Input()
  element: OutputProjectAssociatedOrganization;
  @Input()
  disabled: boolean;
  @Output()
  delete = new EventEmitter<OutputProjectAssociatedOrganization>();

}
