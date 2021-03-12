import {Directive, Host, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {CurrencyMaskConfig, CurrencyMaskDirective} from 'ngx-currency';
import {NumberService} from '../services/number.service';

/**
 * Extends the CurrencyMaskDirective with the NumberService settings.
 */
@Directive({
  // tslint:disable-next-line:directive-selector
  selector: '[currencyMask]',
})
export class CurrencyDirective implements OnInit, OnChanges {
  @Input()
  options?: Partial<CurrencyMaskConfig>;
  @Input()
  type: 'decimal' | 'integer';

  constructor(@Host() public currencyMaskDirective: CurrencyMaskDirective,
              private numberService: NumberService) {
  }

  ngOnInit(): void {
    this.refreshOptions();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.options) {
      this.refreshOptions();
    }
  }

  private refreshOptions(): void {
    this.currencyMaskDirective.options = this.type === 'decimal'
      ? this.numberService.decimalInput(this.options) : this.numberService.integerInput(this.options);
    this.currencyMaskDirective.ngDoCheck();
  }
}
