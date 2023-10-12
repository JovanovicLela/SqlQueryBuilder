package gui;


import checker.MainWords;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
@Data
public class Query {

    public HashMap<MainWords, List<String>> mapByKeyWords;
    public HashMap<MainWords, Integer> orderOfPrimaryKeys ;
    public String query;
    public String calling = "";
    public String tableNames;
    public String queryReplaced;
    public boolean select = false;
    public int order = 1;
    public List<MainWords> contentOfQuery = new ArrayList<>();
    public Query(){
        this.mapByKeyWords = new HashMap<>();
        this.orderOfPrimaryKeys = new HashMap<MainWords, Integer>();
    }


    public void parse(String query){
        this.query = query;
        this.queryReplaced = this.query;

        HashMap<String, List<String>> map = new HashMap<>();

        this.query = this.query.replace("\n"," ");
        this.query = this.query.toLowerCase(Locale.ROOT);


        String [] parts2 = this.query.split(" ");

        for(int i = 0; i < parts2.length; i++) {
            if(parts2[i].contains("group")){
                this.query = this.query.replaceAll("group by", "group_by");
            }
            if(parts2[i].contains("insert")){
                this.query = this.query.replaceAll("insert into", "insert_into");
            }
            if(parts2[i].contains("order")){
                this.query = this.query.replaceAll("order by", "order_by");
            }
            if(parts2[i].contains("inner")){
                this.query = this.query.replaceAll("inner join", "inner_join");
            }
            if(parts2[i].contains("left")){
                this.query = this.query.replaceAll("left join", "left_join");
            }
            if(parts2[i].contains("right")){
                this.query = this.query.replaceAll("right join", "right_join");
            }
            if(parts2[i].contains("full")){
                this.query = this.query.replaceAll("full join", "full_join");
            }



        }
        this.query = this.query.toUpperCase(Locale.ROOT);
        System.out.println("Query je pre parsovanja" + this.query);
        String [] parts = this.query.toUpperCase(Locale.ROOT).split(" ");
        for(int i = 0; i < parts.length; i++) {

            if(checkIfMainWordsExist(parts[i])){
                this.query = this.query.toLowerCase(Locale.ROOT);
                parseString(parts[i].toUpperCase(Locale.ROOT));
            }
        }
        System.out.println( "oVO JE USING " + mapByKeyWords.get(MainWords.USING));
        System.out.println( "oVO JE JOIN " + mapByKeyWords.get(MainWords.JOIN));
        System.out.println( "oVO JE INNER " + mapByKeyWords.get(MainWords.INNER_JOIN));
        System.out.println( "oVO JE FULL " + mapByKeyWords.get(MainWords.FULL_JOIN));
        System.out.println( "oVO JE ORDER " + mapByKeyWords.get(MainWords.ORDER_BY));
        System.out.println( "oVO JE VALUES " + mapByKeyWords.get(MainWords.VALUES));
        System.out.println( "oVO JE GROUP BY " + mapByKeyWords.get(MainWords.GROUP_BY));
        System.out.println( "oVO JE SELECT " + mapByKeyWords.get(MainWords.SELECT));
        System.out.println("oVO JE FROM " + mapByKeyWords.get(MainWords.FROM));
        System.out.println("oVO JE WHERE " + mapByKeyWords.get(MainWords.WHERE));
        System.out.println("oVO JE OR " + mapByKeyWords.get(MainWords.OR));
        System.out.println("oVO JE AND " + mapByKeyWords.get(MainWords.AND));
        System.out.println( "oVO JE INSERT " + mapByKeyWords.get(MainWords.INSERT_INTO));
        System.out.println( "oVO JE SET " + mapByKeyWords.get(MainWords.SET));
    }
    public void parseString(String part){
        switch(part.toUpperCase(Locale.ROOT)) {
            case "SELECT": {
                mapByKeyWords.put(MainWords.SELECT, parseStringsToList(query.toUpperCase(Locale.ROOT).indexOf("SELECT")));
                orderOfPrimaryKeys.put(MainWords.SELECT, order);
                contentOfQuery.add(MainWords.SELECT);
                order++;
                this.calling = "select";
            }
            break;
            case "FROM": {
                //parseStringsToList(query.toUpperCase(Locale.ROOT).indexOf("FROM")
                //List<String> lista = new ArrayList<>();
                //lista.add(getTableFrom());
                mapByKeyWords.put(MainWords.FROM,parseStringsToList(query.toUpperCase(Locale.ROOT).indexOf("FROM")) );
                orderOfPrimaryKeys.put(MainWords.FROM, order);
                order++;
                contentOfQuery.add(MainWords.FROM);
                if (!mapByKeyWords.get(MainWords.FROM).isEmpty()) {
                    this.tableNames = (mapByKeyWords.get(MainWords.FROM).get(0));
                }
            }
            break;
            case "WHERE": {
                mapByKeyWords.put(MainWords.WHERE, parseStringsToList(query.toUpperCase(Locale.ROOT).indexOf("WHERE")));
                orderOfPrimaryKeys.put(MainWords.WHERE, order);
                order++;
                contentOfQuery.add(MainWords.WHERE);
            }
            break;
            case "OR": {
                mapByKeyWords.put(MainWords.OR,parseStringsToList(query.toUpperCase(Locale.ROOT).indexOf("OR")));
                orderOfPrimaryKeys.put(MainWords.OR, order);
                order++;
                contentOfQuery.add(MainWords.OR);
            }
            break;
            case "UPDATE": {
                this.calling = "update";
                this.tableNames = findTableNameUpdate();
                orderOfPrimaryKeys.put(MainWords.UPDATE, order);
                order++;
                contentOfQuery.add(MainWords.UPDATE);
            }
            break;
            case "VALUES": {
                System.out.println(this.query);
                String values =this.query.substring(this.query.toLowerCase(Locale.ROOT).indexOf("values"), this.query.length());
                int a = 0,b = 0;
                if(values.contains("(") && values.contains(")")){
                    a = findFirst(this.query.toLowerCase(Locale.ROOT).indexOf("values"));
                    b = findLast(this.query.toLowerCase(Locale.ROOT).indexOf("values"));
                    mapByKeyWords.put(MainWords.VALUES,parseStringInBrackets(this.query.substring(a,b)));
                }else {
                    List<String>list = new ArrayList<>();
                    list.add("");
                    mapByKeyWords.put(MainWords.VALUES,list);
                }
                orderOfPrimaryKeys.put(MainWords.VALUES, order);
                order++;
                contentOfQuery.add(MainWords.VALUES);
            }
            break;
            case "INSERT_INTO" : {
                System.out.println(this.query);
                mapByKeyWords.put(MainWords.INSERT_INTO, parseStringInBrackets(this.query.substring(findFirst(1), findLast(1))));
                orderOfPrimaryKeys.put(MainWords.INSERT_INTO, order);
                order++;
                this.query = this.queryReplaced;
                this.calling = "insert";
                this.tableNames =  findTableNameInsert();
                contentOfQuery.add(MainWords.INSERT_INTO);
            }break;
            case "DELETE" : {
                //mapByKeyWords.put(MainWords.DELETE,parseStringsToList(query.toUpperCase(Locale.ROOT).indexOf("DELETE")));
                orderOfPrimaryKeys.put(MainWords.DELETE, order);
                order++;
                this.calling = "delete";
                contentOfQuery.add(MainWords.DELETE);
            }break;
            case "AND" : {
                mapByKeyWords.put(MainWords.AND,parseStringsToList(query.toUpperCase(Locale.ROOT).indexOf("AND")));
                orderOfPrimaryKeys.put(MainWords.AND, order);
                order++;
                contentOfQuery.add(MainWords.AND);
            }break;
            case "GROUP_BY" : {
                mapByKeyWords.put(MainWords.GROUP_BY, parseStringsToList(this.query.toUpperCase(Locale.ROOT).indexOf("GROUP_BY")));
                orderOfPrimaryKeys.put(MainWords.GROUP_BY, order);
                order++;
                contentOfQuery.add(MainWords.GROUP_BY);
            }break;
            case "ORDER_BY" :{
                mapByKeyWords.put(MainWords.ORDER_BY, parseStringsToList(this.query.toUpperCase(Locale.ROOT).indexOf("ORDER_BY")));
                orderOfPrimaryKeys.put(MainWords.ORDER_BY, order);
                order++;
                contentOfQuery.add(MainWords.ORDER_BY);
            }break;
            case "INNER_JOIN" :{
                mapByKeyWords.put(MainWords.INNER_JOIN, parseStringsToList(this.query.toUpperCase(Locale.ROOT).indexOf("INNER_JOIN")));
                orderOfPrimaryKeys.put(MainWords.INNER_JOIN, order);
                order++;
                contentOfQuery.add(MainWords.INNER_JOIN);
            }break;
            case "FULL_JOIN" :{
                mapByKeyWords.put(MainWords.FULL_JOIN, parseStringsToList(this.query.toUpperCase(Locale.ROOT).indexOf("FULL_JOIN")));
                orderOfPrimaryKeys.put(MainWords.FULL_JOIN, order);
                order++;
                contentOfQuery.add(MainWords.FULL_JOIN);
            }break;
            case "LEFT_JOIN" :{
                mapByKeyWords.put(MainWords.LEFT_JOIN, parseStringsToList(this.query.toUpperCase(Locale.ROOT).indexOf("LEFT_JOIN")));
                orderOfPrimaryKeys.put(MainWords.LEFT_JOIN, order);
                order++;
                contentOfQuery.add(MainWords.LEFT_JOIN);
            }break;
            case "RIGHT_JOIN" :{
                mapByKeyWords.put(MainWords.RIGHT_JOIN, parseStringsToList(this.query.toUpperCase(Locale.ROOT).indexOf("RIGHT_JOIN")));
                orderOfPrimaryKeys.put(MainWords.RIGHT_JOIN, order);
                order++;
                contentOfQuery.add(MainWords.RIGHT_JOIN);
            }break;
            case "JOIN": {
                mapByKeyWords.put(MainWords.JOIN, parseStringsToList(this.query.toUpperCase(Locale.ROOT).indexOf("JOIN")));
                orderOfPrimaryKeys.put(MainWords.JOIN, order);
                order++;
                contentOfQuery.add(MainWords.JOIN);
            }break;
            case "USING":{
                mapByKeyWords.put(MainWords.USING, parseStringInBrackets(this.query.substring(findFirst(1), findLast(1))));
                orderOfPrimaryKeys.put(MainWords.USING, order);
                order++;
                contentOfQuery.add(MainWords.USING);
            }
            break;


        }
    }
    public String getTableFrom(){
        String from = this.query.substring(this.query.indexOf("FROM"), this.query.length());

        String[] parts= from.split(" ");
        return parts[1];
    }
    private String findTableNameUpdate(){

        String[] parts = this.queryReplaced.split( " ");
        return parts[1];
    }
    private String findTableNameInsert(){
        String[] parts = this.queryReplaced.split( " ");
        if(parts[2].contains("(") || parts[2].toLowerCase(Locale.ROOT).contains("values")){
            return "";
        }
        ///  System.out.println("Tabelaa" + parts[2]);
        return parts[2];
    }
    private int findFirst(int i){
        return this.query.indexOf('(', i) + 1;
    }
    private int findLast(int i){
        return this.query.indexOf(')',i);
    }

    private List<String> parseStringInBrackets(String q){
        List<String> lista = new ArrayList<>();
        int i = 0;
        if(q.contains(",")) {
            String[] parts = q.split(",");
            int length = parts.length;

            while (length != 0) {
                parts[i] = parts[i].replaceAll("\\s+", "");
                lista.add(parts[i]);
                length--;
                i++;
            }
        }else {
            lista.add(q);
        }

        return lista;
    }

    // lista(employees, id, lala);
    private List<String> parseStringsToList(int position){

        List<String> list = new ArrayList<>();
        int s = endPosition(position);

        String sub = this.query.substring(position,endPosition(position));

        if(!sub.contains(",")) {
            String[] parts = sub.split(" ");
            sub = sub.substring(sub.indexOf(parts[1]), sub.length());
            System.out.println("Sub" + sub);
            sub = sub.replace(" ", "");
            list.add(sub);

            return list;

        }else {
            return fillTheList(sub);
        }
    }
    public List<String> fillTheList(String sub){
        List<String> lista = new ArrayList<>();

        String[] parts2 = sub.split(" ");
        sub = sub.replace(parts2[0],"");

        System.out.println("sub that contains ',' " + sub);
        String[] parts = sub.split(",");
        int length = parts.length;

        // String[] parts2 = parts[0].split(" ");
        //parts[0] = parts2[1];

        int i = 0;
        while(length!= 0){
            System.out.println(parts[i]);
            //zbog select treba da bude kolona as k
            if(!parts[i].toLowerCase(Locale.ROOT).contains("as")) {
                parts[i] = parts[i].replaceAll(" ", "");
            }
            lista.add(parts[i]);
            length--;
            i++;
        }
        return lista;
    }

    private int endPosition(int startPosition){
        System.out.println("startna " + startPosition);
        String sub = this.query.substring(startPosition, this.query.length());
        System.out.println("Sub * je FROM...  " + sub);

        //ako ne postoje znaci da je kraj query-a endPosition
        String[] parts = sub.split(" ");
        sub = sub.replace(parts[0],"none");


        if(!checkIfMainWordsExist(sub)){
            return this.query.length();
        }


        int length = parts.length;
        int i = 1;
        while(length != 0){
            if(checkIfMainWordsExist(parts[i])){
                return this.query.indexOf(parts[i]);
            }
            i++;
            length--;
        }

        return 0;
    }

    public HashMap<MainWords, Integer> getOrderOfPrimaryKeys() {
        return orderOfPrimaryKeys;
    }

    public HashMap<MainWords, List<String>> getMapByKeyWords() {
        return mapByKeyWords;
    }


    private boolean checkIfMainWordsExist(String query){
        for (MainWords mainWords : MainWords.values()) {
            if (query.toUpperCase().contains(mainWords.name())) {
                return true;
            }
        }

        return false;
    }

    public String getTableName() {
        if(this.tableNames == null){
            this.tableNames = "";
        }
        System.out.println(this.tableNames);
        return this.tableNames;
    }

    public List<MainWords> getContentOfQuery() {
        return contentOfQuery;
    }

    @Override
    public String toString() {

        this.query = this.query.toLowerCase(Locale.ROOT);

        String [] parts = this.query.split(" ");

        for(int i = 0; i < parts.length; i++) {
            if(parts[i].contains("group")){
                this.query = this.query.replaceAll("group_by", "group by");
            }
            if(parts[i].contains("insert")){
                this.query = this.query.replaceAll("insert_into", "insert into");
            }
            if(parts[i].contains("order")){
                this.query = this.query.replaceAll("orer_by", "order by");
            }
        }
        return this.query;
    }
}