package com.Aurimas.lab1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Patients")

public class Patient {
    private long id;
    private int personalCode;
	private String condition;

    public Patient(){

    }

    public Patient(int personalCode, String condition){
        this.personalCode = personalCode;
		this.condition = condition;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "personalcode", nullable = false)
    public int getPersonalCode() {
        return personalCode;
    }
	    @Column(name = "condition", nullable = false)
    public String getCondition() {
        return condition;
    }
    public void setPersonalCode(int personalCode) {
        this.personalCode = personalCode;
    }
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

}
