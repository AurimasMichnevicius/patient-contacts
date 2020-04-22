package com.Aurimas.lab1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

public class PatientContact {
    private long id;
    private int personalCode;
	private String condition;
    private String surname;
    private String name;
    private String number;
    private String email;
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Column(name = "personalcode", nullable = false)
    public int getPersonalCode() {
        return personalCode;
    }
	    @Column(name = "condition", nullable = false)
    public String getCondition() {
        return condition;
    }
    public void setpersonalCode(int personalCode) {
        this.personalCode = personalCode;
    }
	public void setCondition(String condition)
	{
		this.condition = condition;
	}
	    public void setId(int id){
        this.id = id;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public void setEmail(String email){

        this.email = email;
    }

    public String getSurname(){
        return surname;
    }

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }

    public String getEmail(){
        return email;
    }

}
