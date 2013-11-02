import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;

class SentimentClassifier {  
    String[] categories;  
    LMClassifier classs;  
    public SentimentClassifier() {  
    try {  
       classs= (LMClassifier) AbstractExternalizable.readObject(new File("classifier.txt"));  
       categories = classs.categories();  
    }  
    catch (ClassNotFoundException e) {  
       e.printStackTrace();  
    }  
    catch (IOException e) {  
       e.printStackTrace();  
    }  
    }  
    public String classify(String text) {  
    ConditionalClassification classification = classs.classify(text);  
    return classification.bestCategory();  
    }  
 }  

public class Tweeting {

	static int LIMIT = 500;

	public static void main(String[] args) throws IOException, TwitterException {

		// Your Twitter App's Consumer Key
		String consumerKey = "***";

		// Your Twitter App's Consumer Secret
		String consumerSecret = "***";

		// Your Twitter Access Token
		String accessToken = "***";

		// Your Twitter Access Token Secret
		String accessTokenSecret = "***";

		// Instantiate a re-usable and thread-safe factory
		TwitterFactory twitterFactory = new TwitterFactory();

		// Instantiate a new Twitter instance
		Twitter twitter = twitterFactory.getInstance();

		// setup OAuth Consumer Credentials
		twitter.setOAuthConsumer(consumerKey, consumerSecret);

		// setup OAuth Access Token
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));

		Query query = new Query("Breaking Bad is over & it's Monday morning");
		query.setCount(100);
		try {
			SentimentClassifier sentClassifier = new SentimentClassifier();
			 int count = 0,p=0,n=0,v=0;
			 QueryResult r;
			 do {
			 r = twitter.search(query);
			 ArrayList ts = (ArrayList) r.getTweets();
			
			 for (int i = 0; (i < ts.size()) && (count < LIMIT); i++) {
			 count++;
			 Status t = (Status) ts.get(i);
			 String text = t.getText();
			 System.out.println("Text: " + text);
			 String name = t.getUser().getScreenName();
			 System.out.println("User: " + name);
			 String sent = sentClassifier.classify(t.getText());
			 if(sent.equals("pos"))
			 p++;
			 else if(sent.equals("neu"))
			 n++;
			 else
			 v++;
			 System.out.println("Sentiment: " + sent);
			 }
			 } while ((query = r.nextQuery()) != null && count < LIMIT);
			 System.out.println("TOtal= "+count+" positive= "+p+" negitive= "+n+" neitral= "+n);
			
		} catch (Exception e) {
			System.out.println(e + " ");
		}

	}

}
