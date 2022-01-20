import {Component, Input} from '@angular/core';
import {APIError} from '../../../models/APIError';

@Component({
  selector: 'jems-api-error-content',
  templateUrl: './api-error-content.component.html',
  styleUrls: ['./api-error-content.component.scss']
})
export class ApiErrorContentComponent {
  @Input() error: APIError;
}
