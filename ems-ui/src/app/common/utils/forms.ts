import {FormGroup} from '@angular/forms';

export class Forms {

  static disableControls(formGroup: FormGroup): any {
    if (!formGroup) {
      return;
    }
    Object.keys(formGroup.controls).forEach(key => {
      formGroup.controls[key].disable();
    });
  }

  static enableControls(formGroup: FormGroup): any {
    if (!formGroup) {
      return;
    }
    Object.keys(formGroup.controls).forEach(key => {
      formGroup.controls[key].enable();
    });
  }
}
