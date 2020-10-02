import {Injectable} from '@angular/core';
import {AuthenticationService, LoginRequest, OutputCurrentUser, OutputUserWithRole, UserService} from '@cat/api';
import {BehaviorSubject, Observable, of, ReplaySubject} from 'rxjs';
import {catchError, flatMap, map, shareReplay, take, tap} from 'rxjs/operators';
import {Log} from '../common/utils/log';

@Injectable({providedIn: 'root'})
export class ThemeService {

  public $currentTheme: BehaviorSubject<string> = new BehaviorSubject<string>('light-theme');

}
