package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below-mentioned hashmaps or delete these and create your own.
    private HashMap<Integer, String> messages;
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;
     private HashSet<User> userDB;

    public WhatsappRepository() {
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
        this.userDB = new HashSet<User>();
    }

    public String createUser(String name, String mobile) {
        if (userMobile.contains(mobile))
            return "user already exists";
        User user = new User(name, mobile);
        userMobile.add(mobile);
        userDB.add(user);
        return "user added successfully";
    }

    public Group createGroup(List<User> users) {
        if (users.size() < 2) return null;
        if (users.size() == 2) {
            Group group = new Group(users.get(1).getName(), 2);
            groupUserMap.put(group, users);
            adminMap.put(group, users.get(0));
        }
        customGroupCount++;
        Group group = new Group("Group" + " " + customGroupCount, users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public int createMessage(String message) {
        messageId++;
        messages.put(messageId, message);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) {
        if (!groupUserMap.containsKey(group)) return -1;
        List<User> list = groupUserMap.get(group);
        for (User user : list) {
            if (sender.equals(user)) {
                if (groupMessageMap.containsKey(group)) {
                    List<Message> grpmsg = groupMessageMap.get(group);
                    grpmsg.add(message);
                    groupMessageMap.put(group, grpmsg);
                    messageId++;
                    messages.put(messageId, message.getContent());
                    return grpmsg.size();
                } else {
                    List<Message> grpmsg = new ArrayList<>();
                    grpmsg.add(message);
                    groupMessageMap.put(group, grpmsg);
                    messageId++;
                    messages.put(messageId, message.getContent());
                    return grpmsg.size();
                }
            }
        }
        return -2;
    }
//    Change the admin of a group by providing the approver, user, and group.
//
//    If the mentioned group does not exist, the application will throw an exception.
//    If the approver is not the current admin of the group, the application will throw an exception.
//    If the user is not a part of the group, the application will throw an exception.
//    If all the conditions are met, it will change the admin of the group to "user" and return "SUCCESS". Note that the admin rights are transferred from the approver to the user in this case.

    public String changeAdmin(User approver, User user , Group group){
        if(!groupUserMap.containsKey(group))
            return "invalid group";
        if(!adminMap.get(group).equals(approver))
            return "invalid admin";
        for(User user1 : groupUserMap.get(group)){
            if(user1.equals(user)){
                adminMap.put(group,user);
                return "Success";
            }
        }
        return "Not a user";
    }
//    Remove a user from a group by providing the user.
//
//    If the user is not found in any group, the application will throw an exception.
//    If the user is found in a group and is the admin, the application will throw an exception.
//    If the user is not the admin, the application will remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
//    If the user is removed successfully, the application will return (the updated number of users in the group + the updated number of messages in the group + the updated number of overall messages across all groups).

    public int removeUser(User user){
        for (Group group : groupUserMap.keySet()){
            for (User users : groupUserMap.get(group)){
                if (users.equals(user)){
                    for (User admin : adminMap.values()){
                        if (admin.equals(user)){
                            return -2;
                        }
                    }
                    for (Message message : senderMap.keySet()){
                        if (senderMap.get(message).equals(user)){
                            senderMap.remove(message);
                            groupMessageMap.get(group).remove(message);
                            userDB.remove(user);
                        }
                        groupUserMap.get(group).remove(user);
                        group.setNumberOfParticipants(group.getNumberOfParticipants()-1);
                        return messageId + groupMessageMap.get(group).size()+groupUserMap.get(group).size();
                    }
                }
            }
        }
        return -1;
    }
    public String findMessage(Date start, Date end, int K){
        if (messages.size() < K){
            return null;
        }
        return messages.get(K);
    }



}




