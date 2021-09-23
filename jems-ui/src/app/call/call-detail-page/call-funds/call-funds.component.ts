import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  Output,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {CallDetailDTO, ProgrammeFundDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {CallDetailPageStore} from '../call-detail-page-store.service';
import {map, startWith} from 'rxjs/operators';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {CallFundsConstants} from './call-funds.constants';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-call-funds',
  templateUrl: './call-funds.component.html',
  styleUrls: ['./call-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFundsComponent {
  @Input()
  callFundsForm : FormGroup;

  @Output()
  selectionChanged = new EventEmitter<void>();

  @ViewChild('checkboxCell', {static: true})
  checkboxCell: TemplateRef<any>;

  @ViewChild('switchCell', {static: true})
  switchCell: TemplateRef<any>;

  @ViewChild('valueCell', {static: true})
  valueCell: TemplateRef<any>;

  tableConfiguration: TableConfiguration;
  constants = CallFundsConstants;

  data$: Observable<{
    userCanApply: boolean,
    callIsReadable: boolean,
    callIsEditable: boolean,
    callIsPublished: boolean,
    funds: ProgrammeFundDTO[]
  }>;

  constructor(private callDetailPageStore: CallDetailPageStore,
              private formService: FormService,
              private formBuilder: FormBuilder,
              private changeDetectorRef: ChangeDetectorRef) {

    combineLatest([
      this.callDetailPageStore.allFunds$,
      this.callDetailPageStore.callIsReadable$,
      this.callDetailPageStore.call$,
      this.formService.reset$.pipe(startWith(null))
    ])
      .pipe(
        map(([funds, callIsReadable, call]) => this.resetForm(this.getFunds(funds, call), callIsReadable)),
        untilDestroyed(this)
      ).subscribe();

    this.data$ = combineLatest([
      this.callDetailPageStore.userCanApply$,
      this.callDetailPageStore.callIsReadable$,
      this.callDetailPageStore.callIsEditable$,
      this.callDetailPageStore.callIsPublished$,
      this.callDetailPageStore.allFunds$,
      this.callDetailPageStore.call$
    ])
      .pipe(
        map(([userCanApply, callIsReadable, callIsEditable, callIsPublished, funds, call]) => ({userCanApply, callIsReadable, callIsEditable, callIsPublished, funds: this.getFunds(funds, call)})),
      )
  }

  private resetForm(funds: ProgrammeFundDTO[], isCallReadeable: boolean): void {
    if (!isCallReadeable) {
      funds = funds.filter((fund) => fund.selected);
    }

    this.fundArray.clear();

    funds.forEach(fund => {
      this.fundArray.push(this.formBuilder.group({
        fundId: fund.id,
        fundSelected: this.formBuilder.control(fund.selected),
        fundRateValue: this.formBuilder.control('80'),
        fundRateType: this.formBuilder.control(fund.selected),
      }))
    });

    this.formService.resetEditable();
    this.formService.init(this.callFundsForm);
    this.changeDetectorRef.detectChanges();
  }

  fundDisabled(callIsEditable: boolean, callIsPublished: boolean, fund: ProgrammeFundDTO, initialFunds: ProgrammeFundDTO[]): boolean {
    if (!callIsEditable) {
      return true;
    }
    if (callIsPublished) {
      const foundFund = initialFunds.find(searchedFund => searchedFund.id === fund.id);
      return !!(foundFund && foundFund.selected);
    }
    return false;
  }

  toggleFundType(formControl: FormControl, $event: boolean): void {
    formControl.setValue($event);
    this.callFundsForm.markAsDirty();
  }

   fundRateValue(fundIndex: number): FormControl {
    return this.fundArray.at(fundIndex).get('fundRateValue') as FormControl;
  }

  fundType(fundIndex: number): FormControl {
    return this.fundArray.at(fundIndex).get('fundRateValue') as FormControl;
  }

  get fundArray(): FormArray {
    return this.callFundsForm.get('fundArray') as FormArray;
  }

  private getFunds(allFunds: ProgrammeFundDTO[], call: CallDetailDTO): ProgrammeFundDTO[] {
    const savedFunds = allFunds
      .filter(fund => fund.selected)
      .map(element => ({
        id: element.id,
        abbreviation: element.abbreviation,
        description: element.description,
        selected: false
      } as ProgrammeFundDTO));
    if (!call || !call.funds?.length) {
      return savedFunds;
    }
    const callFundIds = call.funds.map(element => element.id ? element.id : element);
    savedFunds
      .filter(element => callFundIds.includes(element.id))
      .forEach(element => element.selected = true);
    return savedFunds;
  }
}
