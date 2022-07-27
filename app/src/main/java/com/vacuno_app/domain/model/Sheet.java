package com.vacuno_app.domain.model;

import com.google.firebase.database.ServerValue;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Sheet {
    public String id, code, name, race, dateBirth, father, mather, weight, age, sex, status;
    public Long date;

    public Sheet(){}

    public Sheet(String code, String name, String race, String dateBirth, String father, String mather, String weight, String age, String sex, String status) {
        this.code = code;
        this.name = name;
        this.race = race;
        this.dateBirth = dateBirth;
        this.father = father;
        this.mather = mather;
        this.weight = weight;
        this.age = age;
        this.sex = sex;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        //map.put("code", getCode());
        map.put("race", getRace());
        map.put("dateBirth", getDateBirth());
        map.put("father", getFather());
        map.put("mather", getMather());
        map.put("weight", getWeight());
        map.put("age", getAge());
        map.put("sex", "F");
        map.put("status", "A");
        map.put("date", ServerValue.TIMESTAMP);
        return map;
    }

    public static Comparator<Sheet> serviceNameAZComparator = (u1, u2) -> u1.getName().compareTo(u2.getName());
    public static Comparator<Sheet> serviceNameZAComparator = (u1, u2) -> u2.getName().compareTo(u1.getName());
    public static Comparator<Sheet> serviceStatusComparator = (u1, u2) -> u1.getStatus().compareTo(u2.getStatus());
}
