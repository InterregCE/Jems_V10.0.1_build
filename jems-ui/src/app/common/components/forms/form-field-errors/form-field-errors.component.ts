import {Component, Input} from '@angular/core';
import {ValidationErrors} from '@angular/forms';
import {Observable} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'jems-form-field-errors',
  templateUrl: './form-field-errors.component.html',
  styleUrls: ['./form-field-errors.component.scss']
})
export class FormFieldErrorsComponent {

  @Input()
  errors: ValidationErrors | null;

  @Input()
  messages: { [key: string]: string };

  @Input()
  condensed = false;

  @Input()
  args: { [key: string]: {} };

  constructor(private translateService: TranslateService) {
  }

  getErrorMessage(error: ValidationErrors): Observable<string> {
    switch (error.key) {
      case 'maxlength': {
        return this.translateService.get(this.messages && this.messages[error.key] || 'common.error.field.max.length', error.value);
      }
      case 'minlength': {
        return this.translateService.get(this.messages && this.messages[error.key] || 'common.error.field.min.length', error.value);
      }
      case 'required': {
        return this.translateService.get(this.messages && this.messages[error.key] || 'common.error.field.blank', error.value);
      }
      case 'min': {
        return this.translateService.get(this.messages && this.messages[error.key] || 'common.error.field.min.decimal', {minValue: error.value.min});
      }
      case 'max': {
        return this.translateService.get(this.messages && this.messages[error.key] || 'common.error.field.max.decimal', {maxValue: error.value.max});
      }
      default: {
        return this.translateService.get(this.messages && this.messages[error.key] || 'common.error.input.invalid', this.args as any);
      }
    }
  }

}
