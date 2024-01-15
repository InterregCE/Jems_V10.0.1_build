import {Pipe, PipeTransform} from '@angular/core';
import {AccountingYearDTO} from '@cat/api';
import {LocaleDatePipeUtil} from '@common/utils/locale-date-pipe-util';
import {UntilDestroy} from '@ngneat/until-destroy';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';

@UntilDestroy()
@Pipe({name: 'asAccountingYear'})
export class AccountingYearPipe implements PipeTransform {

  constructor(
    private translatePipe: CustomTranslatePipe,
  ) {}

  transform(accountingYear: AccountingYearDTO | null): any {
    if (!accountingYear) {
      return '';
    }

    const year = this.translatePipe.transform('accounting.year.' + accountingYear.year);
    const startDate = LocaleDatePipeUtil.transform(accountingYear.startDate);
    const endDate = LocaleDatePipeUtil.transform(accountingYear.endDate);

    return `${year}: ${startDate} - ${endDate}`;
  }

}
