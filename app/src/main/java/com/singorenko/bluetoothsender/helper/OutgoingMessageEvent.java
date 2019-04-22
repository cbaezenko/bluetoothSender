package com.singorenko.bluetoothsender.helper;

public class OutgoingMessageEvent {

    private String outgoingMessage;

    public OutgoingMessageEvent(String outgoingMessage){
        this.outgoingMessage = outgoingMessage;
    }

    public String getOutgoingMessage() {
        return outgoingMessage;
    }
}
