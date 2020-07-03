import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class AuthenticationHolder {

  currentUserId: number | null = null;
  currentUsername: string | null = null;

}
