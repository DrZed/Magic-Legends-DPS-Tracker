package drzed;

import hxckdms.hxcconfig.Config;

import java.util.LinkedHashMap;

@Config
@SuppressWarnings("WeakerAccess")
public class EntityNames {
    public static LinkedHashMap<String, String> entNameList = new LinkedHashMap<>();

    public EntityNames() { }

    public static String getEntityName(String id) {
        return entNameList.get(id);
    }

    public static String addEntityName(String name, String id) {
        if (name.isEmpty() || name.equalsIgnoreCase("*") || id.isEmpty() || id.equalsIgnoreCase("*")) {
            return "";
        }
//        System.out.println("Pre trim : " + id);
        id = Entity.getID(id);
//        System.out.println("Post Trim : " + id);
        if (!entNameList.containsKey(id)) {
            System.out.println("Adding Entity to list: " + name);
            entNameList.put(id, name);
        }
        return getEntityName(id);
    }
}
