import {Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[appMultiColumnRow]'
})
export class MultiColumnRowDirective implements OnInit {
  @Input() gap = '1rem';
  @Input() stretch = 'none';
  @Input() itemsAlign: 'start' | 'space-between' = 'start';

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    this.el.nativeElement.classList.add('app-layout');
    this.el.nativeElement.classList.add('app-layout-row');
    this.el.nativeElement.classList.add('app-multi-column-row');
    this.el.nativeElement.style.setProperty('--gap', this.gap);
    this.el.nativeElement.style.setProperty('--flex', this.stretch === 'none' ? 'none' : 1);
    this.el.nativeElement.style.setProperty('--align', this.itemsAlign);
  }
}
