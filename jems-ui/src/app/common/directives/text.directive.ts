import {Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[jemsText]',
})
export class TextDirective implements OnInit {

  private title: string;

  @Input()
  maxWidth: number;
  @Input()
  minWidth: string;
  @Input()
  maxLines: number;
  @Input()
  displayTooltip = true;

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    if (this.maxLines) {
      this.el.nativeElement.style.display = '-webkit-box';
      this.el.nativeElement.style['-webkit-box-orient'] = 'vertical';
      this.el.nativeElement.style['-webkit-line-clamp'] = this.maxLines;
      this.el.nativeElement.style['word-break'] = 'break-word';
    }
    this.el.nativeElement.style.overflow = 'hidden';
    this.el.nativeElement.style['text-overflow'] = 'ellipsis';
    this.title = this.el.nativeElement.title;
    if (this.minWidth) {
      this.el.nativeElement.style.minWidth = '5em';
    }
    this.el.nativeElement.style.maxWidth = 'auto';
    new ResizeObserver(res => {
      if (this.maxWidth && this.el.nativeElement.getBoundingClientRect().width > this.maxWidth) {
        this.el.nativeElement.style.width = `${this.maxWidth}px`;
      }
      this.setTitle();
    }).observe(this.el.nativeElement);
  }

  private setTitle(): void {
    if (this.displayTooltip) {
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
}
