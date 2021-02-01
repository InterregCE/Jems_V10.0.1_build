import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProgrammePriorityDTO} from '@cat/api';

@Component({
  selector: 'app-programme-priority-items',
  templateUrl: './programme-priority-items.component.html',
  styleUrls: ['./programme-priority-items.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePriorityItemsComponent {
  @Input()
  priorities: Array<ProgrammePriorityDTO>;
}
