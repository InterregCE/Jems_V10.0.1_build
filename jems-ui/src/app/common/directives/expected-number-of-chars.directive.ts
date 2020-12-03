import {Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[appExpectedNumberOfChars]'
})
export class ExpectedNumberOfCharsDirective implements OnInit {
  @Input('appExpectedNumberOfChars') expectedNumberOfChars: 1 | 2 | 3 | 4 | 5;
  @Input() extendError = false;

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    this.el.nativeElement.classList.add(`expected-number-of-chars-${this.expectedNumberOfChars}`);
    this.el.nativeElement.classList.add(this.extendError ? 'extend-error' : '');
  }

}
