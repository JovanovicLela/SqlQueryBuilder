package checker;

import gui.Query;

import java.util.HashMap;
import java.util.List;

public class Checker {

    DescriptionRepository descriptionRepository;
    Rules rules;
    private HashMap<String, List<String>> desc;

    public Checker(){
        this.rules= new MySqlRules();
        this.descriptionRepository = new DescriptionRepository();
        this.desc = new HashMap<>();
    }
    public boolean check(Query query)  {
        try {

            this.desc = rules.checkAllRules(query);

            if(this.desc.isEmpty())
                return true;
            else
                descriptionRepository.add(this.desc);
            descriptionRepository.add(rules.checkAllRules(query));
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


}
