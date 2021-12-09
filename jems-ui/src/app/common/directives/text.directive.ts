import {Directive, ElementRef, OnInit} from '@angular/core';

@Directive({
  selector: '[appText]',
})
export class TextDirective implements OnInit {
  private static readonly MAX_TEXT_WIDTH = 685;

  private title: string;

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    this.title = this.el.nativeElement.title;
    this.el.nativeElement.style.minWidth = '5em';
    this.el.nativeElement.style.maxWidth = '100%';
    this.el.nativeElement.classList.add('text-overflow-ellipsis');
    new ResizeObserver(res => {
      const maxSize = Math.min(TextDirective.MAX_TEXT_WIDTH, this.el.nativeElement.parentElement.getBoundingClientRect().width);
      if (this.el.nativeElement.getBoundingClientRect().width > maxSize) {
        this.el.nativeElement.style.width = `${maxSize}px`;
      }
      this.setTitle();
    }).observe(this.el.nativeElement);
  }

  private setTitle(): void {
    if (this.el.nativeElement.offsetWidth >= this.el.nativeElement.scrollWidth) {
      // text does not overflow
      this.el.nativeElement.title = this.title;
      return;
    }
    if (this.title) {
      this.el.nativeElement.title = `${this.title}\n${this.el.nativeElement.innerText}`;
    } else {
      this.el.nativeElement.title = this.el.nativeElement.innerText;
    }
  }
}
