CREATE TABLE project_call_strategy
(
    programme_strategy ENUM (
        'EUStrategyAdriaticIonianRegion',
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
        'AtlanticStrategy'
        )                      NOT NULL,
    call_id            INTEGER NOT NULL,
    CONSTRAINT fk_project_call_strategy_to_programme_strategy
        FOREIGN KEY (programme_strategy)
            REFERENCES programme_strategy (strategy)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_strategy_to_call
        FOREIGN KEY (call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT pk_project_call_strategy PRIMARY KEY (programme_strategy, call_id)
);
