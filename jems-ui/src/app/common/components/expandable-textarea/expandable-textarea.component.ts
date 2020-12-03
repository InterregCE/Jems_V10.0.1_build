import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {FormControl, ValidationErrors} from '@angular/forms';

@Component({
  selector: 'app-expandable-textarea',
  templateUrl: './expandable-textarea.component.html',
  styleUrls: ['./expandable-textarea.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExpandableTextareaComponent {
  @Input()
  placeholder: string;
  @Input()
  label: string;
  @Input()
  errors: ValidationErrors | null;
  @Input()
  messages: { [key: string]: string };
  @Input()
  characterLimit: number;
  @Input()
  disabled: boolean;
  @Input()
  control: FormControl;
  @Input()
  customStyle?: string;
  @Input()
  minRows ? = 3;
  @Input()
  maxRows ? = 50;
  @Input()
  contextInfoText?: string;

  @Output()
  changed = new EventEmitter<void>();
}
