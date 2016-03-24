package edu.ncku.application.model;

/**
 * Created by NCKU on 2016/3/8.
 */
public class Message extends News {
    public Message(String title, int pubTime, String contents) {
        super(title, "NULL", pubTime, pubTime, contents);
    }
}
