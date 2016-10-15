/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniandes.edu.twitterreader;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import org.bson.Document;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 *
 * @author Daniel
 */
public class TwitterApp1 {
    
    // BD Mongo
    public static final String DB_SERVER = "localhost";
    public static final int DB_PORT = 27017;
    public static final String DB_NAME = "Grupo10Twitter";
    public static final String COLLECTION_NAME = "OriginalData";

     public static final String[] TWITTER_QUERIES = {
        //"#ProcesoDePaz",
        //"#ReformaTributaria",
        //"#AcuerdoYa"
        // "@lasillavacia"
    };
   
    
    public TwitterApp1(){
        
        
    }
    
    public static void main(String[] args) throws TwitterException {

        // Mongo connection
        MongoDatabase mongoDB = new MongoClient(DB_SERVER, DB_PORT).getDatabase(DB_NAME);
        if (mongoDB.getCollection(COLLECTION_NAME) == null) {
            mongoDB.createCollection(COLLECTION_NAME);
        }
        MongoCollection mongoCollection = mongoDB.getCollection(COLLECTION_NAME);

        ConfigurationBuilder cf = new ConfigurationBuilder();
        cf.setDebugEnabled(true)
                .setOAuthConsumerKey("xqlWFVus3JzlEhnYTi0lmG6rx")
                .setOAuthConsumerSecret("Qwez9YfyJZeUp0KKvUXQ1x6Ov9uhy8NHHjBvua36z7oOGbYHG3")
                .setOAuthAccessToken("426326842-hPErh9QCYpC8Bf3f9EXHOvAkLecypr4zYuzWfWjd")
                .setOAuthAccessTokenSecret("6OGpkuN5lYnSU2IWnp9fAA2hJRSk2LdvNozvT1aVIeikT");

        cf.setJSONStoreEnabled(true);

        Twitter twitter = new TwitterFactory(cf.build()).getInstance();
        for (String queryString : TWITTER_QUERIES) {
            System.out.println(queryString);
            Query query = new Query(queryString);
            QueryResult result = twitter.search(query);
             List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                String tweetJson = TwitterObjectFactory.getRawJSON(tweet);
                System.out.println(tweetJson);
                mongoCollection.insertOne(Document.parse(tweetJson));
            }
        }
    }


}
