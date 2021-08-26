export class ProjectApplicationFormPartnerEditConstants {

  public static EUROSTAT_WEBSITE = 'https://ec.europa.eu/eurostat/ramon/nomenclatures/index.cfm?TargetUrl=LST_NOM_DTL&StrNom=NACE_REV2&StrLanguageCode=EN&IntPcKey=&StrLayoutCode=HIERARCHIC';

  public static partnerTypeEnums = [
    'LocalPublicAuthority',
    'RegionalPublicAuthority',
    'NationalPublicAuthority',
    'SectoralAgency',
    'InfrastructureAndServiceProvider',
    'InterestGroups',
    'HigherEducationOrganisations',
    'EducationTrainingCentreAndSchool',
    'EnterpriseExceptSme',
    'Sme',
    'BusinessSupportOrganisation',
    'Egtc',
    'InternationalOrganisationEeig',
    'GeneralPublic',
    'Hospitals',
    'Other'
  ];

  public static partnerSubTypeEnums = [
    {
      key: 'NOT_APPLICABLE',
      value: null,
    },
    {
      key: 'MICRO_ENTERPRISE',
      value: 'MICRO_ENTERPRISE',
    },
    {
      key: 'SMALL_ENTERPRISE',
      value: 'SMALL_ENTERPRISE',
    },
    {
      key: 'MEDIUM_SIZED_ENTERPRISE',
      value: 'MEDIUM_SIZED_ENTERPRISE',
    },
    {
      key: 'LARGE_ENTERPRISE',
      value: 'LARGE_ENTERPRISE',
    }
  ];

  public static naceEnums = [
    'A',
    'A_01',
    'A_01_1',
    'A_01_11',
    'A_01_12',
    'A_01_13',
    'A_01_14',
    'A_01_15',
    'A_01_16',
    'A_01_19',
    'A_01_2',
    'A_01_21',
    'A_01_22',
    'A_01_23',
    'A_01_24',
    'A_01_25',
    'A_01_26',
    'A_01_27',
    'A_01_28',
    'A_01_29',
    'A_01_3',
    'A_01_30',
    'A_01_4',
    'A_01_41',
    'A_01_42',
    'A_01_43',
    'A_01_44',
    'A_01_45',
    'A_01_46',
    'A_01_47',
    'A_01_49',
    'A_01_5',
    'A_01_50',
    'A_01_6',
    'A_01_61',
    'A_01_62',
    'A_01_63',
    'A_01_64',
    'A_01_7',
    'A_01_70',
    'A_02',
    'A_02_1',
    'A_02_10',
    'A_02_2',
    'A_02_20',
    'A_02_3',
    'A_02_30',
    'A_02_4',
    'A_02_40',
    'A_03',
    'A_03_1',
    'A_03_11',
    'A_03_12',
    'A_03_2',
    'A_03_21',
    'A_03_22',
    'B',
    'B_05',
    'B_05_1',
    'B_05_10',
    'B_05_2',
    'B_05_20',
    'B_06',
    'B_06_1',
    'B_06_10',
    'B_06_2',
    'B_06_20',
    'B_07',
    'B_07_1',
    'B_07_10',
    'B_07_2',
    'B_07_21',
    'B_07_29',
    'B_08',
    'B_08_1',
    'B_08_11',
    'B_08_12',
    'B_08_9',
    'B_08_91',
    'B_08_92',
    'B_08_93',
    'B_08_99',
    'B_09',
    'B_09_1',
    'B_09_10',
    'B_09_9',
    'B_09_90',
    'C',
    'C_10',
    'C_10_1',
    'C_10_11',
    'C_10_12',
    'C_10_13',
    'C_10_2',
    'C_10_20',
    'C_10_3',
    'C_10_31',
    'C_10_32',
    'C_10_39',
    'C_10_4',
    'C_10_41',
    'C_10_42',
    'C_10_5',
    'C_10_51',
    'C_10_52',
    'C_10_6',
    'C_10_61',
    'C_10_62',
    'C_10_7',
    'C_10_71',
    'C_10_72',
    'C_10_73',
    'C_10_8',
    'C_10_81',
    'C_10_82',
    'C_10_83',
    'C_10_84',
    'C_10_85',
    'C_10_86',
    'C_10_89',
    'C_10_9',
    'C_10_91',
    'C_10_92',
    'C_11',
    'C_11_0',
    'C_11_01',
    'C_11_02',
    'C_11_03',
    'C_11_04',
    'C_11_05',
    'C_11_06',
    'C_11_07',
    'C_12',
    'C_12_0',
    'C_12_00',
    'C_13',
    'C_13_1',
    'C_13_10',
    'C_13_2',
    'C_13_20',
    'C_13_3',
    'C_13_30',
    'C_13_9',
    'C_13_91',
    'C_13_92',
    'C_13_93',
    'C_13_94',
    'C_13_95',
    'C_13_96',
    'C_13_99',
    'C_14',
    'C_14_1',
    'C_14_11',
    'C_14_12',
    'C_14_13',
    'C_14_14',
    'C_14_19',
    'C_14_2',
    'C_14_20',
    'C_14_3',
    'C_14_31',
    'C_14_39',
    'C_15',
    'C_15_1',
    'C_15_11',
    'C_15_12',
    'C_15_2',
    'C_15_20',
    'C_16',
    'C_16_1',
    'C_16_10',
    'C_16_2',
    'C_16_21',
    'C_16_22',
    'C_16_23',
    'C_16_24',
    'C_16_29',
    'C_17',
    'C_17_1',
    'C_17_11',
    'C_17_12',
    'C_17_2',
    'C_17_21',
    'C_17_22',
    'C_17_23',
    'C_17_24',
    'C_17_29',
    'C_18',
    'C_18_1',
    'C_18_11',
    'C_18_12',
    'C_18_13',
    'C_18_14',
    'C_18_2',
    'C_18_20',
    'C_19',
    'C_19_1',
    'C_19_10',
    'C_19_2',
    'C_19_20',
    'C_20',
    'C_20_1',
    'C_20_11',
    'C_20_12',
    'C_20_13',
    'C_20_14',
    'C_20_15',
    'C_20_16',
    'C_20_17',
    'C_20_2',
    'C_20_20',
    'C_20_3',
    'C_20_30',
    'C_20_4',
    'C_20_41',
    'C_20_42',
    'C_20_5',
    'C_20_51',
    'C_20_52',
    'C_20_53',
    'C_20_59',
    'C_20_6',
    'C_20_60',
    'C_21',
    'C_21_1',
    'C_21_10',
    'C_21_2',
    'C_21_20',
    'C_22',
    'C_22_1',
    'C_22_11',
    'C_22_19',
    'C_22_2',
    'C_22_21',
    'C_22_22',
    'C_22_23',
    'C_22_29',
    'C_23',
    'C_23_1',
    'C_23_11',
    'C_23_12',
    'C_23_13',
    'C_23_14',
    'C_23_19',
    'C_23_2',
    'C_23_20',
    'C_23_3',
    'C_23_31',
    'C_23_32',
    'C_23_4',
    'C_23_41',
    'C_23_42',
    'C_23_43',
    'C_23_44',
    'C_23_49',
    'C_23_5',
    'C_23_51',
    'C_23_52',
    'C_23_6',
    'C_23_61',
    'C_23_62',
    'C_23_63',
    'C_23_64',
    'C_23_65',
    'C_23_69',
    'C_23_7',
    'C_23_70',
    'C_23_9',
    'C_23_91',
    'C_23_99',
    'C_24',
    'C_24_1',
    'C_24_10',
    'C_24_2',
    'C_24_20',
    'C_24_3',
    'C_24_31',
    'C_24_32',
    'C_24_33',
    'C_24_34',
    'C_24_4',
    'C_24_41',
    'C_24_42',
    'C_24_43',
    'C_24_44',
    'C_24_45',
    'C_24_46',
    'C_24_5',
    'C_24_51',
    'C_24_52',
    'C_24_53',
    'C_24_54',
    'C_25',
    'C_25_1',
    'C_25_11',
    'C_25_12',
    'C_25_2',
    'C_25_21',
    'C_25_29',
    'C_25_3',
    'C_25_30',
    'C_25_4',
    'C_25_40',
    'C_25_5',
    'C_25_50',
    'C_25_6',
    'C_25_61',
    'C_25_62',
    'C_25_7',
    'C_25_71',
    'C_25_72',
    'C_25_73',
    'C_25_9',
    'C_25_91',
    'C_25_92',
    'C_25_93',
    'C_25_94',
    'C_25_99',
    'C_26',
    'C_26_1',
    'C_26_11',
    'C_26_12',
    'C_26_2',
    'C_26_20',
    'C_26_3',
    'C_26_30',
    'C_26_4',
    'C_26_40',
    'C_26_5',
    'C_26_51',
    'C_26_52',
    'C_26_6',
    'C_26_60',
    'C_26_7',
    'C_26_70',
    'C_26_8',
    'C_26_80',
    'C_27',
    'C_27_1',
    'C_27_11',
    'C_27_12',
    'C_27_2',
    'C_27_20',
    'C_27_3',
    'C_27_31',
    'C_27_32',
    'C_27_33',
    'C_27_4',
    'C_27_40',
    'C_27_5',
    'C_27_51',
    'C_27_52',
    'C_27_9',
    'C_27_90',
    'C_28',
    'C_28_1',
    'C_28_11',
    'C_28_12',
    'C_28_13',
    'C_28_14',
    'C_28_15',
    'C_28_2',
    'C_28_21',
    'C_28_22',
    'C_28_23',
    'C_28_24',
    'C_28_25',
    'C_28_29',
    'C_28_3',
    'C_28_30',
    'C_28_4',
    'C_28_41',
    'C_28_49',
    'C_28_9',
    'C_28_91',
    'C_28_92',
    'C_28_93',
    'C_28_94',
    'C_28_95',
    'C_28_96',
    'C_28_99',
    'C_29',
    'C_29_1',
    'C_29_10',
    'C_29_2',
    'C_29_20',
    'C_29_3',
    'C_29_31',
    'C_29_32',
    'C_30',
    'C_30_1',
    'C_30_11',
    'C_30_12',
    'C_30_2',
    'C_30_20',
    'C_30_3',
    'C_30_30',
    'C_30_4',
    'C_30_40',
    'C_30_9',
    'C_30_91',
    'C_30_92',
    'C_30_99',
    'C_31',
    'C_31_0',
    'C_31_01',
    'C_31_02',
    'C_31_03',
    'C_31_09',
    'C_32',
    'C_32_1',
    'C_32_11',
    'C_32_12',
    'C_32_13',
    'C_32_2',
    'C_32_20',
    'C_32_3',
    'C_32_30',
    'C_32_4',
    'C_32_40',
    'C_32_5',
    'C_32_50',
    'C_32_9',
    'C_32_91',
    'C_32_99',
    'C_33',
    'C_33_1',
    'C_33_11',
    'C_33_12',
    'C_33_13',
    'C_33_14',
    'C_33_15',
    'C_33_16',
    'C_33_17',
    'C_33_19',
    'C_33_2',
    'C_33_20',
    'D',
    'D_35',
    'D_35_1',
    'D_35_11',
    'D_35_12',
    'D_35_13',
    'D_35_14',
    'D_35_2',
    'D_35_21',
    'D_35_22',
    'D_35_23',
    'D_35_3',
    'D_35_30',
    'E',
    'E_36',
    'E_36_0',
    'E_36_00',
    'E_37',
    'E_37_0',
    'E_37_00',
    'E_38',
    'E_38_1',
    'E_38_11',
    'E_38_12',
    'E_38_2',
    'E_38_21',
    'E_38_22',
    'E_38_3',
    'E_38_31',
    'E_38_32',
    'E_39',
    'E_39_0',
    'E_39_00',
    'F',
    'F_41',
    'F_41_1',
    'F_41_10',
    'F_41_2',
    'F_41_20',
    'F_42',
    'F_42_1',
    'F_42_11',
    'F_42_12',
    'F_42_13',
    'F_42_2',
    'F_42_21',
    'F_42_22',
    'F_42_9',
    'F_42_91',
    'F_42_99',
    'F_43',
    'F_43_1',
    'F_43_11',
    'F_43_12',
    'F_43_13',
    'F_43_2',
    'F_43_21',
    'F_43_22',
    'F_43_29',
    'F_43_3',
    'F_43_31',
    'F_43_32',
    'F_43_33',
    'F_43_34',
    'F_43_39',
    'F_43_9',
    'F_43_91',
    'F_43_99',
    'G',
    'G_45',
    'G_45_1',
    'G_45_11',
    'G_45_19',
    'G_45_2',
    'G_45_20',
    'G_45_3',
    'G_45_31',
    'G_45_32',
    'G_45_4',
    'G_45_40',
    'G_46',
    'G_46_1',
    'G_46_11',
    'G_46_12',
    'G_46_13',
    'G_46_14',
    'G_46_15',
    'G_46_16',
    'G_46_17',
    'G_46_18',
    'G_46_19',
    'G_46_2',
    'G_46_21',
    'G_46_22',
    'G_46_23',
    'G_46_24',
    'G_46_3',
    'G_46_31',
    'G_46_32',
    'G_46_33',
    'G_46_34',
    'G_46_35',
    'G_46_36',
    'G_46_37',
    'G_46_38',
    'G_46_39',
    'G_46_4',
    'G_46_41',
    'G_46_42',
    'G_46_43',
    'G_46_44',
    'G_46_45',
    'G_46_46',
    'G_46_47',
    'G_46_48',
    'G_46_49',
    'G_46_5',
    'G_46_51',
    'G_46_52',
    'G_46_6',
    'G_46_61',
    'G_46_62',
    'G_46_63',
    'G_46_64',
    'G_46_65',
    'G_46_66',
    'G_46_69',
    'G_46_7',
    'G_46_71',
    'G_46_72',
    'G_46_73',
    'G_46_74',
    'G_46_75',
    'G_46_76',
    'G_46_77',
    'G_46_9',
    'G_46_90',
    'G_47',
    'G_47_1',
    'G_47_11',
    'G_47_19',
    'G_47_2',
    'G_47_21',
    'G_47_22',
    'G_47_23',
    'G_47_24',
    'G_47_25',
    'G_47_26',
    'G_47_29',
    'G_47_3',
    'G_47_30',
    'G_47_4',
    'G_47_41',
    'G_47_42',
    'G_47_43',
    'G_47_5',
    'G_47_51',
    'G_47_52',
    'G_47_53',
    'G_47_54',
    'G_47_59',
    'G_47_6',
    'G_47_61',
    'G_47_62',
    'G_47_63',
    'G_47_64',
    'G_47_65',
    'G_47_7',
    'G_47_71',
    'G_47_72',
    'G_47_73',
    'G_47_74',
    'G_47_75',
    'G_47_76',
    'G_47_77',
    'G_47_78',
    'G_47_79',
    'G_47_8',
    'G_47_81',
    'G_47_82',
    'G_47_89',
    'G_47_9',
    'G_47_91',
    'G_47_99',
    'H',
    'H_49',
    'H_49_1',
    'H_49_10',
    'H_49_2',
    'H_49_20',
    'H_49_3',
    'H_49_31',
    'H_49_32',
    'H_49_39',
    'H_49_4',
    'H_49_41',
    'H_49_42',
    'H_49_5',
    'H_49_50',
    'H_50',
    'H_50_1',
    'H_50_10',
    'H_50_2',
    'H_50_20',
    'H_50_3',
    'H_50_30',
    'H_50_4',
    'H_50_40',
    'H_51',
    'H_51_1',
    'H_51_10',
    'H_51_2',
    'H_51_21',
    'H_51_22',
    'H_52',
    'H_52_1',
    'H_52_10',
    'H_52_2',
    'H_52_21',
    'H_52_22',
    'H_52_23',
    'H_52_24',
    'H_52_29',
    'H_53',
    'H_53_1',
    'H_53_10',
    'H_53_2',
    'H_53_20',
    'I',
    'I_55',
    'I_55_1',
    'I_55_10',
    'I_55_2',
    'I_55_20',
    'I_55_3',
    'I_55_30',
    'I_55_9',
    'I_55_90',
    'I_56',
    'I_56_1',
    'I_56_10',
    'I_56_2',
    'I_56_21',
    'I_56_29',
    'I_56_3',
    'I_56_30',
    'J',
    'J_58',
    'J_58_1',
    'J_58_11',
    'J_58_12',
    'J_58_13',
    'J_58_14',
    'J_58_19',
    'J_58_2',
    'J_58_21',
    'J_58_29',
    'J_59',
    'J_59_1',
    'J_59_11',
    'J_59_12',
    'J_59_13',
    'J_59_14',
    'J_59_2',
    'J_59_20',
    'J_60',
    'J_60_1',
    'J_60_10',
    'J_60_2',
    'J_60_20',
    'J_61',
    'J_61_1',
    'J_61_10',
    'J_61_2',
    'J_61_20',
    'J_61_3',
    'J_61_30',
    'J_61_9',
    'J_61_90',
    'J_62',
    'J_62_0',
    'J_62_01',
    'J_62_02',
    'J_62_03',
    'J_62_09',
    'J_63',
    'J_63_1',
    'J_63_11',
    'J_63_12',
    'J_63_9',
    'J_63_91',
    'J_63_99',
    'K',
    'K_64',
    'K_64_1',
    'K_64_11',
    'K_64_19',
    'K_64_2',
    'K_64_20',
    'K_64_3',
    'K_64_30',
    'K_64_9',
    'K_64_91',
    'K_64_92',
    'K_64_99',
    'K_65',
    'K_65_1',
    'K_65_11',
    'K_65_12',
    'K_65_2',
    'K_65_20',
    'K_65_3',
    'K_65_30',
    'K_66',
    'K_66_1',
    'K_66_11',
    'K_66_12',
    'K_66_19',
    'K_66_2',
    'K_66_21',
    'K_66_22',
    'K_66_29',
    'K_66_3',
    'K_66_30',
    'L',
    'L_68',
    'L_68_1',
    'L_68_10',
    'L_68_2',
    'L_68_20',
    'L_68_3',
    'L_68_31',
    'L_68_32',
    'M',
    'M_69',
    'M_69_1',
    'M_69_10',
    'M_69_2',
    'M_69_20',
    'M_70',
    'M_70_1',
    'M_70_10',
    'M_70_2',
    'M_70_21',
    'M_70_22',
    'M_71',
    'M_71_1',
    'M_71_11',
    'M_71_12',
    'M_71_2',
    'M_71_20',
    'M_72',
    'M_72_1',
    'M_72_11',
    'M_72_19',
    'M_72_2',
    'M_72_20',
    'M_73',
    'M_73_1',
    'M_73_11',
    'M_73_12',
    'M_73_2',
    'M_73_20',
    'M_74',
    'M_74_1',
    'M_74_10',
    'M_74_2',
    'M_74_20',
    'M_74_3',
    'M_74_30',
    'M_74_9',
    'M_74_90',
    'M_75',
    'M_75_0',
    'M_75_00',
    'N',
    'N_77',
    'N_77_1',
    'N_77_11',
    'N_77_12',
    'N_77_2',
    'N_77_21',
    'N_77_22',
    'N_77_29',
    'N_77_3',
    'N_77_31',
    'N_77_32',
    'N_77_33',
    'N_77_34',
    'N_77_35',
    'N_77_39',
    'N_77_4',
    'N_77_40',
    'N_78',
    'N_78_1',
    'N_78_10',
    'N_78_2',
    'N_78_20',
    'N_78_3',
    'N_78_30',
    'N_79',
    'N_79_1',
    'N_79_11',
    'N_79_12',
    'N_79_9',
    'N_79_90',
    'N_80',
    'N_80_1',
    'N_80_10',
    'N_80_2',
    'N_80_20',
    'N_80_3',
    'N_80_30',
    'N_81',
    'N_81_1',
    'N_81_10',
    'N_81_2',
    'N_81_21',
    'N_81_22',
    'N_81_29',
    'N_81_3',
    'N_81_30',
    'N_82',
    'N_82_1',
    'N_82_11',
    'N_82_19',
    'N_82_2',
    'N_82_20',
    'N_82_3',
    'N_82_30',
    'N_82_9',
    'N_82_91',
    'N_82_92',
    'N_82_99',
    'O',
    'O_84',
    'O_84_1',
    'O_84_11',
    'O_84_12',
    'O_84_13',
    'O_84_2',
    'O_84_21',
    'O_84_22',
    'O_84_23',
    'O_84_24',
    'O_84_25',
    'O_84_3',
    'O_84_30',
    'P',
    'P_85',
    'P_85_1',
    'P_85_10',
    'P_85_2',
    'P_85_20',
    'P_85_3',
    'P_85_31',
    'P_85_32',
    'P_85_4',
    'P_85_41',
    'P_85_42',
    'P_85_5',
    'P_85_51',
    'P_85_52',
    'P_85_53',
    'P_85_59',
    'P_85_6',
    'P_85_60',
    'Q',
    'Q_86',
    'Q_86_1',
    'Q_86_10',
    'Q_86_2',
    'Q_86_21',
    'Q_86_22',
    'Q_86_23',
    'Q_86_9',
    'Q_86_90',
    'Q_87',
    'Q_87_1',
    'Q_87_10',
    'Q_87_2',
    'Q_87_20',
    'Q_87_3',
    'Q_87_30',
    'Q_87_9',
    'Q_87_90',
    'Q_88',
    'Q_88_1',
    'Q_88_10',
    'Q_88_9',
    'Q_88_91',
    'Q_88_99',
    'R',
    'R_90',
    'R_90_0',
    'R_90_01',
    'R_90_02',
    'R_90_03',
    'R_90_04',
    'R_91',
    'R_91_0',
    'R_91_01',
    'R_91_02',
    'R_91_03',
    'R_91_04',
    'R_92',
    'R_92_0',
    'R_92_00',
    'R_93',
    'R_93_1',
    'R_93_11',
    'R_93_12',
    'R_93_13',
    'R_93_19',
    'R_93_2',
    'R_93_21',
    'R_93_29',
    'S',
    'S_94',
    'S_94_1',
    'S_94_11',
    'S_94_12',
    'S_94_2',
    'S_94_20',
    'S_94_9',
    'S_94_91',
    'S_94_92',
    'S_94_99',
    'S_95',
    'S_95_1',
    'S_95_11',
    'S_95_12',
    'S_95_2',
    'S_95_21',
    'S_95_22',
    'S_95_23',
    'S_95_24',
    'S_95_25',
    'S_95_29',
    'S_96',
    'S_96_0',
    'S_96_01',
    'S_96_02',
    'S_96_03',
    'S_96_04',
    'S_96_09',
    'T',
    'T_97',
    'T_97_0',
    'T_97_00',
    'T_98',
    'T_98_1',
    'T_98_10',
    'T_98_2',
    'T_98_20',
    'U',
    'U_99',
    'U_99_0',
    'U_99_00'
  ];
}
