package units.others;

import com.springrts.ai.oo.clb.UnitDef;
import logger.AILogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class UnitsConfig {

    private UnitsCategories unitsCategories;
    private String aiVersion = "0.1";
    private String aiName = "SCB";

    // nume unitate -> lista
    // lista :
    // 0  =  numar unitati pentru un atac
    // 1  =  interval de timp (secunde) ce determina durata intre 2 ordine de train pentru unitatea respectiva
    private java.util.Map<String, List<Integer>> unitsConfigs = new HashMap<>();
    private java.util.Map<String, List<Integer>> unitsConfigsBuildings = new HashMap<>();

    private java.util.Map<String, List<Integer>> settingsConfigs = new HashMap<>();


    private AILogger logger;


    public UnitsConfig(AILogger logger, UnitsCategories unitsCategories) {
        this.logger = logger;
        this.unitsCategories = unitsCategories;
        readConfigFile();
    }

    private void addCorrespondingFactory(String unitName) {
        // Nu dorim sa ia factories de la builders pentru ca oricum antrenamentul se va face cu toti constructorii selectati cu 1
        if (unitsCategories.unitContainsCategory(unitName, "BUILDER")){
            return;
        }

        if (unitsCategories.unitContainsCategories(unitName, new String[]{"T1", "AIR", "PLANE"})) {
            //armap
            unitsConfigs.put("armap", Arrays.asList(1));
            unitsConfigsBuildings.put("armap", Arrays.asList(1));

        } else if (unitsCategories.unitContainsCategories(unitName, new String[]{"T2", "AIR", "PLANE"})) {
            //armap si armaap
            unitsConfigs.put("armap", Arrays.asList(1));
            unitsConfigs.put("armaap", Arrays.asList(1));
            unitsConfigsBuildings.put("armap", Arrays.asList(1));
            unitsConfigsBuildings.put("armaap", Arrays.asList(1));

        } else if (unitsCategories.unitContainsCategories(unitName, new String[]{"T1", "LAND", "KBOT"})) {
            //armlab
            unitsConfigs.put("armlab", Arrays.asList(1));
            unitsConfigsBuildings.put("armlab", Arrays.asList(1));

        } else if (unitsCategories.unitContainsCategories(unitName, new String[]{"T2", "LAND", "KBOT"})) {
            //armlab si armalab
            unitsConfigs.put("armlab", Arrays.asList(1));
            unitsConfigs.put("armalab", Arrays.asList(1));
            unitsConfigsBuildings.put("armlab", Arrays.asList(1));
            unitsConfigsBuildings.put("armalab", Arrays.asList(1));

        } else if (unitsCategories.unitContainsCategories(unitName, new String[]{"T1", "LAND", "VEHICLE"})) {
            //armvp
            unitsConfigs.put("armvp", Arrays.asList(1));
            unitsConfigsBuildings.put("armvp", Arrays.asList(1));

        } else if (unitsCategories.unitContainsCategories(unitName, new String[]{"T2", "LAND", "VEHICLE"})) {
            //armvp si armavp
            unitsConfigs.put("armvp", Arrays.asList(1));
            unitsConfigs.put("armavp", Arrays.asList(1));
            unitsConfigsBuildings.put("armvp", Arrays.asList(1));
            unitsConfigsBuildings.put("armavp", Arrays.asList(1));

        } else if (unitsCategories.unitContainsCategories(unitName, new String[]{"HOVER"})) {
            //armhp
            unitsConfigs.put("armhp", Arrays.asList(1));
            unitsConfigsBuildings.put("armhp", Arrays.asList(1));
        }
    }


    private void readArmyConfig(String[] current) {
        int value = Integer.parseInt(current[1]);
        if (value != 0) {
            String unitName = current[0];
            addCorrespondingFactory(unitName);
            unitsConfigs.put(unitName, Arrays.asList(value));
        }
    }


    private void readSettingsConfig(String[] current) {
        int value = Integer.parseInt(current[1]);
        String settingName = current[0];
        settingsConfigs.put(settingName, Arrays.asList(value));
    }


    private void readConfigFile() {
        boolean settingsConfig = false;

        try {
            Scanner scanner = new Scanner(new File("./AI/Skirmish/" + aiName + "/" + aiVersion + "/config.cfg"));
            while (scanner.hasNextLine()) {
                String nextLineString = scanner.nextLine();

                // Ignora comentariile (liniile cu "---" la inceput)
                if (nextLineString.startsWith("---")) {
                    continue;
                }
                if (nextLineString.startsWith("===")) {

                    if (nextLineString.contains("SETTINGS")) {
                        settingsConfig = true;
                    } else if (nextLineString.contains("ARMY")) {
                        settingsConfig = false;
                    }
                    continue;
                }

                String[] current = nextLineString.split(":");
                if (settingsConfig) {
                    readSettingsConfig(current);
                } else {
                    readArmyConfig(current);
                }

            }
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // Intoarce toate unitatile utilizate
    public List<String> getAllUnits() {
        List<String> unitNames = new LinkedList<>(unitsConfigs.keySet());

        if (unitNames.size() == 0) {
            return null;
        }
        return unitNames;
    }

    public List<UnitDef> getAllUnitsDefs() {
        List<UnitDef> unitDefs = new LinkedList<>();
        for (String unitName : unitsConfigs.keySet()) {
            unitDefs.add(this.logger.getCallback().getUnitDefByName(unitName));
        }

        if (unitDefs.size() == 0) {
            return null;
        }
        return unitDefs;
    }

    public List<String> getAllBuildings() {
        List<String> unitNames = new LinkedList<>(unitsConfigsBuildings.keySet());

        if (unitNames.size() == 0) {
            return null;
        }
        return unitNames;
    }

    public List<UnitDef> getAllBuildingsDefs() {
        List<UnitDef> unitDefs = new LinkedList<>();
        for (String unitName : unitsConfigsBuildings.keySet()) {
            unitDefs.add(this.logger.getCallback().getUnitDefByName(unitName));
        }

        if (unitDefs.size() == 0) {
            return null;
        }
        return unitDefs;
    }


    public boolean containsUnit(String unitName) {
        return unitsConfigs.containsKey(unitName);
    }

    /*
        Intoarce configuratia armatei pentru o unitate (nu doar numarul de unitati pe grup !!!)
     */
    public List<Integer> getArmyConfigForUnit(String unitName) {
        if (unitsConfigs.containsKey(unitName)) {
            return unitsConfigs.get(unitName);
        }
        return null;
    }


    public List<Integer> getSettingValues(String setting){
        return settingsConfigs.get(setting);
    }

    public Integer getFirstSettingValue(String setting){
        return settingsConfigs.get(setting).get(0);
    }

}