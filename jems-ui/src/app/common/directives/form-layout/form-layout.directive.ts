import {AfterContentChecked, AfterViewInit, Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[appFormLayout]'
})
export class FormLayoutDirective implements OnInit, AfterViewInit, AfterContentChecked {

  @Input() gap = '0.5rem';
  @Input() defaultRowMaxWidth = '50rem';

  constructor(private el: ElementRef) {
  }

  ngAfterViewInit(): void {
    this.setChildrenClasses();
  }

  ngAfterContentChecked(): void {
    this.setChildrenClasses();
  }

  ngOnInit(): void {
    this.el.nativeElement.classList.add(`app-layout`);
    this.el.nativeElement.classList.add(`app-form-layout`);
    this.el.nativeElement.style.setProperty('--gap', this.gap);
  }

  setChildrenClasses(): void {
    if (this.defaultRowMaxWidth) {
      for (const row of this.el.nativeElement.children) {
        if (row.style.maxWidth.length <= 0) {
          row.style.maxWidth = this.defaultRowMaxWidth;
        }
      }
    }
  }
}
