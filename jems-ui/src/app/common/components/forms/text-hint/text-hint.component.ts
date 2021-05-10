import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

@Component({
  selector: 'app-text-hint',
  templateUrl: './text-hint.component.html',
  styleUrls: ['./text-hint.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TextHintComponent {
  @Input()
  maxLength: number;
  @Input()
  currentLength: number;
}
