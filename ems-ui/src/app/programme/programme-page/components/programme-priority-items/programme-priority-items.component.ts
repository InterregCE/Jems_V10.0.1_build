import {Component, Input} from '@angular/core';
import {OutputProgrammePriority} from '@cat/api';

@Component({
  selector: 'app-programme-priority-items',
  templateUrl: './programme-priority-items.component.html',
  styleUrls: ['./programme-priority-items.component.scss']
})
export class ProgrammePriorityItemsComponent {
  @Input()
  priorities: Array<OutputProgrammePriority>;
}
