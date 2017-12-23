package com.example.iris.meetmehalfway;

/**
 * Created by Iris on 12/13/17.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "MeetingTable")
public class MeetingTable {
    private String meetingID;
    private String meetingDate;
    private String meetingTime;
    private String organizer;
    private List<String> confirmation;
    private Map<String,List<String>> acceptStatus;
    private Boolean finalStatus;

    @DynamoDBHashKey(attributeName = "MeetingId")
    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    @DynamoDBAttribute(attributeName = "meetingDate")
    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }


    @DynamoDBAttribute(attributeName = "meetingTime")
    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "organizer-index",attributeName = "organizer")
    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    @DynamoDBAttribute(attributeName = "Confirmation")
    public List<String> getConfirmation() {return confirmation;}

    public void setConfirmation(List<String> confirmation) {this.confirmation = confirmation;}

    @DynamoDBAttribute(attributeName = "acceptStatus")
    public Map<String,List<String>> getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(Map<String,List<String>> acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    @DynamoDBAttribute(attributeName = "finalStatus")
    public Boolean getFinalStatus() {return finalStatus;}
    public void setFinalStatus(Boolean finalStatus) {this.finalStatus = finalStatus;}

}
