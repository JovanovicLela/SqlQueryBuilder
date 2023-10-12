package checker;

import gui.MainFrame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptionRepository {


    public DescriptionRepository(){

    }
    public void add(HashMap<String, List<String>> stackTrace) throws Exception {
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader("src/main/java/res/stackTrace.json");

        Object obj = jsonParser.parse(fileReader);

        JSONObject jsonObject =  (JSONObject) obj;

        JSONArray rules  = (JSONArray) jsonObject.get("Array");

        StringBuilder stackOfRules = new StringBuilder();

        /// ime greske , lista gresaka
        for (Map.Entry<String, List<String>> set : stackTrace.entrySet()) {
            for (int i = 0; i < rules.size(); i++) {
                JSONObject rule = (JSONObject) rules.get(i);

                if (set.getKey().equals(rule.get("name"))) {

                    stackOfRules.append(set.getKey());
                    stackOfRules.append("\n");
                    stackOfRules.append(" ");

                    String desc = (String) rule.get("description");

                    stackOfRules.append(returnString(set, desc));

                    stackOfRules.append("\n");
                    String sugg = (String) rule.get("suggestions");

                    stackOfRules.append(returnString(set, sugg));
                    System.out.println(sugg);
                    stackOfRules.append(";");
                    stackOfRules.append("\n");
                }
            }
        }

        // System.out.println(stackOfRules.toString());
        MainFrame.getInstance().getJtpTextArea().setText(stackOfRules.toString());

        //  System.out.println(stackTrace.get("name"));
    }

    public String returnString(Map.Entry<String, List<String>> set, String string){
        String string1 = "";

        int s;
        for (s = 0; s < set.getValue().size() - 1; s++) {
            string1 += set.getValue().get(s) + "," + "\n";
        }
        string1 += set.getValue().get(s);

        if (string.contains("%s")) {
            string = string.replace("%s", string1);
        }

        return string;
    }
}

