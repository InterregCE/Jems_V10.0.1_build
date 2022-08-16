import {ChangeDetectionStrategy, Component} from '@angular/core';
import {PaymentsPageStore} from "../payments-store.service";

@Component({
  selector: 'advanced-payments-page',
  templateUrl: './advanced-payments-page.component.html',
  providers: [PaymentsPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdvancedPaymentsPageComponent {

}
