package io.icker.factions.database;

import java.util.ArrayList;
import io.icker.factions.FactionsMod;

public class Ally {
  public String target;
  public String source;

  public static Ally get(String target, String source) {
    Query query = new Query("SELECT * FROM Ally WHERE (source = ? AND target = ?) OR (source = ? AND target = ?);")
        .set(source, target, target, source)
        .executeQuery();

    if (!query.success)
      return null;
    return new Ally(query.getString("source"), query.getString("target"));
  }

  public static Ally add(String source, String target) {
    Query query = new Query("INSERT INTO Allies (source, target, accept) VALUES (?, ?, 0);")
        .set(source, target)
        .executeUpdate();

    if (!query.success)
      return null;
    return new Ally(source, target);
  }

  public static Ally accept(String source, String target) {
    Faction oldSource = Faction.get(source);
    Faction oldTarget = Faction.get(target);

    Query query = new Query("UPDATE Allies SET accept = 1 WHERE source = ? AND target = ?;")
        .set(source, target)
        .executeUpdate();

    if (!query.success) return null;

    if (FactionsMod.dynmapEnabled) {
      FactionsMod.dynmap.updateFaction(oldSource, Faction.get(source));
      FactionsMod.dynmap.updateFaction(oldTarget, Faction.get(target));
    }

    return new Ally(source, target);
  }

  public Ally(String source, String target) {
    this.source = source;
    this.target = target;
  }

  public void remove() {
    Ally.remove(source, target);
  }

  public static void remove(String source, String target) {
    Faction oldSource = Faction.get(source);
    Faction oldTarget = Faction.get(target);

    new Query("DELETE FROM Allies WHERE (source = ? AND target = ?) OR (source = ? AND target = ?);")
        .set(source, target, target, source)
        .executeUpdate();

    if (FactionsMod.dynmapEnabled) {
      FactionsMod.dynmap.updateFaction(oldSource, Faction.get(source));
      FactionsMod.dynmap.updateFaction(oldTarget, Faction.get(target));
    }
  }

  public static boolean checkIfAlly(String source, String target) {
    Query query = new Query("SELECT EXISTS(SELECT * FROM Allies WHERE ((source = ? AND target = ?) OR (source = ? AND target = ?)) AND accept = 1);")
        .set(source, target, target, source)
        .executeQuery();

    return query.exists();
  }

  public static boolean checkIfAllyInvite(String source, String target) {
    Query query = new Query("SELECT EXISTS(SELECT * FROM Allies WHERE ((source = ? AND target = ?) OR (source = ? AND target = ?)) AND accept = 0);")
        .set(source, target, target, source)
        .executeQuery();

    return query.exists();
  }

  public static ArrayList<Ally> getAllies(String source) {
    Query query = new Query("SELECT * FROM Allies WHERE (source = ? OR target = ?) AND accept = 1;")
        .set(source, source)
        .executeQuery();

    ArrayList<Ally> allies = new ArrayList<Ally>();
    if (!query.success)
      return allies;

    while (query.next()) {
      allies.add(new Ally(query.getString("source"), query.getString("target")));
    }
    return allies;
  }

  public static ArrayList<Ally> getAllyInvites(String source) {
    Query query = new Query("SELECT * FROM Allies WHERE target = ? AND accept = 0;")
        .set(source)
        .executeQuery();

    ArrayList<Ally> allies = new ArrayList<Ally>();
    if (!query.success)
      return allies;

    while (query.next()) {
      allies.add(new Ally(query.getString("source"), query.getString("target")));
    }
    return allies;
  }

  public static ArrayList<Ally> getSentInvites(String source) {
    Query query = new Query("SELECT * FROM Allies WHERE source = ? AND accept = 0;")
        .set(source)
        .executeQuery();

    ArrayList<Ally> allies = new ArrayList<Ally>();
    if (!query.success)
      return allies;

    while (query.next()) {
      allies.add(new Ally(query.getString("source"), query.getString("target")));
    }
    return allies;
  }

  public static ArrayList<Ally> getAll(String source) {
    Query query = new Query("SELECT * FROM Allies WHERE target = ? OR source = ?;")
        .set(source, source)
        .executeQuery();

    ArrayList<Ally> allies = new ArrayList<Ally>();
    if (!query.success)
      return allies;

    while (query.next()) {
      allies.add(new Ally(query.getString("source"), query.getString("target")));
    }
    return allies;
  }
}