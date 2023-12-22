import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {TooltipPosition} from '@angular/material/tooltip';

@Component({
  selector: 'jems-context-info',
  templateUrl: './context-info.component.html',
  styleUrls: ['./context-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContextInfoComponent {

  @Input()
  infoText?: string;

  /*
    InfoPosition
    Position of context-info icon in relation to parent.
    Use before/after/above/below to absolutely position in relation to parent (parent needs rel. positioning)
    Use left/right to position for inline context like headlines (wrap both inside span)
   */
  @Input()
  infoPosition: TooltipPosition = 'after';

  @Input()
  noWidth: Boolean = false;

}
