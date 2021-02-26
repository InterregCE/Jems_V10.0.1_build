import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {mergeMap, map, startWith, tap} from 'rxjs/operators';
import {Tables} from '../../../common/utils/tables';
import {Log} from '../../../common/utils/log';
import {MatSort} from '@angular/material/sort';
import {CallService} from '@cat/api';
import {Permission} from '../../../security/permissions/permission';
import {Router} from '@angular/router';

@Component({
  selector: 'app-call-page',
  templateUrl: './call-page.component.html',
  styleUrls: ['./call-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallPageComponent implements OnInit {
  Permission = Permission;

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
