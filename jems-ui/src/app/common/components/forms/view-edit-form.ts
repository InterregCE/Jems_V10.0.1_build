import {ChangeDetectorRef, EventEmitter, OnInit, Output} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';
import {AbstractForm} from './abstract-form';
import {delay, filter, takeUntil} from 'rxjs/operators';
import {BehaviorSubject} from 'rxjs';
import {Log} from '../../utils/log';
import {FormState} from '@common/components/forms/form-state';
import {TranslateService} from '@ngx-translate/core';

export abstract class ViewEditForm extends AbstractForm implements OnInit {
  FormState = FormState;

  @Output()
  switchedFormState: EventEmitter<FormState> = new EventEmitter<FormState>();

  // formState is deprecated. Should be replaced with (changeFormState$ | async) <- see programme-legal-status-list.component.html
  formState: FormState;
  changeFormState$ = new BehaviorSubject<FormState>(FormState.VIEW);

  protected constructor(protected changeDetectorRef: ChangeDetectorRef, protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);
  }

  abstract getForm(): FormGroup | null;

  get controls(): { [key: string]: AbstractControl } | undefined {
    return this.getForm()?.controls;
  }

  ngOnInit(): void {
    super.ngOnInit();

    if (this.success$) {
      this.success$
        .pipe(
          takeUntil(this.destroyed$),
          filter(success => !!success),
          delay(50)
        )
        .subscribe(() => this.changeFormState$.next(FormState.VIEW));
    }

    this.changeFormState$
      .pipe(takeUntil(this.destroyed$))
      .subscribe(newState => {
        this.formState = newState;
        if (newState === FormState.VIEW) {
          Log.debug('Enter view mode and disable form', this);
          this.getForm()?.disable();
          this.enterViewMode();
        }
        if (newState === FormState.EDIT) {
          Log.debug('Enter edit mode, enable form and hide success message.', this);
          this.showSuccessMessage$.next(false);
          this.getForm()?.enable();
          this.enterEditMode();
          this.getForm()?.markAsUntouched();
        }
        this.switchedFormState.emit(newState);
      });

    this.changeFormState$.next(FormState.VIEW);
  }

  protected enterViewMode(): void {
    // This is intentional
  }

  protected enterEditMode(): void {
    // This is intentional
  }

}
