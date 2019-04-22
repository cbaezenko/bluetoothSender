package com.singorenko.bluetoothsender.helper;

public class IncomingMessageEvent {
    private String text;
    public IncomingMessageEvent(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
