/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniandes.edu.twitterreader;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.Document;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.conf.ConfigurationBuilder;
import uniandes.edu.util.MongoDataRecord;

/**
 *
 * @author Rodrigo B
 */
public class TwitterApp {

    public static final String[] TWITTER_QUERIES = {
        "#ProcesoDePaz"
        ,"#ReformaTributaria"
        ,"#PazSinReformaTributaria"
        ,"#ReformaTributaria2016"
//        "#AcuerdoYa"
    };
    
    public static final String QUERY_SINCE_DATE = "2016-10-25";
    public static final String QUERY_LAST_DATE = "2016-10-27";

    private static final String TWEETS_PATH = "tweets.txt";

    private final Twitter twitter;

    // BD Mongo
    public static final String DB_SERVER = "localhost";
    public static final int DB_PORT = 27017;
    public static final String DB_NAME = "Grupo10Twitter";
    public static final String COLLECTION_NAME = "OriginalData";

    private final MongoDatabase mongoDB;
    private final MongoCollection mongoCollection;
    
    private final List<MongoDataRecord> mongoResults = new ArrayList<>();

    public TwitterApp() {

        // Twitter Configuration
        ConfigurationBuilder cf = new ConfigurationBuilder();
        cf.setDebugEnabled(true)
                .setOAuthConsumerKey("Pjh11zSaQp7uI4Qe6HZrnXp5f")
                .setOAuthConsumerSecret("dCT2Z85JQ0B9FPafVrbEP26cC4dGduMkQeRT29YIU4Mhl8bhk0")
                .setOAuthAccessToken("105491840-OzLXnQyvNrpfm7pp6X0b6olTDWKKWmxvdhlQnA2a")
                .setOAuthAccessTokenSecret("edrCvTf2AOo7xIpuBwFNLY5GS0Dbx35KbFygMzs17hvrY");
        
        cf.setJSONStoreEnabled(true);
        
        TwitterFactory tf = new TwitterFactory(cf.build());
        twitter = tf.getInstance();

        // Mongo connection
        mongoDB = new MongoClient(DB_SERVER, DB_PORT).getDatabase(DB_NAME);
        if (mongoDB.getCollection(COLLECTION_NAME) == null) {
            mongoDB.createCollection(COLLECTION_NAME);
        }
        mongoCollection = mongoDB.getCollection(COLLECTION_NAME);

    }

    /**
     * Reads all twetts accoding to TWITTER_QUERIES with JSON format and inserts
     * in MongoDB collection
     *
     * @param writesFile writes an additional file in TWEETS_PATH if true
     */
    public void readColombiaTopics(boolean writesFile) {

        PrintWriter writer = null;

        try {
            if (writesFile) {
                writer = new PrintWriter(TWEETS_PATH, "UTF-8");
            }

            for (String queryString : TWITTER_QUERIES) {
                Query query = new Query(queryString);
                query.setSince(QUERY_SINCE_DATE);
                query.setUntil(QUERY_LAST_DATE);
                QueryResult result;
                do {
                    result = twitter.search(query);
                    List<Status> tweets = result.getTweets();
                    for (Status tweet : tweets) {
                        String jsonTweet = TwitterObjectFactory.getRawJSON(tweet);
                        if (writesFile && writer != null) {
                            writer.println(jsonTweet);
                        }
                        mongoCollection.insertOne(Document.parse(jsonTweet));
                    }
                } while ((query = result.nextQuery()) != null);
            }

        } catch (FileNotFoundException | UnsupportedEncodingException | TwitterException ex) {
            Logger.getLogger(TwitterApp.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (writesFile && writer != null) {
                writer.close();
            }
        }

    }
    
    public void getMongoFunctionResults(String functionStringWithParameters){
        Document doc2 = mongoDB.runCommand(new Document("$eval", functionStringWithParameters));
        String collectionResults = doc2.getString("retval");
        
        mongoResults.clear();
        
        MongoCursor cursor = mongoDB.getCollection(collectionResults).find().iterator();
        
        while (cursor.hasNext()) {
            String resultString = cursor.next().toString();
            
            // obtener propiedades del resultado
            String hashtag = getValueFromMongoResult(resultString, "hashtag", false),
                user = getValueFromMongoResult(resultString, "user", false),
                date = getValueFromMongoResult(resultString, "date", false),
                sentiment = getValueFromMongoResult(resultString, "sentiment", false);
            
            float tweets = Float.parseFloat(getValueFromMongoResult(resultString, "totalTweets", true)), 
                retweets = Float.parseFloat(getValueFromMongoResult(resultString, "totalReTweet", true)), 
                followers = Float.parseFloat(getValueFromMongoResult(resultString, "totalFollowers", true));
            
            mongoResults.add(new MongoDataRecord(hashtag, user, date, sentiment, tweets, retweets, followers));
            
            System.out.println(hashtag + "|" + user + "|" +  date + "|" +  sentiment + "|" +  tweets + "|" +  retweets + "|" +  followers);
        }
    }
    
    public String getValueFromMongoResult(String mongoResult, String property, boolean returnsNumberIfNoExists){
        
        Pattern regexItems = Pattern.compile("(?s)(?<=" + property + "=).+?(?=,|})");
        Matcher mItems = regexItems.matcher(mongoResult);
        
        if (mItems.find()) {
            return mItems.group(0);
        }else{
            if(returnsNumberIfNoExists){
                return "0";
            }else{
                return "";
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        TwitterApp tapp = new TwitterApp();
        //tapp.readColombiaTopics(true);
        String regexAll = "\".\"";
        tapp.getMongoFunctionResults("getGeneralInfo(" + regexAll + ", " + regexAll + ")");
        
        // fecha, tema, sentimiento, user.followers_count
    }

}
