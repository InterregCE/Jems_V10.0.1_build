import {ValidatorFn} from '@angular/forms';

export class AppControl {
  name: string;
  errorMessages?: { [key: string]: string };
  validators?: ValidatorFn[];
}
