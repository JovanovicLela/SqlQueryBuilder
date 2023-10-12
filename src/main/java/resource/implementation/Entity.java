package resource.implementation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import resource.DBNode;
import resource.DBNodeComposite;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Entity extends DBNodeComposite {

    public Entity(String name, DBNode parent) {
        super(name, parent);
        relations = new ArrayList<>();
    }

    List<Entity> relations;

    @Override
    public void addChild(DBNode child) {
        if (child != null && child instanceof Attribute){
            Attribute attribute = (Attribute) child;
            this.getChildren().add(attribute);
        }

    }

    @Override
    public String toString() {
        return super.getName();
    }

    public void addRelation(Entity childByName) {
        relations.add(childByName);
    }
}
