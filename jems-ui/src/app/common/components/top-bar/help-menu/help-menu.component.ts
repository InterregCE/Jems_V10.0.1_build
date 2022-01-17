import {Component, OnInit} from '@angular/core';
import {InfoService} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-help-menu',
  templateUrl: './help-menu.component.html',
  styleUrls: ['./help-menu.component.scss']
})
export class HelpMenuComponent implements OnInit {
  Permission = Permission;

  version: string;
  helpdeskUrl: string;
  helpdeskEmail: string;
  accessibilityStatementUrl: string;
  termsAndPrivacyPolicyUrl: string;

  constructor(private infoService: InfoService) {
  }

  ngOnInit(): void {
    this.infoService.getVersionInfo().pipe(
      tap(info => {
        this.version = info.version;
        this.helpdeskUrl = info.helpdeskUrl;
        this.helpdeskEmail = info.helpdeskEmail;
        this.accessibilityStatementUrl = info.accessibilityStatementUrl;
        this.termsAndPrivacyPolicyUrl = info.termsAndPrivacyPolicyUrl;
      }),
      untilDestroyed(this),
    ).subscribe();
  }

}
