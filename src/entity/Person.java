/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author ASUS
 */
public class Person {
    private String name;
    private String gender;
    private String sdt;

    public Person(String name, String gender, String sdt) {
        this.name = name;
        this.gender = gender;
        this.sdt = sdt;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getSdt() {
        return sdt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
}
