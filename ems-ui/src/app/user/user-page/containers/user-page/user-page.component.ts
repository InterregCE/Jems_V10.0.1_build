import {Component} from '@angular/core';
import {UserPageService} from '../../services/user-page/user-page.service';
import {Observable} from 'rxjs';
import {OutputAccount} from '@cat/api';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss']
})
export class UserPageComponent {
  filtered$: Observable<OutputAccount[]>;

  constructor(private userPageService: UserPageService) {
    this.filtered$ = this.userPageService.filtered();
    this.userPageService.newPage(0, 100, 'id,desc');
  }
}
