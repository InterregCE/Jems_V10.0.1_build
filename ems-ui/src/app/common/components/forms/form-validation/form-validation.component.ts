import {Component, Input, OnInit} from '@angular/core';
import {delay} from 'rxjs/operators';

@Component({
  selector: 'app-form-validation',
  templateUrl: './form-validation.component.html',
  styleUrls: ['./form-validation.component.scss']
})
export class FormValidationComponent {

  @Input()
  success = false;
  @Input()
  successMessage: string;
  @Input()
  validationError: string;
}
