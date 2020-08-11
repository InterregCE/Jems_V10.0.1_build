import {HeadlineType} from '@common/components/side-nav/headline-type';

export class HeadlineRoute{
  constructor(headline: string, route: string, type: HeadlineType) {
    this.headline = headline;
    this.route = route;
    this.type = type;
  }

  headline: string;
  route: string;
  type: HeadlineType;
}
