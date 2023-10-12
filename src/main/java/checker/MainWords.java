package checker;

public enum MainWords {
    SELECT(1),
    FROM(2),
    DELETE(1),
    WHERE(3),
    AND(4),
    OR(4),
    VALUES(2),
    SET(2),
    UPDATE(1),
    GROUP_BY(4),
    ORDER_BY(5),
    INNER_JOIN(3),
    LEFT_JOIN(3),
    RIGHT_JOIN(3),
    FULL_JOIN(3),
    USING(4),
    JOIN(3),
    INSERT_INTO(1);
    public final int priority;
    MainWords(int i) {
        this.priority = i;
    }

    public int getPriority() {
        return priority;
    }
}
