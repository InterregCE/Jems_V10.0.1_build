import {ChangeDetectionStrategy, Component, ContentChild, ElementRef, HostBinding, HostListener} from '@angular/core';

@Component({
  selector: 'app-inline-editable-field',
  templateUrl: './inline-editable-field.component.html',
  styleUrls: ['./inline-editable-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InlineEditableFieldComponent {

  @HostBinding('attr.tabIndex') tabIndex = 0;
  @ContentChild('input', {read: ElementRef}) inputElement: ElementRef;

  isInEditMode = false;

  @HostListener('focus') onFocus(): void {
    this.enterEditMode();
  }

  enterEditMode(): void {
    this.isInEditMode = true;
    setTimeout(() => {
      this.inputElement.nativeElement.focus();
      this.inputElement.nativeElement.setSelectionRange(1, 0);
    },         0);
  }
}
