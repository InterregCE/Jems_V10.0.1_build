import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {TooltipPosition} from '@angular/material/tooltip';

@Component({
  selector: 'app-context-info',
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

  constructor() {
  }

}
