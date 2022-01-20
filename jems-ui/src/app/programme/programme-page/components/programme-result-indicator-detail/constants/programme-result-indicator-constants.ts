export interface ResultIndicatorCodeRelation {
  code: string;
  name: string;
  measurementUnit: string;
}

export class ProgrammeResultIndicatorConstants {
  private static readonly MEASUREMENT_UNIT_USER_YEAR = 'users/year';
  private static readonly MEASUREMENT_UNIT_TONNES_YEAR = 'tonnes/year';

  static indicatorCodes: ResultIndicatorCodeRelation[] = [
    {
      code: 'RCR01',
      name: 'Jobs created in supported entities',
      measurementUnit: 'annual FTEs'
    },
    {
      code: 'RCR02',
      name: 'Private investments matching public support (of which: grants, financial instruments)',
      measurementUnit: 'euro'
    },
    {
      code: 'RCR03',
      name: 'Small and medium-sized enterprises (SMEs) introducing product or process innovation',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR04',
      name: 'SMEs introducing marketing or organisational innovation',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR05',
      name: 'SMEs innovating in-house',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR06',
      name: 'Patent applications submitted',
      measurementUnit: 'patent applications'
    },
    {
      code: 'RCR07',
      name: 'Trademark and design applications',
      measurementUnit: 'trademark and design applications'
    },
    {
      code: 'RCR08',
      name: 'Publications from supported projects',
      measurementUnit: 'publications'
    },
    {
      code: 'RCR11',
      name: 'Users of new and upgraded public digital services, products and processes',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR12',
      name: 'Users of new and upgraded digital services, products and processes developed by enterprises',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR13',
      name: 'Enterprises reaching high digital intensity',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR17',
      name: 'New enterprises surviving in the market',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR18',
      name: 'SMEs using incubator services after incubator creation',
      measurementUnit: 'enterprises/year'
    },
    {
      code: 'RCR19',
      name: 'Enterprises with higher turnover',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR25',
      name: 'SMEs with higher value added per employee',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR26',
      name: 'Annual primary energy consumption (of which: dwellings, public buildings, enterprises, other)',
      measurementUnit: 'MWh/year'
    },
    {
      code: 'RCR29',
      name: 'Estimated greenhouse gas emissions',
      measurementUnit: 'tonnes CO2 eq./year'
    },
    {
      code: 'RCR31',
      name: 'Total renewable energy produced (of which: electricity, thermal)',
      measurementUnit: 'MWh/year'
    },
    {
      code: 'RCR32',
      name: 'Additional operational capacity installed for renewable energy',
      measurementUnit: 'MW'
    },
    {
      code: 'RCR33',
      name: 'Users connected to smart energy systems',
      measurementUnit: 'end users/year'
    },
    {
      code: 'RCR34',
      name: 'Roll-out of projects for smart energy systems',
      measurementUnit: 'projects'
    },
    {
      code: 'RCR35',
      name: 'Population benefiting from flood protection measures',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR36',
      name: 'Population benefiting from wildfire protection measures',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR37',
      name: 'Population benefiting from protection measures against climate related natural disaster (other than flood and wildfires)',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR41',
      name: 'Population connected to improved public water supply',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR42',
      name: 'Population connected to at least secondary public waste water treatment',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR43',
      name: 'Water losses in distribution systems for public water supply',
      measurementUnit: 'cubic metres per year'
    },
    {
      code: 'RCR47',
      name: 'Waste recycled',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_TONNES_YEAR
    },
    {
      code: 'RCR48',
      name: 'Waste used as raw materials',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_TONNES_YEAR
    },
    {
      code: 'RCR50',
      name: 'Population benefiting from measures for air quality',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR52',
      name: 'Rehabilitated land used for green areas, social housing, economic or other uses',
      measurementUnit: 'hectares'
    },
    {
      code: 'RCR53',
      name: 'Dwellings with broadband subscriptions to a very high capacity network',
      measurementUnit: 'dwellings'
    },
    {
      code: 'RCR54',
      name: 'Enterprises with broadband subscriptions to a very high capacity network',
      measurementUnit: 'enterprises'
    },
    {
      code: 'RCR55',
      name: 'Annual users of newly built, reconstructed, upgraded or modernised roads',
      measurementUnit: 'passenger-km/year'
    },
    {
      code: 'RCR56',
      name: 'Time savings due to improved road infrastructures',
      measurementUnit: 'man-days/year'
    },
    {
      code: 'RCR58',
      name: 'Annual users of newly, built, upgraded, reconstructed or modernised railways',
      measurementUnit: 'passenger-km/year'
    },
    {
      code: 'RCR59',
      name: 'Freight transport on rail',
      measurementUnit: 'tonnes-km/year'
    },
    {
      code: 'RCR60',
      name: 'Freight transport on inland waterways',
      measurementUnit: 'tonnes-km/year'
    },
    {
      code: 'RCR62',
      name: 'Annual users of new or modernised public transport',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR63',
      name: 'Annual users of new or modernised tram and metro lines',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR64',
      name: 'Annual users of dedicated cycling infrastructure',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR65',
      name: 'Annual users of new or modernised facilities for employment services',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR66',
      name: 'Annual users of new or modernised temporary reception facilities',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR67',
      name: 'Annual users of new or modernised social housing',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR70',
      name: 'Annual users of new or modernised childcare facilities',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR71',
      name: 'Annual users of new or modernised education facilities',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR72',
      name: 'Annual users of new or modernised e-health care services',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR73',
      name: 'Annual users of new or modernised health care facilities',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR74',
      name: 'Annual users of new or modernised social care facilities',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_USER_YEAR
    },
    {
      code: 'RCR77',
      name: 'Visitors of cultural and tourism sites supported',
      measurementUnit: 'visitors/year'
    },
    {
      code: 'RCR79',
      name: 'Joint strategies and action plans taken up by organisations',
      measurementUnit: 'joint strategy/action plan'
    },
    {
      code: 'RCR81',
      name: 'Completion of joint training schemes',
      measurementUnit: 'participants'
    },
    {
      code: 'RCR82',
      name: 'Legal or administrative obstacles across borders alleviated or resolved',
      measurementUnit: 'legal or administrative obstacles'
    },
    {
      code: 'RCR83',
      name: 'Persons covered by joint administrative or legal agreements signed',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR84',
      name: 'Organisations cooperating across borders after project completion',
      measurementUnit: 'organisations'
    },
    {
      code: 'RCR85',
      name: 'Participations in joint actions across borders after project completion',
      measurementUnit: 'participations'
    },
    {
      code: 'RCR95',
      name: 'Population having access to new or improved green infrastructure',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR96',
      name: 'Population benefiting from protection measures against non-climate related natural risks and risks related to human activities',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR97',
      name: 'Apprenticeships supported in SMEs',
      measurementUnit: 'persons'
    },
    {
      code: 'RCR98',
      name: 'SMEs staff completing training for skills for smart specialisation, for industrial transition and entrepreneurship (by type of skill: technical, management, entrepreneurship, green, other)',
      measurementUnit: 'participants'
    },
    {
      code: 'RCR101',
      name: 'Time savings due to improved rail infrastructures',
      measurementUnit: 'man-days/year'
    },
    {
      code: 'RCR102',
      name: 'Research jobs created in supported entities',
      measurementUnit: 'annual FTEs'
    },
    {
      code: 'RCR103',
      name: 'Waste collected separately',
      measurementUnit: ProgrammeResultIndicatorConstants.MEASUREMENT_UNIT_TONNES_YEAR
    },
    {
      code: 'RCR104',
      name: 'Solutions taken up or up-scaled by organisations',
      measurementUnit: 'solutions'
    },
    {
      code: 'RCR105',
      name: 'Estimated greenhouse emissions by boilers and heating systems converted from solid fossil fuels to gas',
      measurementUnit: 'tonnes CO2 eq./year',
    },
  ];
}
