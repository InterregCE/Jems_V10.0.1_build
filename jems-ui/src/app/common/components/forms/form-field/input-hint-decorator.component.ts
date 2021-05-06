import {
  AfterContentInit, ChangeDetectionStrategy, ChangeDetectorRef,
  Component,
  ContentChild,
  ElementRef,
  Input,
} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {MatInput} from '@angular/material/input';

@UntilDestroy()
@Component({
  selector: 'app-input-hint-decorator',
  templateUrl: './input-hint-decorator.component.html',
  styleUrls: ['./input-hint-decorator.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InputHintDecoratorComponent implements AfterContentInit {

  @ContentChild(MatInput, { read: ElementRef }) child: ElementRef;

  @Input()
  maxLength: number;
  @Input()
  hasErrors = false;

  currentLength = 0;
  isFocused = false;
  disabled = false;

  constructor(private changeDetectorRef: ChangeDetectorRef) {
  }

  ngAfterContentInit(): void {
    this.currentLength = this.child.nativeElement.value.length;
    this.child.nativeElement.addEventListener('focus', this.onFocus.bind(this));
    this.child.nativeElement.addEventListener('blur', this.onFocus.bind(this));
    this.child.nativeElement.addEventListener('input', this.onChange.bind(this));
    this.disabled = this.child.nativeElement.disabled;
  }

  onFocus(event: FocusEvent): void {
    this.isFocused = event.type === 'focus';
    this.changeDetectorRef.markForCheck();
  }

  onChange(): void {
    this.currentLength = this.child.nativeElement.value.length;
    this.changeDetectorRef.markForCheck();
  }
}
