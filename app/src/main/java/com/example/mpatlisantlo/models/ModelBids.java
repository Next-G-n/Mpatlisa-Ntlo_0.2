package com.example.mpatlisantlo.models;

public class ModelBids {
    String BidsId,Bid_Price,Bid_Username,Bid_Dp,Bid_UserId,OwnerID,PostID,Request;


    public ModelBids() {
    }

    public ModelBids(String bidsId, String bid_Price, String bid_Username, String bid_Dp, String bid_UserId, String ownerID, String postID, String request) {
        BidsId = bidsId;
        Bid_Price = bid_Price;
        Bid_Username = bid_Username;
        Bid_Dp = bid_Dp;
        Bid_UserId = bid_UserId;
        OwnerID = ownerID;
        PostID = postID;
        Request = request;
    }

    public String getBidsId() {
        return BidsId;
    }

    public void setBidsId(String bidsId) {
        BidsId = bidsId;
    }

    public String getBid_Price() {
        return Bid_Price;
    }

    public void setBid_Price(String bid_Price) {
        Bid_Price = bid_Price;
    }

    public String getBid_Username() {
        return Bid_Username;
    }

    public void setBid_Username(String bid_Username) {
        Bid_Username = bid_Username;
    }

    public String getBid_Dp() {
        return Bid_Dp;
    }

    public void setBid_Dp(String bid_Dp) {
        Bid_Dp = bid_Dp;
    }

    public String getBid_UserId() {
        return Bid_UserId;
    }

    public void setBid_UserId(String bid_UserId) {
        Bid_UserId = bid_UserId;
    }

    public String getOwnerID() {
        return OwnerID;
    }

    public void setOwnerID(String ownerID) {
        OwnerID = ownerID;
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }

    public String getRequest() {
        return Request;
    }

    public void setRequest(String request) {
        Request = request;
    }
}
