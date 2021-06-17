import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CallService, UserRoleDTO} from '@cat/api';
import {Router} from '@angular/router';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-call-page',
  templateUrl: './call-page.component.html',
  styleUrls: ['./call-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallPageComponent implements OnInit {
  PermissionsEnum = PermissionsEnum;

  success = this.router.getCurrentNavigation()?.extras?.state?.success;

  constructor(private callService: CallService,
              private router: Router,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    if (this.success) {
      setTimeout(() => {
        this.success = null;
        this.changeDetectorRef.markForCheck();
      },         3000);
    }
  }
}
