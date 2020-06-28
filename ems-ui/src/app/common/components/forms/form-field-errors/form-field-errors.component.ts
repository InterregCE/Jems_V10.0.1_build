import {Component, Input} from '@angular/core';
import {ValidationErrors} from '@angular/forms';

@Component({
  selector: 'app-form-field-errors',
  templateUrl: './form-field-errors.component.html',
  styleUrls: ['./form-field-errors.component.scss']
})
export class FormFieldErrorsComponent {

  @Input()
  errors: ValidationErrors | null;

  @Input()
  messages: {[key: string]: string};

}
