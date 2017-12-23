package com.example.iris.meetmehalfway;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

/**
 * Created by kanishk on 12/18/2017.
 */
@DynamoDBTable(tableName = "MeetingStatus")

public class MeetingStatusDDBModel {

    private String userID;
    private List<String> pendingInvitations;
    private List<String> acceptedInvitations;

    @DynamoDBHashKey(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @DynamoDBAttribute(attributeName = "pending")
    public List<String> getPendingInvitations() {
        return pendingInvitations;
    }

    public void setPendingInvitations(List<String> pendingInvitations) {
        this.pendingInvitations = pendingInvitations;
    }

    @DynamoDBAttribute(attributeName = "accepted")
    public List<String> getAcceptedInvitations() {
        return acceptedInvitations;
    }

    public void setAcceptedInvitations(List<String> acceptedInvitations) {
        this.acceptedInvitations = acceptedInvitations;
    }

}
