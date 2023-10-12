package checker;

import gui.Query;

import java.util.HashMap;
import java.util.List;

public interface Rules {

    HashMap<String, List<String>> checkAllRules(Query query);
    List<String> ifTablesExists(Query query);
    List<String> ifColumnsExists(Query query);
    List<String> isColumnSuitableForJoin(Query query);
    List<String> ifEverythingNecessaryMissed(Query query);
    List<String> isAliasQuotationMarksValid(Query query);
    List<String> isAggregationWithoutGroupBy(Query query);
    List<String> WhereIsWithoutFunctionOfAggregation(Query query);
    String sameTypes(Query query);
    List<String> orderOfTheStatement(Query query);
    String ifCSVisSuitableForTable(Query query);
}
