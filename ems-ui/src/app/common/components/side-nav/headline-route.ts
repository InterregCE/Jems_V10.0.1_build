import {HeadlineType} from '@common/components/side-nav/headline-type';

export class HeadlineRoute {
  headline: string;
  route?: string;
  scrollRoute?: string;
  type?: HeadlineType;
  paddingLeft?: number;
  paddingTop?: number;
  fontSize?: string;
  bullets?: HeadlineRoute[]; // used if type=BULLETS
}
