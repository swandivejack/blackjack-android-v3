## Data Definition Language (DDL) for data model

```sql
CREATE TABLE IF NOT EXISTS `Card`
(
    `card_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `created` INTEGER                           NOT NULL,
    `hand_id` INTEGER                           NOT NULL,
    `rank`    TEXT                              NOT NULL,
    `suit`    TEXT                              NOT NULL,
    FOREIGN KEY (`hand_id`) REFERENCES `Hand` (`hand_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS `index_Card_created` ON `Card` (`created`);

CREATE INDEX IF NOT EXISTS `index_Card_hand_id` ON `Card` (`hand_id`);

CREATE TABLE IF NOT EXISTS `Hand`
(
    `hand_id`  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `created`  INTEGER                           NOT NULL,
    `updated`  INTEGER                           NOT NULL,
    `dealer`   INTEGER                           NOT NULL,
    `round_id` INTEGER                           NOT NULL,
    `wager`    INTEGER                           NOT NULL,
    `outcome`  TEXT,
    FOREIGN KEY (`round_id`) REFERENCES `Round` (`round_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS `index_Hand_created` ON `Hand` (`created`);

CREATE INDEX IF NOT EXISTS `index_Hand_updated` ON `Hand` (`updated`);

CREATE INDEX IF NOT EXISTS `index_Hand_round_id` ON `Hand` (`round_id`);

CREATE INDEX IF NOT EXISTS `index_Hand_outcome` ON `Hand` (`outcome`);

CREATE TABLE IF NOT EXISTS `Round`
(
    `round_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `created`  INTEGER                           NOT NULL,
    `shoe_id`  INTEGER                           NOT NULL,
    FOREIGN KEY (`shoe_id`) REFERENCES `Shoe` (`shoe_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS `index_Round_created` ON `Round` (`created`);

CREATE INDEX IF NOT EXISTS `index_Round_shoe_id` ON `Round` (`shoe_id`);

CREATE TABLE IF NOT EXISTS `Shoe`
(
    `shoe_id`       INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `created`       INTEGER                           NOT NULL,
    `updated`       INTEGER                           NOT NULL,
    `shuffle_point` INTEGER                           NOT NULL,
    `shoe_key`      TEXT                              NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_Shoe_shoe_key` ON `Shoe` (`shoe_key`);

CREATE INDEX IF NOT EXISTS `index_Shoe_created` ON `Shoe` (`created`);

CREATE INDEX IF NOT EXISTS `index_Shoe_updated` ON `Shoe` (`updated`);
```

[`ddl.sql`](ddl.sql)