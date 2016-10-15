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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author Rodrigo B
 */
public class TwitterApp {

    public static final String[] TWITTER_QUERIES = {
        "#ProcesoDePaz",
        "#ReformaTributaria"
    };

    private static final String TWEETS_PATH = "tweets.txt";

    private final Twitter twitter;

    // BD Mongo
    public static final String DB_SERVER = "localhost";
    public static final int DB_PORT = 27017;
    public static final String DB_NAME = "Grupo10Twitter";
    public static final String COLLECTION_NAME = "OriginalData";

    private final MongoDatabase mongoDB;
    private final MongoCollection mongoCollection;

    public TwitterApp() {

        // Twitter Configuration
        ConfigurationBuilder cf = new ConfigurationBuilder();
        cf.setDebugEnabled(true)
                .setOAuthConsumerKey("Pjh11zSaQp7uI4Qe6HZrnXp5f")
                .setOAuthConsumerSecret("dCT2Z85JQ0B9FPafVrbEP26cC4dGduMkQeRT29YIU4Mhl8bhk0")
                .setOAuthAccessToken("105491840-OzLXnQyvNrpfm7pp6X0b6olTDWKKWmxvdhlQnA2a")
                .setOAuthAccessTokenSecret("edrCvTf2AOo7xIpuBwFNLY5GS0Dbx35KbFygMzs17hvrY");

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
                QueryResult result;
                do {
                    result = twitter.search(query);
                    List<Status> tweets = result.getTweets();
                    for (Status tweet : tweets) {
                        String jsonTweet = limpiarJSONparaMongo(tweet.toString());
                        if (writesFile && writer != null) {
                            writer.println(jsonTweet);
                        }
                        //mongoCollection.insertOne(Document.parse(jsonTweet));
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

    private String limpiarJSONparaMongo(String str) {
        // quitar encabezado y final
        str = str.replace("StatusJSONImpl{", "");
        str = str.substring(0, str.length() - 1);
        
        return str;
    }

    public void readMongoDB() {
        MongoCursor cursor = mongoCollection.find().iterator();
        while (cursor.hasNext()) {
            System.out.println("Cursor: " + cursor.next());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        TwitterApp tapp = new TwitterApp();
        tapp.readColombiaTopics(true);
        tapp.readMongoDB();

    }

}
