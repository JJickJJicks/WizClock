package com.jjickjjicks.wizclock.data.item;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Member {
    private String email;
    private String name;
    private String phoneNumber;
    private int experience;

    public Member() {
    }

    public Member(String email, String name) {
        this.email = email;
        this.name = name;
        this.phoneNumber = "";
        this.experience = 0;
    }

    public Member(String email, String name, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.experience = 0;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setExperience(int level, int experience) {
        this.experience = (level - 1) * 10 + experience;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getLevel() {
        return experience / 10 + 1;
    }

    public int getExperience() {
        return experience % 10;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("phoneNumber", phoneNumber);
        result.put("experience", experience);

        return result;
    }
}
