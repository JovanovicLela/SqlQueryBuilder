package checker;

import gui.MainFrame;
import gui.Query;
import resource.DBNode;
import resource.enums.ConstraintType;
import resource.implementation.Attribute;
import resource.implementation.AttributeConstraint;
import resource.implementation.Entity;
import resource.implementation.InformationResource;

import java.util.*;

public class MySqlRules implements Rules {

    @Override
    public HashMap<String, List<String>> checkAllRules(Query query) {
        HashMap<String, List<String>> stackTrace = new HashMap<>();

        List<String> missedElements = this.ifEverythingNecessaryMissed(query);
        if (!missedElements.isEmpty())
            stackTrace.put("MissingQueries", missedElements);
        else {
            List<String> notExistingTables = this.ifTablesExists(query);
            if (!notExistingTables.isEmpty())
                stackTrace.put("NoSuchTable", notExistingTables);
            List<String> notExistingColumns = this.ifColumnsExists(query);
            if (!notExistingColumns.isEmpty())
                stackTrace.put("NoSuchColumns", notExistingColumns);
            List<String> order = this.orderOfTheStatement(query);
            if (!order.isEmpty())
                stackTrace.put("InvalidOrder", order);
            List<String> alias = this.isAliasQuotationMarksValid(query);
            if (!alias.isEmpty())
                stackTrace.put("SyntaxError", alias);
            List<String> aggregationWithGroupBy = this.isAggregationWithoutGroupBy(query);
            if (!aggregationWithGroupBy.isEmpty())
                stackTrace.put("GroupByError", aggregationWithGroupBy);
            List<String> aggregationWithoutWith = this.WhereIsWithoutFunctionOfAggregation(query);
            if (!aggregationWithoutWith.isEmpty())
                stackTrace.put("WhereWithAggregation", aggregationWithoutWith);
            List<String> joinSuitable = this.isColumnSuitableForJoin(query);
            if (!joinSuitable.isEmpty())
                stackTrace.put("JoinNotValid", joinSuitable);

        }

        this.orderOfTheStatement(query);
        return stackTrace;
    }

    @Override
    public List<String> ifTablesExists(Query query) {
        List<String> lista = new ArrayList<>();


        InformationResource informationResource = MainFrame.getInstance().getAppCore().getIr();
        if (query.getTableName() != null) {
            if (query.getTableName().contains("dual")) {
                //lista.add("none");
                return lista;
            }
        }
        String tableNameJoin = " ";
//        if(query.toString().contains("join")){
//            tableNameJoin = query.getMapByKeyWords().get(MainWords.JOIN).get(0);
//
//        }

        int flag = 0;
        if (!query.getTableName().isEmpty()) {
            String tableName = query.getTableName();

            for (int i = 0; i < informationResource.getChildren().size(); i++) {
                if (tableName.equals(informationResource.getChildren().get(i).getName())) {
                    return lista;
                }
            }
            if (flag == 0) {
                lista.add(tableName);
            } else {
                flag = 0;
            }
        }
        return lista;
    }

    @Override
    public List<String> ifColumnsExists(Query query) {
        List<String> list = new ArrayList<>();
        InformationResource informationResource = MainFrame.getInstance().getAppCore().getIr();
        Entity entity;
        if (this.ifTablesExists(query).isEmpty()) {
            entity = (Entity) informationResource.getChildByName(query.getTableName());

        } else {
            entity = (Entity) informationResource.getChildByName("regions");
        }

        for (Map.Entry<MainWords, List<String>> set : query.getMapByKeyWords().entrySet()) {
            if (validMainWords(set.getKey())) {
                int flag = 0;
                for (String attributesFromQuery : set.getValue()) {
                    flag = 0;
                    for (DBNode attributeFromDatabase : entity.getChildren()) {
                        if (attributesFromQuery.contains(attributeFromDatabase.getName())) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        list.add(attributesFromQuery);
                    }
                }
            }
        }
        if (!list.isEmpty()) {
            if (query.toString().toLowerCase(Locale.ROOT).contains("select") && list.get(0).equals("*")) {
                list.clear();
            }
        }
        return list;
    }

    public boolean validMainWords(MainWords mW) {
        //za ispitivanje kolona
        if (mW.equals(MainWords.SELECT) || mW.equals(MainWords.INSERT_INTO)) {
            return true;
        }
        if (mW.equals(MainWords.SET) || mW.equals(MainWords.WHERE)) {
            return true;
        }

        return false;
    }

    @Override
    public List<String> isColumnSuitableForJoin(Query query) {
        List<String> list = new ArrayList<>();

        if (query.getMapByKeyWords().get(MainWords.JOIN) != null) {

            String tableToJoin = query.getMapByKeyWords().get(MainWords.JOIN).get(0);
            String usingKey = query.getMapByKeyWords().get(MainWords.USING).get(0);

            InformationResource informationResource = MainFrame.getInstance().getAppCore().getIr();
            Entity entity = (Entity) informationResource.getChildByName(tableToJoin);
            Attribute attribute = (Attribute) entity.getChildByName(usingKey);

            System.out.println("OVo je atribut " + attribute.getName());

            if (attribute != null) {
                for (DBNode attrDbNode : attribute.getChildren()) {
                    if (!((AttributeConstraint) attrDbNode).getConstraintType().equals(ConstraintType.PRIMARY_KEY)) {
                        list.add(usingKey + " is not a foreign key of table " + tableToJoin);
                    }
                }
            } else {
                list.add(usingKey + " doesn't exist");
            }

            System.out.println(list);
        } else {
            return list;
        }
        return list;
}

    @Override
    public List<String> ifEverythingNecessaryMissed(Query query) {
        List<String> list = new ArrayList<>();

        for (MainWords mainWords : query.getContentOfQuery()) {
            System.out.println("ovo" + mainWords);
            switch (mainWords) {
                case SELECT: {
                    System.out.println(query.getMapByKeyWords().get(MainWords.SELECT));
                    if (query.getMapByKeyWords().get(MainWords.SELECT).isEmpty()) {
                        list.add("columns are not specified in select");
                    }
                    if (query.getMapByKeyWords().get(MainWords.FROM) == null) {
                        list.add("select missing FROM");
                    }
                } break;
                case FROM: {
                    if (query.getTableName() == null) {
                        list.add("tables are not specified in FROM");
                    }
                } break;
                // ne radi
                case INSERT_INTO: {
                    if(query.tableNames.equals(""))
                        list.add("table is not specified in INSERT INTO");
                } break;
                case VALUES:{
                    // if(query.getMapByKeyWords().get(MainWords.VALUES).get(0).contains("")){
                    //    list.add("values are not specified in VALUES");
                    //  }
                }
            }
        }
        return list;
    }

    @Override
    public List<String> isAliasQuotationMarksValid(Query query) {
        List<String> lista = new ArrayList<>();
        if(query.calling.equals("select")){
            for(String columns : query.getMapByKeyWords().get(MainWords.SELECT)){
                System.out.println(columns);
                if(columns.toLowerCase(Locale.ROOT).contains("as")){
                    String nameOfColumn = columns.substring(columns.toLowerCase(Locale.ROOT).indexOf("as") ,columns.length());
                    String parts[] = nameOfColumn.split(" ");
                    String alias = nameOfColumn.substring(lista.indexOf(parts[0]) + parts[0].length() + 1,nameOfColumn.length());
                    //String alias = parts[1];
                    //  System.out.println("partsss 1 - alias vrednosti " + alias);
                    if(countWordsUsingSplit(alias)> 2){

                        System.out.println("ITS WORKING");
                        if(!alias.contains("\"\"")){
                            lista.add(alias);
                        }
                    }

                }
            }
        }
        // System.out.println(lista);
        return lista;
    }
    public static int countWordsUsingSplit(String input) { if (input == null || input.isEmpty()) { return 0; } String[] words = input.split("\\s+"); return words.length; }


    @Override
    public List<String> isAggregationWithoutGroupBy(Query query) {
        List<String> list  =new ArrayList<>();

        System.out.println("group by values : " + query.getMapByKeyWords().get(MainWords.GROUP_BY));
        if(query.getMapByKeyWords().get(MainWords.SELECT) != null && query.getMapByKeyWords().get(MainWords.GROUP_BY)!= null) {
            for (String column : query.getMapByKeyWords().get(MainWords.SELECT)) {
                if (functionOfAggregation(column)) {



                   /* if (query.getMapByKeyWords().get(MainWords.GROUP_BY)== null) {
                        return list;
                    }*/
                }
            }
        }else{
            return list;
        }

        list.add("GROUP BY");
        return list;
    }
    public boolean functionOfAggregation (String column){
        String columnUpperCase = column.toUpperCase(Locale.ROOT);
        List<String> aggList = new ArrayList<>();
        aggList.add("MAX");
        aggList.add("MIN");
        aggList.add("SUM");
        aggList.add("AVG");

        for(String agg: aggList){
            if(columnUpperCase.contains(agg)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> WhereIsWithoutFunctionOfAggregation(Query query) {
        List<String> list = new ArrayList<>();

        if(query.getMapByKeyWords().get(MainWords.WHERE) != null) {
            for (String columns : query.getMapByKeyWords().get(MainWords.WHERE)) {
                if (functionOfAggregation(columns)) {
                    list.add(columns);
                }
            }
        }
        return list;
    }

    @Override
    public String sameTypes(Query query) {
        return null;
    }

    @Override
    public List<String> orderOfTheStatement(Query query) {
        List<String> list = new ArrayList<>();


        Map<MainWords, Integer> map = sortValues(query.getOrderOfPrimaryKeys());


        for (Map.Entry<MainWords, Integer> set : map.entrySet()){
            int i = 0;
            for (Map.Entry<MainWords, Integer> set2 : map.entrySet()){
                if(set.getKey().equals(set2.getKey())){
                    i++;
                }
                if(set.getKey() != set2.getKey() ){
                    if(!(set.getKey().getPriority() < set2.getKey().getPriority()) && i == 1){
                        list.add(set.getKey().toString());
                    }
                    if(i == 1){
                        //  System.out.println(set.getValue() + " set kljuc " + set.getKey() + "uporedjujemo sa " + set2.getKey());
                        i = 2;
                    }
                }
            }

        }
        // System.out.println(list);
        return list;
    }
    private static HashMap sortValues(HashMap map)
    {
        List list = new LinkedList(map.entrySet());
//Custom Comparator
        Collections.sort(list, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });
//copying the sorted list in HashMap to preserve the iteration order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
    @Override
    public String ifCSVisSuitableForTable(Query query) {
        return null;
    }
}
