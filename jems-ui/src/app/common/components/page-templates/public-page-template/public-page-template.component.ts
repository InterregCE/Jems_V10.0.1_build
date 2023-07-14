import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ResourceStoreService} from '@common/services/resource-store.service';
import {Alert} from '../../forms/alert';

@Component({
  selector: 'jems-public-page-template',
  templateUrl: './public-page-template.component.html',
  styleUrls: ['./public-page-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PublicPageTemplateComponent {

  largeLogo$ = this.resourceStore.largeLogo$;
  userAgent$: string;
  Alert = Alert;
  browserAgents = {
    firefox: {
      version: 82,
      link: 'https://www.mozilla.org',
      regex: 'Firefox/(.*)'
    },
    chrome: {
      version: 85,
      link: 'https://www.google.com/chrome',
      regex: 'Chrome\/([^.]*)'
    },
    edge: {
      version: 84,
      link: 'https://www.microsoft.com/edge',
      regex: 'Edg\/([^.]*)'
    }
  };

  constructor(public resourceStore: ResourceStoreService) {
    this.userAgent$ = window.navigator.userAgent;
  }

  validateUserAgent(): boolean {
    return this.validateBrowser(this.browserAgents.firefox.regex, this.browserAgents.firefox.version) || this.validateBrowser(this.browserAgents.edge.regex, this.browserAgents.edge.version) || this.validateBrowser(this.browserAgents.chrome.regex, this.browserAgents.chrome.version);
  }

  validateBrowser(regex: string, reqVersion: number): boolean {
    const match = this.userAgent$.match(regex);
    if (match == null) {
      return false;
    }

    const version = Number(match ? match[1] : -1);
    return version >= reqVersion;
  }
}
