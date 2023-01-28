package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<Integer,String> messages;
    HashSet<User> userDb;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.messages = new HashMap<>();
        this.userDb = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String createUser(String name, String mobile){
        if (userMobile.contains(mobile)){
            return null;
        }
        User user = new User(name,mobile);
        userMobile.add(mobile);
        userDb.add(user);
        return "SUCCESS";
    }
    public Group createGroup(List<User> users){
        if (users.size() < 2){
            return null;
        }
        if (users.size()==2){
            Group group = new Group(users.get(1).getName(),2);
            groupUserMap.put(group,users);
            adminMap.put(group,users.get(0));
            return group;
        }
        customGroupCount++;
        Group group = new Group("Group "+customGroupCount,users.size());
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }
    public int createMessage(String content){
        messageId++;
        messages.put(messageId,content);
        return messageId;
    }
    public int sendMessage(Message message, User sender, Group group){
        if (!groupUserMap.containsKey(group)){
            return -1;
        }
        List<User> users = groupUserMap.get(group);
        for (User user : users){
            if (sender.equals(user)){
                if (groupMessageMap.containsKey(group)){
                    List<Message> messageList = groupMessageMap.get(group);
                    messageList.add(message);
                    groupMessageMap.put(group,messageList);
                    messageId++;
                    messages.put(messageId,message.getContent());
                    return messageList.size();
                }else{
                    List<Message> messageList = new ArrayList<>();
                    messageList.add(message);
                    groupMessageMap.put(group,messageList);
                    messageId++;
                    messages.put(messageId,message.getContent());
                    return messageList.size();
                }
            }
        }
        return -2;
    }
    public String changeAdmin(User approver, User user, Group group){
        if (!groupUserMap.containsKey(group)){
            return "Group not exists";
        }
        if (!adminMap.get(group).equals(approver)){
            return "not an admin";
        }
        for (User users : groupUserMap.get(group)){
            if (users.equals(user)){
                adminMap.put(group,user);
                return "SUCCESS";
            }
        }
        return "not a user";
    }
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
                            userDb.remove(user);
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
