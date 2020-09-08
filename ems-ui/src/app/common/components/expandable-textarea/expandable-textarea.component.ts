import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {FormControl, ValidationErrors} from '@angular/forms';

@Component({
  selector: 'app-expandable-textarea',
  templateUrl: './expandable-textarea.component.html',
  styleUrls: ['./expandable-textarea.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExpandableTextareaComponent {
  @Input()
  label: string;
  @Input()
  errors: ValidationErrors | null;
  @Input()
  messages: {[key: string]: string};
  @Input()
  characterLimit: number;
  @Input()
  disabled: boolean;
  @Input()
  control: FormControl;
  @Input()
  customStyle?: string;
}
