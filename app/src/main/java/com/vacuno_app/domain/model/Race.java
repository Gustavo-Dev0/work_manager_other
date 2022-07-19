package com.vacuno_app.domain.model;

import java.util.HashMap;
import java.util.Map;

public class Race {
    public String id;
    public String name;

    public Race(){}

    public Race(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        return map;
    }

}
