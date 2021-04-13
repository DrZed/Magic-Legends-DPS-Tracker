package drzed.Data.subtype;

import drzed.Data.Ability;
import drzed.Data.Encounter;
import drzed.Data.Entity;
import hxckdms.hxcconfig.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Config
public class EncounterData {
    public static Encounter encounter = new Encounter();

    public EncounterData() { }
//
//    public static void loadEncounter(String f) throws IOException {
//        BufferedReader r = new BufferedReader(new FileReader("./data/" + f));
//        String lin;
//        while ((lin = r.readLine()) != null) {
//            String tlin = lin.trim();
//            if (tlin.startsWith("duration")) {
//                encounter.duration = Integer.parseInt(tlin.split("=")[1]);
//            }else if (tlin.startsWith("entityDeaths=[")) {
//                parseEntDeaths(r);
//            } else if (tlin.startsWith("noDupedEntities=[")) {
//                while (!(tlin = r.readLine().trim()).equals("]")) {
//                    Entity e = parseEntity(r, tlin.split("=")[0]);
//                    if (e == null) continue;
//                    encounter.noDupedEntities.put(e.internalName, e);
//                }
//            }
//        }
//
//    }
//
//    private static Entity parseEntity(BufferedReader r, String ei) throws IOException {
//        String tlin;
//        String entID = ei, entNm = "";
//        Entity ent = null;
//        while (!(tlin = r.readLine().trim()).equals("]")) {
//            if (ent != null) {
//                if (tlin.startsWith("abilities")) {
//                    ent.abilities = parseAbilities(r);
//                } else if (tlin.startsWith("healingSources")) {
//                    ent.healingSources = parseHealingSources(r);
//                } else if (tlin.startsWith("hits")) {
//                    ent.hits = Long.parseLong(tlin.split("=")[1]);
//                } else if (tlin.startsWith("healingTaken")) {
//                    ent.healingTaken = Double.parseDouble(tlin.split("=")[1]);
//                } else if (tlin.startsWith("damageTaken")) {
//                    ent.damageTaken = Double.parseDouble(tlin.split("=")[1]);
//                } else if (tlin.startsWith("damageDealt")) {
//                    ent.damageDealt = Double.parseDouble(tlin.split("=")[1]);
//                } else if (tlin.startsWith("firstSeen")) {
//                    ent.firstSeen = Long.parseLong(tlin.split("=")[1]);
//                } else if (tlin.startsWith("lastSeen")) {
//                    ent.lastSeen = Long.parseLong(tlin.split("=")[1]);
//                } else if (tlin.startsWith("deathTime")) {
//                    ent.deathTime = Long.parseLong(tlin.split("=")[1]);
//                } else if (tlin.startsWith("internalName")) {
//                    ent.internalName = tlin.split("=")[1];
//                } else if (tlin.startsWith("IDNum")) {
//                    ent.IDNum = Long.parseLong(tlin.split("=")[1]);
//                } else if (tlin.startsWith("petIds")) {
//                    ent.petIds = parsePets(r);
//                } else if (tlin.startsWith("ownerEntity")) {
//                    String oe = tlin.split("=")[1];
//                    if (!oe.equals("null")) {
//                        ent.ownerEntityID = oe;
//                    }
//                } else if (tlin.startsWith("isPlayer")) {
//                    ent.isPlayer = Boolean.parseBoolean(tlin.split("=")[1]);
//                } else if (tlin.startsWith("dpsOT")) {
//                    ent.dpsOT = parseDPSOT(r);
//                } else if (tlin.startsWith("deaths")) {
//                    ent.deaths = Integer.parseInt(tlin.split("=")[1]);
//                } else if (tlin.startsWith("DOTT")) {
//                    ent.DOTT = Long.parseLong(tlin.split("=")[1]);
//                } else if (tlin.startsWith("lDOTT")) {
//                    ent.lDOTT = Long.parseLong(tlin.split("=")[1]);
//                } else if (tlin.startsWith("DOT")) {
//                    ent.DOT = Double.parseDouble(tlin.split("=")[1]);
//                }
//            } else {
//                if (!entID.isEmpty()) {
//                    if (tlin.startsWith("name")) {
//                        entNm = tlin.split("=")[1];
//                        continue;
//                    }
//                    if (!entNm.isEmpty()) {
//                        if (tlin.startsWith("ID")) {
//                            entID = tlin.split("=")[1];
//                            ent = encounter.getOrAddEntity(entNm, entID, 0);
//                        }
//                    }
//                } else if (entID.isEmpty() && tlin.endsWith("=[")) {
//                    entID = tlin.split("=")[0];
//                }
//            }
//        }
//        return ent;
//    }
//
//    private static LinkedList<Integer> parseDPSOT(BufferedReader r) throws IOException {
//        LinkedList<Integer> dps = new LinkedList<>();
//        String nlin;
//        while (!(nlin = r.readLine().trim()).equals("]")) {
//            dps.add(Integer.parseInt(nlin));
//        }
//        return dps;
//    }
//
//    private static List<String> parsePets(BufferedReader r) throws IOException {
//        List<String> petIds = new ArrayList<>();
//        String nlin;
//        while (!(nlin = r.readLine().trim()).equals("]")) {
//            petIds.add(nlin);
//        }
//        return petIds;
//    }
//
//    private static String parseEntDeaths(BufferedReader r) throws IOException {
//        String nlin;
//        while (!(nlin = r.readLine().trim()).equals("]")) {
//            String eid = nlin.split("=")[0];
//            long deaths = Long.parseLong(nlin.split("=")[1]);
//            encounter.entityDeaths.put(eid, deaths);
//        }
//        return nlin;
//    }
//
//    private static HashMap<String, Ability> parseAbilities(BufferedReader r) throws IOException {
//        String nlin;
//        HashMap<String, Ability> abilities = new HashMap<>();
//        Ability ability;
//        while (!(nlin = r.readLine().trim()).equals("]")) {
//            if (nlin.endsWith("=[")) {
//                ability = new Ability();
//                while (!(nlin = r.readLine().trim()).equals("]")) {
//                    if (nlin.startsWith("name")) {
//                        ability.name = nlin.split("=")[1];
//                        continue;
//                    }
//                    if (nlin.startsWith("ID")) {
//                        ability.ID = nlin.split("=")[1];
//                        continue;
//                    }
//                    if (nlin.startsWith("baseDamage")) {
//                        ability.baseMagnitude = Double.parseDouble(nlin.split("=")[1]);
//                        continue;
//                    }
//                    if (nlin.startsWith("Damage")) {
//                        ability.totalDamage = Double.parseDouble(nlin.split("=")[1]);
//                        continue;
//                    }
//                    if (nlin.startsWith("hits")) {
//                        ability.hits = Integer.parseInt(nlin.split("=")[1]);
//                        continue;
//                    }
//                }
//                abilities.put(ability.ID, ability);
//            }
//        }
//
//        return abilities;
//    }
//    private static HashMap<String, Ability> parseHealingSources(BufferedReader r) throws IOException {
//        String nlin;
//        HashMap<String, Ability> abilities = new HashMap<>();
//        Ability ability;
//        while (!(nlin = r.readLine().trim()).equals("]")) {
//            if (nlin.endsWith("=[")) {
//                ability = new Ability();
//                while (!(nlin = r.readLine().trim()).equals("]")) {
//                    if (nlin.startsWith("name")) {
//                        ability.name = nlin.split("=")[1];
//                        continue;
//                    }
//                    if (nlin.startsWith("ID")) {
//                        ability.ID = nlin.split("=")[1];
//                        continue;
//                    }
//                    if (nlin.startsWith("baseDamage")) {
//                        ability.baseMagnitude = Double.parseDouble(nlin.split("=")[1]);
//                        continue;
//                    }
//                    if (nlin.startsWith("Damage")) {
//                        ability.totalDamage = Double.parseDouble(nlin.split("=")[1]);
//                        continue;
//                    }
//                    if (nlin.startsWith("hits")) {
//                        ability.hits = Integer.parseInt(nlin.split("=")[1]);
//                        continue;
//                    }
//                }
//                abilities.put(ability.ID, ability);
//            }
//        }
//
//        return abilities;
//    }
}
