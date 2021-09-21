import {Injectable} from '@angular/core';
import {LogoDTO, ResourcesService} from '@cat/api';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class ResourceStoreService {

  smallLogo$: Observable<LogoDTO | undefined>;
  mediumLogo$: Observable<LogoDTO | undefined>;
  largeLogo$: Observable<LogoDTO | undefined>;

  private logos$: Observable<LogoDTO[]>;

  constructor(private resourcesService: ResourcesService) {
    this.logos$ = this.resourcesService.getLogos()
      .pipe(
        shareReplay(1)
      );
    this.smallLogo$ = this.getLogo(LogoDTO.LogoTypeEnum.SMALL);
    this.mediumLogo$ = this.getLogo(LogoDTO.LogoTypeEnum.MEDIUM);
    this.largeLogo$ = this.getLogo(LogoDTO.LogoTypeEnum.LARGE);
  }

  private getLogo(logoType: string): Observable<LogoDTO | undefined> {
    return this.logos$
      .pipe(
        map(logos => logos.find(logo => logo.logoType === logoType))
      );
  }
}
