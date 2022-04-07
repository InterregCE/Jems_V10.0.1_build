import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ResourceStoreService} from '@common/services/resource-store.service';

@Component({
  selector: 'jems-public-page-template',
  templateUrl: './public-page-template.component.html',
  styleUrls: ['./public-page-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PublicPageTemplateComponent  {

  largeLogo$ = this.resourceStore.largeLogo$;

  constructor(public resourceStore: ResourceStoreService) { }

}
