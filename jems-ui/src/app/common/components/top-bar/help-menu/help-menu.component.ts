import {Component} from '@angular/core';
import {InfoService, VersionDTO} from '@cat/api';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-help-menu',
  templateUrl: './help-menu.component.html',
  styleUrls: ['./help-menu.component.scss']
})
export class HelpMenuComponent {

  version$: Observable<VersionDTO>;

  constructor(private infoService: InfoService) {
    this.version$ = this.infoService.getVersionInfo();
  }

}
