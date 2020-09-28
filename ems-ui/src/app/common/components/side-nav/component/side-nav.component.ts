import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideNavComponent {

  @Input()
  headlines: HeadlineRoute[];

  constructor( public sideNavService: SideNavService){}

  getExpansionDisabledStatus(bullets: HeadlineRoute[]): boolean{
    return !bullets || bullets.length === 0;
  }

}
