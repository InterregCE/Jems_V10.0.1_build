import {Subject} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Injectable} from '@angular/core';

@UntilDestroy()
@Injectable()
export class ProjectPartnerBudgetTabService {

  private isBudgetOptionsFormInEditModeSubject = new Subject<boolean>();
  private isBudgetFormInEditModeSubject = new Subject<boolean>();
  isBudgetOptionsFormInEditMode$ = this.isBudgetOptionsFormInEditModeSubject.asObservable();
  isBudgetFormInEditMode$ = this.isBudgetFormInEditModeSubject.asObservable();

  trackBudgetOptionsFormState(formService: FormService): void {
    formService.dirty$.pipe(
      tap(dirty => this.isBudgetOptionsFormInEditModeSubject.next(dirty)),
      untilDestroyed(this)
    ).subscribe();
  }

  trackBudgetFormState(formService: FormService): void {
    formService.dirty$.pipe(
      tap(dirty => this.isBudgetFormInEditModeSubject.next(dirty)),
      untilDestroyed(this)
    ).subscribe();
  }
}
