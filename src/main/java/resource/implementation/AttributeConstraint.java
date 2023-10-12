package resource.implementation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import resource.DBNode;
import resource.enums.ConstraintType;

@Getter
@Setter
public class AttributeConstraint extends DBNode {

    private ConstraintType constraintType;

    public AttributeConstraint(String name, DBNode parent, ConstraintType constraintType) {
        super(name, parent);
        this.constraintType = constraintType;
    }

}
