package com.example.iris.meetmehalfway;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;

import java.util.List;

/**
 * Created by kanishk on 12/18/2017.
 */

public class MeetingStatusDDBModel {

    private String userID;
    private List<String> pendingInvitations;
    private List<String> acceptedInvitation;

    @DynamoDBHashKey(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    @DynamoDBAttribute(attributeName = "pending")
    public List<String> getPendingInvitatiosn() {
        return pendingInvitations;
    }

    @DynamoDBAttribute(attributeName = "accepted")
    public List<String> getAcceptedInvitation() {
        return acceptedInvitation;
    }


}
