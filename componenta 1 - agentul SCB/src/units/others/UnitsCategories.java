package units.others;

import com.springrts.ai.oo.clb.UnitDef;
import logger.AILogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class UnitsCategories {

    private AILogger logger;
    private Map<String, String[]> unitsCategories = new HashMap<>();

    private String aiVersion = "0.1";
    private String aiName = "SCB";


    public UnitsCategories(AILogger logger) {
        this.logger = logger;
        try {
            Scanner scanner = new Scanner(new File("./AI/Skirmish/" + aiName + "/" + aiVersion + "/info/units-categories.info"));
            while (scanner.hasNextLine()) {
                String[] currentUnitSplit = scanner.nextLine().split(":");
                String[] currentCategoriesSplit = currentUnitSplit[1].split(" ");
                unitsCategories.put(currentUnitSplit[0], currentCategoriesSplit);
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /*
        Verifica daca o unitate face parte din categoriile din "categories" (daca nu face parte dintr-una atunci intoarce false)
     */
    public boolean unitContainsCategories(String unit, String[] categories) {
        String[] unitCats = unitsCategories.get(unit);
        if (categories.length > unitCats.length) {
            return false;
        }

        for (String cat : categories) {
            boolean found = false;
            for (String unitCat : unitCats) {
                if (cat.equals(unitCat)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /*
        Verifica daca o unitate face parte din categoria "category"
     */
    public boolean unitContainsCategory(String unit, String category) {
        String[] unitCats = unitsCategories.get(unit);

        for (String unitCat : unitCats) {
            if (category.equals(unitCat)) {
                return true;
            }
        }

        return false;
    }

    /*
        Verifica daca este cladire sau tower (constructie in general)
     */
    public boolean isBuilding(String unitName) {
        return unitContainsCategory(unitName, "BUILDING");
    }

    /*
        Verifica daca este unitate mobila
     */
    public boolean isTroop(String unitName) {
        return !(isBuilding(unitName));
    }

    /*
        Intoarce o lista cu UnitDef-urile unitatilor care pot construi "unitName" (chiar si cele nefolosite de AI)
    */
    public List<UnitDef> getBuildersFor(String unitName) {
        List<UnitDef> buildersDefs = new LinkedList<>();
        for (UnitDef unitDef : logger.getCallback().getUnitDefs()) {
            List<UnitDef> buildOptions = unitDef.getBuildOptions();
            if (buildOptions.contains(logger.getCallback().getUnitDefByName(unitName))) {
                buildersDefs.add(unitDef);
            }
        }

        if (buildersDefs.size() == 0) {
            return null;
        }
        return buildersDefs;
    }


    public boolean isLand(String unitName) {
        return unitContainsCategory(unitName, "LAND");
    }

    public boolean isAir(String unitName) {
        return unitContainsCategory(unitName, "AIR");
    }

    public boolean isSea(String unitName) {
        return unitContainsCategory(unitName, "SEA");
    }


}