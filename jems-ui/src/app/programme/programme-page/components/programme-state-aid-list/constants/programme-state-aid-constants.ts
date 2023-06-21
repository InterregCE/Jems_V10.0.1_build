import {InputTranslation, ProgrammeStateAidDTO} from '@cat/api';
import {DEFAULT_FALLBACK_LANGUAGE} from '@common/services/language-store.service';

export interface ProgrammeStateAidMeasureRelation {
  measure: ProgrammeStateAidDTO.MeasureEnum;
  measureDisplayValue: string;
  name?: InputTranslation[];
  abbreviatedName: InputTranslation[];
  maxIntensity?: number;
  threshold?: number;
  comments?: InputTranslation[];
}

export class ProgrammeStateAidConstants {
  private static readonly GENERAL_DE_MINIMIS = 'General de minimis';
  private static readonly ROAD_FREIGHT_DE_MINIMIS = 'Road freight de minimis';
  private static readonly AGRICULTURAL_DE_MINIMIS = 'Agricultural de minimis';
  private static readonly FISHER_AND_AQUA_CULTURE_SECTOR_DE_MINIMIS = 'Fishery and aquaculture sector de minimis';
  private static readonly SGEI_DE_MINIMIS = 'SGEI de minimis';

  static stateAidMeasures: ProgrammeStateAidMeasureRelation[] = [
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GENERALDEMINIMIS,
      measureDisplayValue: ProgrammeStateAidConstants.GENERAL_DE_MINIMIS,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: ProgrammeStateAidConstants.GENERAL_DE_MINIMIS}],
      abbreviatedName: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: ProgrammeStateAidConstants.GENERAL_DE_MINIMIS
      }],
      threshold: 200000,
      comments: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'per MS'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.ROADFREIGHTDEMINIMIS,
      measureDisplayValue: ProgrammeStateAidConstants.ROAD_FREIGHT_DE_MINIMIS,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: ProgrammeStateAidConstants.ROAD_FREIGHT_DE_MINIMIS}],
      abbreviatedName: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: ProgrammeStateAidConstants.ROAD_FREIGHT_DE_MINIMIS
      }],
      threshold: 100000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.AGRICULTURALDEMINIMIS,
      measureDisplayValue: ProgrammeStateAidConstants.AGRICULTURAL_DE_MINIMIS,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: ProgrammeStateAidConstants.AGRICULTURAL_DE_MINIMIS}],
      abbreviatedName: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: ProgrammeStateAidConstants.AGRICULTURAL_DE_MINIMIS
      }],
      threshold: 25000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.FISHERANDAQUACULTURESECTORDEMINIMIS,
      measureDisplayValue: ProgrammeStateAidConstants.FISHER_AND_AQUA_CULTURE_SECTOR_DE_MINIMIS,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: ProgrammeStateAidConstants.FISHER_AND_AQUA_CULTURE_SECTOR_DE_MINIMIS
      }],
      abbreviatedName: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: ProgrammeStateAidConstants.FISHER_AND_AQUA_CULTURE_SECTOR_DE_MINIMIS
      }],
      threshold: 30000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.SGEIDEMINIMIS,
      measureDisplayValue: ProgrammeStateAidConstants.SGEI_DE_MINIMIS,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: ProgrammeStateAidConstants.SGEI_DE_MINIMIS}],
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: ProgrammeStateAidConstants.SGEI_DE_MINIMIS}],
      threshold: 500000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE14,
      measureDisplayValue: 'GBER Article 14',
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Regional investment aid'}],
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 14'}],
      threshold: 100000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE15,
      measureDisplayValue: 'GBER Article 15',
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Regional operating aid'}],
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 15'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE16,
      measureDisplayValue: 'GBER Article 16',
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Regional urban development aid'}],
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 16'}],
      threshold: 20000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE17,
      measureDisplayValue: 'GBER Article 17',
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Investment aid to SMEs'}],
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 17'}],
      maxIntensity: 10,
      threshold: 7500000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE18,
      measureDisplayValue: 'GBER Article 18',
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for consultancy in favour of SMEs'}],
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 18'}],
      maxIntensity: 50,
      threshold: 2000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE19,
      measureDisplayValue: 'GBER Article 19',
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid to SMEs for participation in fairs'}],
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 19'}],
      maxIntensity: 50,
      threshold: 2000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE19A,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for costs incurred by SMEs participating in community-led local development (“CLLD”) or European Innovation Partnership for agricultural productivity and sustainability (“EIP”) Operational Group projects'
      }],
      measureDisplayValue: 'GBER Article 19a',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 19a'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE19B,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Limited amounts of aid to SMEs benefitting from community-led local development (“CLLD”) or European Innovation Partnership for agricultural productivity and sustainability (“EIP”) Operational Group projects'
      }],
      measureDisplayValue: 'GBER Article 19b',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 19b'}],
      threshold: 200000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE20,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for costs incurred by undertakings participating in European Territorial Cooperation project'
      }],
      measureDisplayValue: 'GBER Article 20',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 20'}],
      maxIntensity: 80,
      threshold: 2000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE20A,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Limited amounts of aid to undertakings for participation in European Territorial Cooperation projects'
      }],
      measureDisplayValue: 'GBER Article 20a',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 20a'}],
      threshold: 20000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE21,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Risk finance aid'}],
      measureDisplayValue: 'GBER Article 21',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 21'}],
      threshold: 15000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE22,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for start-ups'}],
      measureDisplayValue: 'GBER Article 22',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 22'}],
      threshold: 400000,
      comments: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'grants, including equity or quasi equity investment, interests rate and guarantee premium reductions up to EUR 0,4 million gross grant equivalent or EUR 0,6 million for undertakings established in assisted areas fulfilling the conditions of Article 107(3)(c) of the Treaty, or EUR 0,8 million for undertakings established in assisted areas fulfilling the conditions of Article 107(3)(a) of the Treaty.'
      }],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE23,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid to alternative trading platforms specialised in SME'
      }],
      measureDisplayValue: 'GBER Article 23',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 23'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE24,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for scouting costs'}],
      measureDisplayValue: 'GBER Article 24',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 24'}],
      maxIntensity: 50,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25PARA,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for research and development projects (a) fundamental research'
      }],
      measureDisplayValue: 'GBER Article 25 par. (a)',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25 par. (a)'}],
      maxIntensity: 100,
      threshold: 40000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25PARB,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for research and development projects (b) fundamental research'
      }],
      measureDisplayValue: 'GBER Article 25 par. (b)',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25 par. (b)'}],
      maxIntensity: 50,
      threshold: 20000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25PARC,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for research and development projects (c) fundamental research'
      }],
      measureDisplayValue: 'GBER Article 25 par. (c)',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25 par. (c)'}],
      maxIntensity: 25,
      threshold: 15000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25PARD,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for research and development projects (d) fundamental research'
      }],
      measureDisplayValue: 'GBER Article 25 par. (d)',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25 par. (d)'}],
      maxIntensity: 50,
      threshold: 7500000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25A,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for projects awarded a Seal of Excellence quality label'
      }],
      measureDisplayValue: 'GBER Article 25a',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25a'}],
      threshold: 2500000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25B,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for Marie Skłodowska-Curie actions and ERC Proof of Concept actions'
      }],
      measureDisplayValue: 'GBER Article 25b',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25b'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25C,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid involved in co-funded research and development projects'
      }],
      measureDisplayValue: 'GBER Article 25c',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25c'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE25D,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for Teaming actions'}],
      measureDisplayValue: 'GBER Article 25d',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 25d'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE26,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for research infrastructures (investment aid)'}],
      measureDisplayValue: 'GBER Article 26',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 26'}],
      maxIntensity: 50,
      threshold: 20000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE27,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for innovation clusters'}],
      measureDisplayValue: 'GBER Article 27',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 27'}],
      maxIntensity: 50,
      threshold: 7500000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE28,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Innovation aid for SMEs'}],
      measureDisplayValue: 'GBER Article 28',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 28'}],
      maxIntensity: 50,
      threshold: 5000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE29,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for process and organisational innovation'}],
      measureDisplayValue: 'GBER Article 29',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 29'}],
      maxIntensity: 15,
      threshold: 7500000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE30,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for research and development in the fishery and acquaculture sector'
      }],
      measureDisplayValue: 'GBER Article 30',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 30'}],
      maxIntensity: 100,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE31,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Training aid'}],
      measureDisplayValue: 'GBER Article 31',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 31'}],
      maxIntensity: 50,
      threshold: 2000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE32,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for the recruitment of disadvantaged workers in the form of wage subsidies'
      }],
      measureDisplayValue: 'GBER Article 32',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 32'}],
      maxIntensity: 50,
      threshold: 5000000,
      comments: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'per undertaking per year'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE33,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for the employment of workers with disabilities in the form of wage subsidies'
      }],
      measureDisplayValue: 'GBER Article 33',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 33'}],
      maxIntensity: 75,
      threshold: 10000000,
      comments: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'per undertaking per year'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE34,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for compensating the additional costs of employing workers with disabilities'
      }],
      measureDisplayValue: 'GBER Article 34',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 34'}],
      maxIntensity: 100,
      threshold: 10000000,
      comments: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'per undertaking per year'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE35,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for compensating the costs of assistance provided to disadvantaged workers'
      }],
      measureDisplayValue: 'GBER Article 35',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 35'}],
      maxIntensity: 50,
      threshold: 5000000,
      comments: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'per undertaking per year'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE36,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid enabling undertakings to go beyond Union standards for environmental protection or to increase the level of environmental protection in the absence of Union standards'
      }],
      measureDisplayValue: 'GBER Article 36',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 36'}],
      maxIntensity: 40,
      threshold: 15000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE36A,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid for publicly accessible recharging or refuelling infrastructure for zero and low emission road vehicles'
      }],
      measureDisplayValue: 'GBER Article 36a',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 36a'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE37,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid for early adaptation to future Union standards'
      }],
      measureDisplayValue: 'GBER Article 37',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 37'}],
      maxIntensity: 15,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE38,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Investment aid for energy efficiency measures'}],
      measureDisplayValue: 'GBER Article 38',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 38'}],
      maxIntensity: 30,
      threshold: 10000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE39,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid for energy efficiency projects in buildings in the form of financial instruments'
      }],
      measureDisplayValue: 'GBER Article 39',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 39'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE40,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Investment for high -efficiency cogeneration'}],
      measureDisplayValue: 'GBER Article 40',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 40'}],
      maxIntensity: 45,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE41,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid for the promotion of energy from renewable sources'
      }],
      measureDisplayValue: 'GBER Article 41',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 41'}],
      maxIntensity: 30,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE42,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Operating aid for the promotion of electricity from renewable sources'
      }],
      measureDisplayValue: 'GBER Article 42',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 42'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE43,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Operating aid for the promotion of energy from renewable sources in small scale installations'
      }],
      measureDisplayValue: 'GBER Article 43',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 43'}],
      threshold: 15000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE44,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid in the form of reductions in environmental taxes under Directive 2003/96/EC'
      }],
      measureDisplayValue: 'GBER Article 44',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 44'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE45,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid for remediation of contaminated sites'
      }],
      measureDisplayValue: 'GBER Article 45',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 45'}],
      maxIntensity: 100,
      threshold: 20000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE46,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid for energy efficient district heating and cooling'
      }],
      measureDisplayValue: 'GBER Article 46',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 46'}],
      maxIntensity: 45,
      threshold: 20000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE47,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Investment aid for waste recycling and re-utilisation'
      }],
      measureDisplayValue: 'GBER Article 47',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 47'}],
      maxIntensity: 35,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE48,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Investment aid for energy infrastructure'}],
      measureDisplayValue: 'GBER Article 48',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 48'}],
      threshold: 50000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE49,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for environmental studies'}],
      measureDisplayValue: 'GBER Article 49',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 49'}],
      maxIntensity: 50,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE50,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid schemes to make good the damage caused by certain natural disasters'
      }],
      measureDisplayValue: 'GBER Article 50',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 50'}],
      maxIntensity: 100,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE51,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Social aid for transport for residents of remote regions'
      }],
      measureDisplayValue: 'GBER Article 51',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 51'}],
      maxIntensity: 100,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE52,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for fixed broadband networks'}],
      measureDisplayValue: 'GBER Article 52',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 52'}],
      threshold: 70000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE52A,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for 4G and 5G mobile networks'}],
      measureDisplayValue: 'GBER Article 52a',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 52a'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE52B,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for projects of common interest in the area of trans-European digital connectivity infrastructure'
      }],
      measureDisplayValue: 'GBER Article 52b',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 52b'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE52C,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Connectivity vouchers'}],
      measureDisplayValue: 'GBER Article 52c',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 52c'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE53,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for culture and heritage conservation'}],
      measureDisplayValue: 'GBER Article 53',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 53'}],
      maxIntensity: 80,
      threshold: 75000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE54,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid schemes for audiovisual works'}],
      measureDisplayValue: 'GBER Article 54',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 54'}],
      maxIntensity: 50,
      threshold: 50000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE55,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Aid for sport and multifunctional recreational infrastructures'
      }],
      measureDisplayValue: 'GBER Article 55',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 55'}],
      maxIntensity: 80,
      threshold: 2000000,
      comments: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'for investment aid for sport and multifunctional recreational infrastructures: EUR 30 million or the total costs exceeding EUR 100 million per project; operating aid for sport infrastructure: EUR 2 million per infrastructure per year;'
      }],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE56,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Investment aid for local infrastructures'}],
      measureDisplayValue: 'GBER Article 56',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 56'}],
      threshold: 10000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE56A,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for regional airports'}],
      measureDisplayValue: 'GBER Article 56a',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 56a'}],
      maxIntensity: 50,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE56B,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for maritime ports'}],
      measureDisplayValue: 'GBER Article 56b',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 56b'}],
      maxIntensity: 60,
      threshold: 130000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE56C,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Aid for inland ports'}],
      measureDisplayValue: 'GBER Article 56c',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 56c'}],
      maxIntensity: 100,
      threshold: 40000000,
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE56E,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Conditions for aid involved in financial products supported by the InvestEU Fund'
      }],
      measureDisplayValue: 'GBER Article 56e',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 56e'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.GBERARTICLE56F,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'Conditions for aid involved in intermediated commercially-driven financial products supported by the InvestEU Fund'
      }],
      measureDisplayValue: 'GBER Article 56f',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'GBER Article 56f'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.INDIRECTAID,
      name: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Indirect aid (other than GBER Article 20a)'}],
      measureDisplayValue: 'Indirect Aid',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Indirect Aid'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.RDIFRAMEWORK,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'State aid Framework for Research, Development and Innovation'
      }],
      measureDisplayValue: 'RDI Framework',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'RDI Framework'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.SGEIFRAMEWORK,
      name: [{
        language: DEFAULT_FALLBACK_LANGUAGE,
        translation: 'State aid Framework for Services of General Economic Interest'
      }],
      measureDisplayValue: 'SGEI Framework',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'SGEI Framework'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.OTHER1,
      measureDisplayValue: 'Other 1',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Other 1'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.OTHER2,
      measureDisplayValue: 'Other 2',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Other 2'}],
    },
    {
      measure: ProgrammeStateAidDTO.MeasureEnum.OTHER3,
      measureDisplayValue: 'Other 3',
      abbreviatedName: [{language: DEFAULT_FALLBACK_LANGUAGE, translation: 'Other 3'}],
    },
  ];
}
