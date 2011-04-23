package com.greetim

/**
 * Retrieve last message between users
 * @author Vitalii Samolovskikh aka Kefir
 */
class LastMessagesCommand {
    // Input parameters
    Profile profile1;
    Profile profile2;

    // Output parameters
    List<Message> messages;
    long firstMessageTimestamp = 0;
    long lastMessageTimestamp = System.currentTimeMillis();

    void execute(){
        messages = Message.last(profile1, profile2, 20);
        if (messages.size() > 0) {
            lastMessageTimestamp = messages.last().date.time;
            firstMessageTimestamp = messages.first().date.time;
        }
    }
}
