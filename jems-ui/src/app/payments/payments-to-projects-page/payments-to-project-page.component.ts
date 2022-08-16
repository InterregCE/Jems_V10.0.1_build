import {ChangeDetectionStrategy, Component} from '@angular/core';
import {PaymentsPageStore} from "../payments-store.service";

@Component({
  selector: 'payments-to-projects-page',
  templateUrl: './payments-to-projects-page.component.html',
  providers: [PaymentsPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsToProjectPageComponent {
}
