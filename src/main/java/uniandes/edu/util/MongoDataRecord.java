/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniandes.edu.util;

/**
 *
 * @author Rodrigo B
 */
public class MongoDataRecord {

    String hashtag;
    String user;
    String date;
    String sentiment;

    float tweets;
    float retweets;
    float followers;

    public MongoDataRecord(String hashtag, String user, String date, String sentiment, float tweets, float retweets, float followers) {
        this.hashtag = hashtag;
        this.user = user;
        this.date = date;
        this.sentiment = sentiment;
        this.tweets = tweets;
        this.retweets = retweets;
        this.followers = followers;
    }

    public String getHashtag() {
        return hashtag;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getSentiment() {
        return sentiment;
    }

    public float getTweets() {
        return tweets;
    }

    public float getRetweets() {
        return retweets;
    }

    public float getFollowers() {
        return followers;
    }
    
    public String getWeek(){
        String week = "Semana ";
        int dia = Integer.parseInt(date);
        
        if(dia < 10){
            week += "1";
        }else if(dia > 10 && dia < 17){
            week += "2";
        }else if(dia > 17 && dia < 24){
            week += "3";
        }else if(dia > 24){
            week += "4";
        }
        
        return week;
    }

}
