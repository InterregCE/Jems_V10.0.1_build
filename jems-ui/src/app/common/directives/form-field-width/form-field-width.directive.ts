import {Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[appFormFieldWidth]'
})
export class FormFieldWidthDirective implements OnInit {
  @Input('appFormFieldWidth') formFieldWidth: 1 | 2 | 3 | 4 | 5 | 'small' | 'medium' | 'large';
  @Input() extendError = false;

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    this.el.nativeElement.classList.add(`app-form-field-width-${this.formFieldWidth}`);
    if (this.extendError) {
      this.el.nativeElement.classList.add('extend-error');
    }
  }
}
