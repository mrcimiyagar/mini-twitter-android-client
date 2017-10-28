package kasper.android.custom_twitter.helpers;

import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.realm.Realm;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.models.database.Feed;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.AnswerFollow;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowers;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowings;
import kasper.android.custom_twitter.models.packets.AnswerGetHumanById;
import kasper.android.custom_twitter.models.packets.AnswerGetTweets;
import kasper.android.custom_twitter.models.packets.AnswerLogin;
import kasper.android.custom_twitter.models.packets.AnswerPostTweet;
import kasper.android.custom_twitter.models.packets.AnswerRegister;
import kasper.android.custom_twitter.models.packets.AnswerRequest;
import kasper.android.custom_twitter.models.packets.AnswerSearchUserTitle;
import kasper.android.custom_twitter.models.packets.AnswerUnFollow;
import kasper.android.custom_twitter.models.packets.NotifyNewFollow;
import kasper.android.custom_twitter.models.packets.NotifyNewTweet;
import kasper.android.custom_twitter.models.packets.NotifyUnFollow;
import kasper.android.custom_twitter.models.packets.RequestFollow;
import kasper.android.custom_twitter.models.packets.RequestGetFollowers;
import kasper.android.custom_twitter.models.packets.RequestGetFollowings;
import kasper.android.custom_twitter.models.packets.RequestGetHumanById;
import kasper.android.custom_twitter.models.packets.RequestGetTweets;
import kasper.android.custom_twitter.models.packets.RequestLogin;
import kasper.android.custom_twitter.models.packets.RequestPostTweet;
import kasper.android.custom_twitter.models.packets.RequestRegister;
import kasper.android.custom_twitter.models.packets.RequestSearchUserTitle;
import kasper.android.custom_twitter.models.packets.RequestUnFollow;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;
import kasper.android.custom_twitter.models.packets.base.BaseNotify;
import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class NetworkHelper {

    private static final int connectTimeOut = 5000;
    private static final String serverIp = /*"192.168.43.23";*/"136.243.229.153";
    private static final int serverTcpPort = 24500;
    private static final int serverUdpPort = 24501;

    private Client client;

    private Hashtable<Long, OnRequestAnsweredListener> requestCallbacksMap;
    private BlockingQueue<BaseRequest> requestsQueue;

    private long generationCode = 1;

    public NetworkHelper() {

        this.requestCallbacksMap = new Hashtable<>();
        this.requestsQueue = new LinkedBlockingQueue<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        BaseRequest request = NetworkHelper.this.requestsQueue.take();
                        NetworkHelper.this.client.sendTCP(request);
                    }
                } catch (Exception ignored) {

                }
            }
        }).start();

        this.client = new Client();
        client.getKryo().register(Human.class);
        client.getKryo().register(Tweet.class);
        client.getKryo().register(NotifyUnFollow.class);
        client.getKryo().register(NotifyNewFollow.class);
        client.getKryo().register(NotifyNewTweet.class);
        client.getKryo().register(BaseNotify.class);
        client.getKryo().register(AnswerStatus.class);
        client.getKryo().register(RequestRegister.class);
        client.getKryo().register(RequestLogin.class);
        client.getKryo().register(RequestFollow.class);
        client.getKryo().register(RequestUnFollow.class);
        client.getKryo().register(RequestPostTweet.class);
        client.getKryo().register(RequestGetTweets.class);
        client.getKryo().register(RequestSearchUserTitle.class);
        client.getKryo().register(RequestGetHumanById.class);
        client.getKryo().register(RequestGetFollowers.class);
        client.getKryo().register(RequestGetFollowings.class);
        client.getKryo().register(AnswerRequest.class);
        client.getKryo().register(AnswerRegister.class);
        client.getKryo().register(AnswerLogin.class);
        client.getKryo().register(AnswerFollow.class);
        client.getKryo().register(AnswerUnFollow.class);
        client.getKryo().register(AnswerPostTweet.class);
        client.getKryo().register(AnswerGetTweets.class);
        client.getKryo().register(AnswerSearchUserTitle.class);
        client.getKryo().register(AnswerGetHumanById.class);
        client.getKryo().register(AnswerGetFollowers.class);
        client.getKryo().register(AnswerGetFollowings.class);
        client.getKryo().register(ArrayList.class);

        client.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {

                super.connected(connection);

                Log.d("KasperLogger", "Connected to server !");
            }

            @Override
            public void received(Connection connection, Object o) {

                super.received(connection, o);

                if (o instanceof BaseAnswer) {

                    Log.d("KasperLogger", "received answer : " + o.toString());

                    BaseAnswer rawAnswer = (BaseAnswer) o;

                    OnRequestAnsweredListener callback = NetworkHelper.this.requestCallbacksMap.get(rawAnswer.packetCode);

                    if (callback != null) {
                        NetworkHelper.this.requestCallbacksMap.remove(rawAnswer.packetCode);
                        callback.onRequestAnswered(rawAnswer);
                    }
                }
                else if (o instanceof BaseNotify) {

                    Log.d("KasperLogger", "received notify : " + o.toString());

                    if (o instanceof NotifyNewTweet) {

                        Log.d("KasperLogger", "hello !");

                        NotifyNewTweet notifyNewTweet = (NotifyNewTweet) o;

                        Tweet tweet = notifyNewTweet.getTweet();

                        Realm realm = Realm.getDefaultInstance();

                        MyData myData = realm.where(MyData.class).findFirst();

                        if (tweet.getPageId() != myData.getHuman().getHumanId()) {

                            realm.beginTransaction();

                            Feed feed = realm.where(Feed.class).findFirst();

                            kasper.android.custom_twitter.models.database.Human human = null;

                            human = feed.getHumans().where().equalTo("humanId", tweet.getAuthor().getHumanId()).findFirst();

                            if (human == null) {
                                human = new kasper.android.custom_twitter.models.database.Human();
                                human.setHumanId(tweet.getAuthor().getHumanId());
                                feed.getHumans().add(human);
                            }

                            human.setUserTitle(tweet.getAuthor().getUserTitle());

                            kasper.android.custom_twitter.models.database.Tweet dTweet = new kasper.android.custom_twitter.models.database.Tweet();
                            dTweet.setTweetId(tweet.getTweetId());
                            dTweet.setPageId(tweet.getPageId());

                            dTweet.setAuthor(human);
                            dTweet.setParentId(tweet.getParentId());
                            dTweet.setContent(tweet.getContent());
                            dTweet.setTime(tweet.getTime());

                            feed.getTweets().add(0, dTweet);

                            realm.commitTransaction();
                        }

                        realm.close();
                    }
                    else if (o instanceof NotifyNewFollow) {

                        NotifyNewFollow notifyNewFollow = (NotifyNewFollow) o;

                        Realm realm = Realm.getDefaultInstance();

                        MyData myData = realm.where(MyData.class).findFirst();

                        realm.beginTransaction();

                        kasper.android.custom_twitter.models.database.Human human = new kasper.android.custom_twitter.models.database.Human();
                        human.setHumanId(notifyNewFollow.getFollowerId());

                        myData.getHuman().setFollowersCount(myData.getHuman().getFollowersCount() + 1);
                        myData.getHuman().getFollowers().add(human);

                        realm.commitTransaction();

                        realm.close();
                    }
                    else if (o instanceof NotifyUnFollow) {

                        Realm realm = Realm.getDefaultInstance();

                        MyData myData = realm.where(MyData.class).findFirst();

                        realm.beginTransaction();

                        myData.getHuman().setFollowersCount(myData.getHuman().getFollowersCount() - 1);

                        realm.commitTransaction();

                        realm.close();
                    }
                }
            }
        });

        client.start();
    }

    public void connectToServer(final Runnable callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect(connectTimeOut, serverIp, serverTcpPort, serverUdpPort);
                    if (client.isConnected()) {
                        callback.run();
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }).start();
    }

    public void pushTCP(BaseRequest request, OnRequestAnsweredListener callback) {
        request.packetCode = generationCode++;
        this.requestCallbacksMap.put(request.packetCode, callback);
        this.requestsQueue.add(request);
    }
}