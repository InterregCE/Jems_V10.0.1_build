import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

@Component({
  selector: 'jems-filter-template',
  templateUrl: './filter-template.component.html',
  styleUrls: ['./filter-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FilterTemplateComponent {

  @Input()
  isThereAnyActiveFilter: boolean;

}
