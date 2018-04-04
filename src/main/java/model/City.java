package model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Cities")
public class City extends Base {

    public City() {
    }

    public City(String name) {
        super(name);
    }

    public City(int id, String name) {
        super(id, name);
    }
}
