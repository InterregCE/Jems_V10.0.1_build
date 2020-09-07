CREATE TABLE programme_strategy
(
    strategy     ENUM('EUStrategyAdriaticIonianRegion',
    'EUStrategyAlpineRegion',
    'EUStrategyBalticSeaRegion',
    'EUStrategyDanubeRegion',
    'SeaBasinStrategyNorthSea',
    'SeaBasinStrategyBlackSea',
    'SeaBasinStrategyBalticSea',
    'SeaBasinStrategyArcticOcean',
    'SeaBasinStrategyOutermostRegions',
    'SeaBasinStrategyAdriaticIonianSea',
    'MediterraneanSeaBasin',
    'AtlanticStrategy') PRIMARY KEY,
    active       BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO programme_strategy (strategy)
VALUES ('EUStrategyAdriaticIonianRegion'),
       ('EUStrategyAlpineRegion'),
       ('EUStrategyBalticSeaRegion'),
       ('EUStrategyDanubeRegion'),
       ('SeaBasinStrategyNorthSea'),
       ('SeaBasinStrategyBlackSea'),
       ('SeaBasinStrategyBalticSea'),
       ('SeaBasinStrategyArcticOcean'),
       ('SeaBasinStrategyOutermostRegions'),
       ('SeaBasinStrategyAdriaticIonianSea'),
       ('MediterraneanSeaBasin'),
       ('AtlanticStrategy');
