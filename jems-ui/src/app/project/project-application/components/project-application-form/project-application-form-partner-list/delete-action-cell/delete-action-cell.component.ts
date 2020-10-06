import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProjectPartner} from '@cat/api'

@Component({
  selector: 'app-delete-action-cell',
  templateUrl: './delete-action-cell.component.html',
  styleUrls: ['./delete-action-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeleteActionCellComponent {

  @Input()
  element: OutputProjectPartner;
  @Input()
  disabled: boolean;
  @Output()
  delete = new EventEmitter<OutputProjectPartner>();

}
