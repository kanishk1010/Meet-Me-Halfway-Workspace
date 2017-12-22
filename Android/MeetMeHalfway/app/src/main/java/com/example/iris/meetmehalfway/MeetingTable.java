package com.example.iris.meetmehalfway;

/**
 * Created by Iris on 12/13/17.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.Map;

@DynamoDBTable(tableName = "MeetingTable")
public class MeetingTable {
    private String MeetingID;
    private Map<String, Boolean> acceptStatus;
    private String meetingDate;
    private String meetingTime;
    private String morganizer;

    @DynamoDBHashKey(attributeName = "MeetingID")
    public String getMeetingID() {
        return MeetingID;
    }

    public void setMeetingID(String meetingID) {
        this.MeetingID = meetingID;
    }


    @DynamoDBAttribute(attributeName = "acceptStatus")
    public Map<String, Boolean> getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(Map<String, Boolean> acceptStatus) {
        this.acceptStatus = acceptStatus;
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

    @DynamoDBAttribute(attributeName = "morganizer")
    public String getMorganizer() {
        return morganizer;
    }

    public void setMorganizer(String morganizer) {
        this.morganizer = morganizer;
    }


}
