package io.icker.factions.database;

import io.icker.factions.FactionsMod;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection con;

    public static void connect() {
        try {
            con = DriverManager.getConnection("jdbc:h2:./factions/factions");
            new Query("""
                    CREATE TABLE IF NOT EXISTS Faction (
                        name VARCHAR(255) PRIMARY KEY,
                        description VARCHAR(255),
                        color VARCHAR(255),
                        open BOOLEAN,
                        power INTEGER
                    );

                    CREATE TABLE IF NOT EXISTS Member (
                        uuid UUID PRIMARY KEY,
                        faction VARCHAR(255),
                        rank ENUM('owner', 'co_owner', 'officer', 'civilian'),
                        FOREIGN KEY(faction) REFERENCES Faction(name) ON DELETE CASCADE
                    );

                    CREATE TABLE IF NOT EXISTS Claim (
                        x INTEGER,
                        z INTEGER,
                        level VARCHAR(255),
                        faction VARCHAR(255),
                        PRIMARY KEY(x, z, level),
                        FOREIGN KEY(faction) REFERENCES Faction(name) ON DELETE CASCADE
                    );
                                    
                    CREATE TABLE IF NOT EXISTS Invite (
                        player UUID,
                        faction VARCHAR(255),
                        PRIMARY KEY (player, faction),
                        FOREIGN KEY(faction) REFERENCES Faction(name) ON DELETE CASCADE
                    );

                    CREATE TABLE IF NOT EXISTS Home (
                        faction VARCHAR(255),
                        x DOUBLE,
                        y DOUBLE,
                        z DOUBLE,
                        yaw REAL,
                        pitch REAL,
                        level VARCHAR(255),
                        FOREIGN KEY(faction) REFERENCES Faction(name) ON DELETE CASCADE
                    );

                    CREATE TABLE IF NOT EXISTS PlayerConfig (
                        uuid UUID PRIMARY KEY,
                        chat VARCHAR(255),
                        bypass BOOLEAN,
                        zone BOOLEAN
                    );
                                    
                    CREATE TABLE IF NOT EXISTS Allies (
                        source VARCHAR(255),
                        target VARCHAR(255),
                        accept BOOL,
                        FOREIGN KEY(source) REFERENCES Faction(name) ON DELETE CASCADE,
                        FOREIGN KEY(target) REFERENCES Faction(name) ON DELETE CASCADE
                    );
                    """)
                .executeUpdate();

            Query query = new Query("SELECT EXISTS(SELECT * FROM INFORMATION_SCHEMA.columns WHERE table_name = 'PLAYERCONFIG' and column_name = 'ZONE');")
                .executeQuery();
            
            if (!query.exists()) {
                new Query("""
                        ALTER TABLE PlayerConfig ADD zone BOOLEAN;
                        UPDATE PlayerConfig SET zone = 0;
                        """)
                    .executeUpdate();
                
                FactionsMod.LOGGER.info("Successfully migrated to newer version");
            }
            FactionsMod.LOGGER.info("Successfully connected to database");
        } catch (SQLException e) {
            e.printStackTrace();    
            FactionsMod.LOGGER.error("Error connecting to and setting up database");
        }
    }

    public static void disconnect() {
        try {
            con.close();
            FactionsMod.LOGGER.info("Successfully disconnected from database");
        } catch (SQLException e) {
            FactionsMod.LOGGER.error("Error disconnecting from database");
        }
    }
}
