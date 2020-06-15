import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class AuthenticationHolder {

  currentUsername: string | null = null;

}
