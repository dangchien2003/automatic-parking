package model.test;

import javax.persistence.*;

@Entity
@Table(name = "abc")
public class abcEntity {
    @Id
    private Integer c1;

    @Column
    private String value;

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Column
    private String value2;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getC1() {
        return c1;
    }

    public void setC1(Integer c1) {
        this.c1 = c1;
    }

    public abcEntity() {
    }
}
