import {Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[appText]',
})
export class TextDirective implements OnInit {
  private static readonly DEFAULT_MAX_WIDTH = 685;

  private title: string;

  @Input()
  maxWidth: number;
  @Input()
  maxLines: number;

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    const width = this.maxWidth || TextDirective.DEFAULT_MAX_WIDTH;
    if (this.maxLines) {
      this.el.nativeElement.style.display = '-webkit-box';
      this.el.nativeElement.style['-webkit-box-orient'] = 'vertical';
      this.el.nativeElement.style['-webkit-line-clamp'] = this.maxLines;
      this.el.nativeElement.style['word-break'] = 'break-all';
    }
    this.el.nativeElement.style.overflow = 'hidden';
    this.el.nativeElement.style['text-overflow'] = 'ellipsis';
    this.title = this.el.nativeElement.title;
    this.el.nativeElement.style.minWidth = '5em';
    this.el.nativeElement.style.maxWidth = '100%';
    new ResizeObserver(res => {
      if (this.el.nativeElement.getBoundingClientRect().width > width) {
        this.el.nativeElement.style.width = `${width}px`;
      }
      this.setTitle();
    }).observe(this.el.nativeElement);
  }

  private setTitle(): void {
    if (this.maxLines && this.el.nativeElement.scrollHeight <= this.el.nativeElement.clientHeight) {
      // multi-line text does not overflow
      this.el.nativeElement.title = this.title;
      return;
    }
    if (!this.maxLines && this.el.nativeElement.offsetWidth >= this.el.nativeElement.scrollWidth) {
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
