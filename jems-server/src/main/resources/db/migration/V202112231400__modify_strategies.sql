UPDATE IGNORE project_call_strategy pcs
SET pcs.programme_strategy = 'EUStrategyAdriaticIonianRegion'
WHERE pcs.programme_strategy = 'SeaBasinStrategyAdriaticIonianSea';

UPDATE project_description_c2_relevance_strategy pdc2rs
SET pdc2rs.strategy = 'EUStrategyAdriaticIonianRegion'
WHERE pdc2rs.strategy = 'SeaBasinStrategyAdriaticIonianSea';

DELETE FROM project_call_strategy WHERE programme_strategy = 'SeaBasinStrategyAdriaticIonianSea';

UPDATE IGNORE project_call_strategy pcs
SET pcs.programme_strategy = 'EUStrategyBalticSeaRegion'
WHERE pcs.programme_strategy = 'SeaBasinStrategyBalticSea';

UPDATE project_description_c2_relevance_strategy pdc2rs
SET pdc2rs.strategy = 'EUStrategyBalticSeaRegion'
WHERE pdc2rs.strategy = 'SeaBasinStrategyBalticSea';

DELETE FROM project_call_strategy WHERE programme_strategy = 'SeaBasinStrategyBalticSea';

UPDATE programme_strategy
SET active = (
    SELECT MAX(active) FROM programme_strategy
    WHERE strategy = 'EUStrategyAdriaticIonianRegion' OR strategy = 'SeaBasinStrategyAdriaticIonianSea')
WHERE strategy = 'EUStrategyAdriaticIonianRegion';


UPDATE programme_strategy
SET active = (
    SELECT MAX(active) FROM programme_strategy
    WHERE strategy = 'EUStrategyBalticSeaRegion' OR strategy = 'SeaBasinStrategyBalticSea')
WHERE strategy = 'EUStrategyBalticSeaRegion';
