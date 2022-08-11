package com.vacuno_app.domain.model;

import com.google.firebase.database.ServerValue;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Sheet {
    public String id = null,
            name = null,
            race = null,
            dateBirth = null,
            father = null,
            mather = null,
            weight = null,
            age = null,
            sex = null,
            status = null;
    public Long date;

    public Sheet(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMather() {
        return mather;
    }

    public void setMather(String mather) {
        this.mather = mather;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        map.put("race", getRace());
        map.put("dateBirth", getDateBirth());
        map.put("father", getFather());
        map.put("mather", getMather());
        map.put("weight", getWeight());
        map.put("age", getAge());
        map.put("sex", getSex());
        map.put("status", "A");
        map.put("date", ServerValue.TIMESTAMP);
        return map;
    }

}
