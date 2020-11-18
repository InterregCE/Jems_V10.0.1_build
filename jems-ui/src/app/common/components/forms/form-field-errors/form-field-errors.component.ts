import {Component, Input} from '@angular/core';
import {ValidationErrors} from '@angular/forms';
import {Log} from '../../../utils/log';

@Component({
  selector: 'app-form-field-errors',
  templateUrl: './form-field-errors.component.html',
  styleUrls: ['./form-field-errors.component.scss']
})
export class FormFieldErrorsComponent {
  Log = Log;

  @Input()
  errors: ValidationErrors | null;

  @Input()
  messages: { [key: string]: string };

  @Input()
  args: { [key: string]: {} };
}
