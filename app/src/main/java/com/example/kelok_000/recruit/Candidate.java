package com.example.kelok_000.recruit;

import java.io.Serializable;

/**
 * Created by kelok_000 on 10/11/2015.
 */
public class Candidate implements Serializable{
    String name;
    String age;
    int gender;
    int photoId;
    int recommend;
    String most_experienced;
    String email;

    Candidate(String name, String age, int gender, int photoId, String most_experienced, int recommend) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.photoId = photoId;
        this.most_experienced = most_experienced;
        this.recommend = recommend;
        email = "abc@abc.com";
    }
}
