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

    public static void loadEncounter(String f) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader("./data/" + f));
        String lin;
        while ((lin = r.readLine()) != null) {
            String tlin = lin.trim();
            if (tlin.startsWith("duration")) {
                long etz = Long.parseLong(tlin.split("=")[1]);
                encounter = new Encounter(0);
                if (etz > 50000) {
                    encounter.duration = Math.round(etz / 1000f);
                } else {
                    encounter.duration = (int) etz;
                }
            } else if (tlin.startsWith("noDupedEntities=[") || tlin.startsWith("entities=[")) {
                while (!(tlin = r.readLine().trim()).equals("]")) {
//                    System.out.println(tlin);
                    if (tlin.startsWith("globalDamageByPlayers")) {
                        break;
                    }
                    Entity e = parseEntity(r, tlin.split("=")[0]);
                    if (e == null) continue;
                    encounter.entities.put(e.internalName, e);
                }
            }
            if (tlin.startsWith("globalDamageByPlayers")) {
                encounter.globalDamageByPlayers = Long.parseLong(tlin.split("=")[1]);
            }
            if (tlin.startsWith("globalDamageToPlayers")) {
                encounter.globalDamageToPlayers = Long.parseLong(tlin.split("=")[1]);
            }
        }

    }

    private static Entity parseEntity(BufferedReader r, String ei) throws IOException {
        String tlin;
        String entID = ei, entNm = "";
        Entity ent = null;
        long firstSeen = 0;
        while (!(tlin = r.readLine().trim()).equals("]")) {
            if (ent == null && (tlin.startsWith("abilities") || tlin.startsWith("abilityList") || tlin.startsWith("healingSources"))) {
                parseAbilities(r);
            }
            if (ent != null) {
                if (tlin.startsWith("abilities") || tlin.startsWith("abilityList")) {
                    ent.abilityList = parseAbilities(r);
                } else if (tlin.startsWith("healingSources")) {
                    parseHealingSources(r);
                    //Do nothing with this for now, merge with abilities later
                } else if (tlin.startsWith("petIds")) {
                    parsePets(r);
                    //Do nothing, never do anything (this is to not break new stuff, and support old stuff)
                } else if (tlin.startsWith("dpsOT")) {
                    parseDPSOT(r);
                } else if (tlin.startsWith("hits")) {
                    ent.hits = Integer.parseInt(tlin.split("=")[1]);
                } else if (tlin.startsWith("healingTaken")) {
                    ent.healingTaken = (int) Math.round(Double.parseDouble(tlin.split("=")[1]));
                } else if (tlin.startsWith("damageTaken")) {
                    ent.damageTaken = (int) Math.round(Double.parseDouble(tlin.split("=")[1]));
                } else if (tlin.startsWith("shieldTaken")) {
                    ent.shieldTaken = (int) Math.round(Double.parseDouble(tlin.split("=")[1]));
                } else if (tlin.startsWith("kills")) {
                    ent.kills = (int) Math.round(Double.parseDouble(tlin.split("=")[1]));
                } else if (tlin.startsWith("damageDealt")) {
                    ent.damageDealt = (int) Math.round(Double.parseDouble(tlin.split("=")[1]));
                } else if (tlin.startsWith("lifetime")) {
                    ent.lifetime = (int) Long.parseLong(tlin.split("=")[1]);
                } else if (tlin.startsWith("firstSeen")) {
                    firstSeen = Long.parseLong(tlin.split("=")[1]);
                } else if (tlin.startsWith("lastSeen")) {
                    if (firstSeen != 0)
                        ent.lifetime = (int) Math.round((Long.parseLong(tlin.split("=")[1]) - firstSeen) / 1000D);
                } else if (tlin.startsWith("internalName")) {
                    ent.internalName = tlin.split("=")[1];
                } else if (tlin.startsWith("ownerEntity")) {
                    if (tlin.split("=").length > 1) {
                        String oe = tlin.split("=")[1];
                        if (!oe.equals("null")) {
                            ent.ownerEntityID = oe;
                        }
                    }
                } else if (tlin.startsWith("isPlayer")) {
                    ent.isPlayer = Boolean.parseBoolean(tlin.split("=")[1]);
                } else if (tlin.startsWith("deaths")) {
                    ent.deaths = Integer.parseInt(tlin.split("=")[1]);
                }
            } else {
                if (!entID.isEmpty()) {
                    if (tlin.startsWith("name")) {
                        entNm = tlin.split("=")[1];
                        continue;
                    }
                    if (!entNm.isEmpty()) {
                        if (tlin.startsWith("ID=") || tlin.startsWith("fullID")) {
                            entID = tlin.split("=")[1];
                            ent = encounter.getOrAddEntity(entNm, entID, 0);
                        }
                    }
                } else if (entID.isEmpty() && tlin.endsWith("=[")) {
                    entID = tlin.split("=")[0];
                }
            }
        }
        return ent;
    }

    private static LinkedList<Ability> parseAbilities(BufferedReader r) throws IOException {
        String nlin;
        LinkedList<Ability> abilities = new LinkedList<>();
        Ability ability;
//        System.out.println("PARSING ABILS");
//        System.out.println(r.readLine());
        while (!(nlin = r.readLine().trim()).equals("]")) {
//            System.out.println("ABILS LOOP = " + nlin);
            if (nlin.endsWith("[")) {
                ability = new Ability();
                while (!(nlin = r.readLine().trim()).equals("]")) {
                    if (nlin.startsWith("name")) {
                        ability.name = nlin.split("=")[1];
                        continue;
                    }
                    if (nlin.startsWith("ID")) {
                        ability.ID = nlin.split("=")[1];
                        continue;
                    }
                    if (nlin.startsWith("baseDamage") || nlin.startsWith("baseMagnitude")) {
                        ability.baseMagnitude = (int) Math.round(Double.parseDouble(nlin.split("=")[1]));
                        continue;
                    }
                    if (nlin.startsWith("Damage") || nlin.startsWith("totalDamage")) {
                        ability.totalDamage = (int) Math.round(Double.parseDouble(nlin.split("=")[1]));
                        continue;
                    }
                    if (nlin.startsWith("hits")) {
                        ability.hits = Integer.parseInt(nlin.split("=")[1]);
                        continue;
                    }
                    if (nlin.startsWith("baseHealingMagnitude")) {
                        ability.baseHealingMagnitude = Integer.parseInt(nlin.split("=")[1]);
                        continue;
                    }
                    if (nlin.startsWith("totalHealing")) {
                        ability.totalHealing = Integer.parseInt(nlin.split("=")[1]);
                        continue;
                    }
                    if (nlin.startsWith("taken")) {
                        ability.taken = Integer.parseInt(nlin.split("=")[1]);
                        continue;
                    }
                    if (nlin.startsWith("type")) {
                        if (nlin.split("=").length > 1) {
                            ability.type = nlin.split("=")[1];
                            continue;
                        }
                    }
                    abilities.add(ability);
                }
            }
        }
        return abilities;
    }

    private static LinkedList<Integer> parseDPSOT(BufferedReader r) throws IOException {
        LinkedList<Integer> dps = new LinkedList<>();
        String nlin;
        while (!(nlin = r.readLine().trim()).equals("]")) {
            dps.add(Integer.parseInt(nlin));
        }
        return dps;
    }

    private static List<String> parsePets(BufferedReader r) throws IOException {
        List<String> petIds = new ArrayList<>();
        String nlin;
        while (!(nlin = r.readLine().trim()).equals("]")) {
            petIds.add(nlin);
        }
        return petIds;
    }

    private static String parseEntDeaths(BufferedReader r) throws IOException {
        String nlin;
        while (!(nlin = r.readLine().trim()).equals("]")) {
            String eid = nlin.split("=")[0];
            long deaths = Long.parseLong(nlin.split("=")[1]);
//            encounter.entityDeaths.put(eid, deaths);
        }
        return nlin;
    }

    private static HashMap<String, Ability> parseHealingSources(BufferedReader r) throws IOException {
        String nlin;
        HashMap<String, Ability> abilities = new HashMap<>();
        Ability ability;
        while (!(nlin = r.readLine().trim()).equals("]")) {
            if (nlin.endsWith("=[")) {
                ability = new Ability();
                while (!(nlin = r.readLine().trim()).equals("]")) {
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
                }
                abilities.put(ability.ID, ability);
            }
        }

        return abilities;
    }
}
