import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output
} from '@angular/core';
import {CallDetailDTO, CallFundRateDTO, ProgrammeFundDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {CallDetailPageStore} from '../call-detail-page-store.service';
import {startWith, tap} from 'rxjs/operators';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-call-funds',
  templateUrl: './call-funds.component.html',
  styleUrls: ['./call-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFundsComponent {

  @Input()
  callForm: FormGroup;

  @Output()
  selectionChanged = new EventEmitter<void>();

  tableConfiguration: TableConfiguration;

  data$: Observable<any>;

  constructor(private callDetailPageStore: CallDetailPageStore,
              private formService: FormService,
              private formBuilder: FormBuilder) {

    this.data$ = combineLatest([
      this.callDetailPageStore.callIsEditable$,
      this.callDetailPageStore.callIsPublished$,
      this.callDetailPageStore.allFunds$,
      this.callDetailPageStore.call$,
      this.formService.reset$.pipe(startWith(null))
    ])
      .pipe(
        tap(([callIsEditable, callIsPublished, funds, call]) => this.resetForm(funds, call, callIsEditable, callIsPublished))
      );
  }

  get fundArray(): FormArray {
    return this.callForm.get('funds') as FormArray;
  }

  fundRateValue(fundIndex: number): FormControl {
    return this.fundArray.at(fundIndex).get('fundRateValue') as FormControl;
  }

  fundType(fundIndex: number): FormControl {
    return this.fundArray.at(fundIndex).get('fundRateValue') as FormControl;
  }

  private resetForm(allFunds: ProgrammeFundDTO[],
                    call: CallDetailDTO,
                    callIsEditable: boolean,
                    callIsPublished: boolean): void {
    this.fundArray.clear();

    allFunds
      .filter(fund => fund.selected)
      .forEach(fund => {
        const callFund = this.getCallFundById(fund.id, call);
        if (!callFund && !callIsEditable) {
          return; // if call is not readable only selected funds appear
        }
        const control = this.formBuilder.group({
          programmeFund: fund,
          selected: [!!callFund],
          rate: [callFund?.rate || 80],
          adjustable: [!!callFund?.adjustable],
        });
        this.fundArray.push(control);

        if (!callIsEditable || (callIsPublished && callFund)) {
          control.disable();
        }
      });
  }

  private getCallFundById(id: number, call: CallDetailDTO): CallFundRateDTO | undefined {
    return call?.funds?.find(fundRate => fundRate.programmeFund.id === id);
  }
}
