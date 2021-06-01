import {Directive, ElementRef, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';

@Directive({
  selector: '[appHintFor]',
})
export class HintDirective implements OnInit, OnChanges, OnDestroy {

  @Input('appHintFor')
  inputElement: HTMLInputElement | HTMLTextAreaElement;

  @Input()
  hide: boolean;

  isFocused = false;
  onFocusChangeCallback = this.onFocusChange.bind(this);

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    this.inputElement.addEventListener('focus', this.onFocusChangeCallback);
    this.inputElement.addEventListener('blur', this.onFocusChangeCallback);
  }

  onFocusChange(event: FocusEvent): void {
    this.isFocused = event.type === 'focus';
    this.updateHintVisibility();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.updateHintVisibility();
  }

  ngOnDestroy(): void {
    this.inputElement.removeEventListener('focus', this.onFocusChangeCallback);
    this.inputElement.removeEventListener('blur', this.onFocusChangeCallback);
  }

  private updateHintVisibility(): void {
    if (this.inputElement.disabled || this.hide || !this.isFocused) {
      this.hideHint();
    } else {
      this.showHint();
    }
  }

  private hideHint(): void {
    this.el.nativeElement.style.visibility = 'hidden';
  }

  private showHint(): void {
    this.el.nativeElement.style.visibility = 'visible';
  }

}
