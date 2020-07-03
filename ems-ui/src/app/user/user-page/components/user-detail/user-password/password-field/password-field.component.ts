import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-password-field',
  templateUrl: './password-field.component.html',
  styleUrls: ['./password-field.component.scss'],
})
export class PasswordFieldComponent {

  @Input()
  name: string;
  @Input()
  passwordControl: FormControl;
  @Input()
  passwordErrors: { [key: string]: string };
  @Input()
  showHint: boolean;

  hide = true;
}
