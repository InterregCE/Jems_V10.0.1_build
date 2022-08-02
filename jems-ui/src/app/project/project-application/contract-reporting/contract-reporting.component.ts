import {Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector:    'jems-contract-reporting',
  templateUrl: './contract-reporting.component.html',
  styleUrls: ['./contract-reporting.component.scss'],
  providers: [FormService],
})
export class ContractReportingComponent {

}
