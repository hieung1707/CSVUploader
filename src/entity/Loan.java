/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Date;

/**
 *
 * @author ASUS
 */
public class Loan {
    private Person person;
    private String type;
    private int amount;
    private Date expDate;
    private Date oblReturnDate;

    public Loan(Person person, String type, int amount, Date expDate, Date oblReturnDate) {
        this.person = person;
        this.type = type;
        this.amount = amount;
        this.expDate = expDate;
        this.oblReturnDate = oblReturnDate;
    }

    public Person getPerson() {
        return person;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public Date getExpDate() {
        return expDate;
    }

    public Date getOblReturnDate() {
        return oblReturnDate;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public void setOblReturnDate(Date oblReturnDate) {
        this.oblReturnDate = oblReturnDate;
    }
    
    
}
