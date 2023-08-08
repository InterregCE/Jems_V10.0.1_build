import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {animate, style, transition, trigger} from '@angular/animations';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';

@Component({
  selector: 'jems-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss'],
  animations: [
    trigger('slideInOut', [
      transition(':enter', [
        style({'opacity': '0', 'height': '0', 'min-height': '0'}),
        animate('250ms ease-in-out', style({}))
      ]),
      transition(':leave', [
        style({'min-height': '4rem'}),
        animate('250ms ease-in-out', style({'opacity': '0', 'height': '0', 'min-height': '0'}))
      ]),
    ]),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FormComponent {

  @Input()
  confirmSave: ConfirmDialogData;

  @Output()
  save = new EventEmitter<void>();
  @Output()
  discard = new EventEmitter<void>();

  showSaveDiscard$ = combineLatest([this.formService.dirty$, this.formService.pending$, this.formService.showMenu$])
    .pipe(
      map(([dirty, pending, showMenu]) => (dirty || pending) && showMenu)
    );


  constructor(
    public formService: FormService,
  ) {
  }

  submit(): void {
    this.formService.pending$.next(true);
    this.formService.setDirty(false);
    this.save.emit();
  }
}
