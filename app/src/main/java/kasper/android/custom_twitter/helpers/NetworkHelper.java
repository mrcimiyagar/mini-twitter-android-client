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
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.AnswerAcceptFollowRequest;
import kasper.android.custom_twitter.models.packets.AnswerDeleteTweet;
import kasper.android.custom_twitter.models.packets.AnswerEditUserBio;
import kasper.android.custom_twitter.models.packets.AnswerFollow;
import kasper.android.custom_twitter.models.packets.AnswerGetFeed;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowRequests;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowers;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowings;
import kasper.android.custom_twitter.models.packets.AnswerGetHumanById;
import kasper.android.custom_twitter.models.packets.AnswerGetTopTweets;
import kasper.android.custom_twitter.models.packets.AnswerGetTweets;
import kasper.android.custom_twitter.models.packets.AnswerIgnoreFollowRequest;
import kasper.android.custom_twitter.models.packets.AnswerLikeTweet;
import kasper.android.custom_twitter.models.packets.AnswerLogin;
import kasper.android.custom_twitter.models.packets.AnswerPostTweet;
import kasper.android.custom_twitter.models.packets.AnswerRegister;
import kasper.android.custom_twitter.models.packets.AnswerRequest;
import kasper.android.custom_twitter.models.packets.AnswerSearchUserTitle;
import kasper.android.custom_twitter.models.packets.AnswerSwitchProfileMode;
import kasper.android.custom_twitter.models.packets.AnswerUnFollow;
import kasper.android.custom_twitter.models.packets.AnswerUnlikeTweet;
import kasper.android.custom_twitter.models.packets.NotifyFollowRequestAccepted;
import kasper.android.custom_twitter.models.packets.NotifyFollowRequestIgnored;
import kasper.android.custom_twitter.models.packets.NotifyNewFollow;
import kasper.android.custom_twitter.models.packets.NotifyNewTweet;
import kasper.android.custom_twitter.models.packets.NotifyUnFollow;
import kasper.android.custom_twitter.models.packets.RequestAcceptFollowRequest;
import kasper.android.custom_twitter.models.packets.RequestDeleteTweet;
import kasper.android.custom_twitter.models.packets.RequestEditUserBio;
import kasper.android.custom_twitter.models.packets.RequestFollow;
import kasper.android.custom_twitter.models.packets.RequestGetFeed;
import kasper.android.custom_twitter.models.packets.RequestGetFollowRequests;
import kasper.android.custom_twitter.models.packets.RequestGetFollowers;
import kasper.android.custom_twitter.models.packets.RequestGetFollowings;
import kasper.android.custom_twitter.models.packets.RequestGetHumanById;
import kasper.android.custom_twitter.models.packets.RequestGetTopTweets;
import kasper.android.custom_twitter.models.packets.RequestGetTweets;
import kasper.android.custom_twitter.models.packets.RequestIgnoreFollowRequest;
import kasper.android.custom_twitter.models.packets.RequestLikeTweet;
import kasper.android.custom_twitter.models.packets.RequestLogin;
import kasper.android.custom_twitter.models.packets.RequestPostTweet;
import kasper.android.custom_twitter.models.packets.RequestRegister;
import kasper.android.custom_twitter.models.packets.RequestSearchUserTitle;
import kasper.android.custom_twitter.models.packets.RequestSwitchProfileMode;
import kasper.android.custom_twitter.models.packets.RequestUnFollow;
import kasper.android.custom_twitter.models.packets.RequestUnlikeTweet;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;
import kasper.android.custom_twitter.models.packets.base.BaseNotify;
import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class NetworkHelper {

    private static final int connectTimeOut = 5000;
    private static final String serverIp = "192.168.43.36";//"136.243.229.153";
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
        client.getKryo().register(NotifyFollowRequestAccepted.class);
        client.getKryo().register(NotifyFollowRequestIgnored.class);
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
        client.getKryo().register(RequestEditUserBio.class);
        client.getKryo().register(RequestLikeTweet.class);
        client.getKryo().register(RequestUnlikeTweet.class);
        client.getKryo().register(RequestGetTopTweets.class);
        client.getKryo().register(RequestGetFollowRequests.class);
        client.getKryo().register(RequestAcceptFollowRequest.class);
        client.getKryo().register(RequestIgnoreFollowRequest.class);
        client.getKryo().register(RequestGetFeed.class);
        client.getKryo().register(RequestSwitchProfileMode.class);
        client.getKryo().register(RequestDeleteTweet.class);
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
        client.getKryo().register(AnswerEditUserBio.class);
        client.getKryo().register(AnswerLikeTweet.class);
        client.getKryo().register(AnswerUnlikeTweet.class);
        client.getKryo().register(AnswerGetTopTweets.class);
        client.getKryo().register(AnswerGetFollowRequests.class);
        client.getKryo().register(AnswerAcceptFollowRequest.class);
        client.getKryo().register(AnswerIgnoreFollowRequest.class);
        client.getKryo().register(AnswerGetFeed.class);
        client.getKryo().register(AnswerSwitchProfileMode.class);
        client.getKryo().register(AnswerDeleteTweet.class);
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

                    if (o instanceof NotifyNewFollow) {

                        NotifyNewFollow notifyNewFollow = (NotifyNewFollow) o;

                        Realm realm = Realm.getDefaultInstance();

                        MyData myData = realm.where(MyData.class).findFirst();

                        realm.beginTransaction();

                        kasper.android.custom_twitter.models.database.Human human = new kasper.android.custom_twitter.models.database.Human();
                        human.setHumanId(notifyNewFollow.getFollowerId());

                        myData.getHuman().getFollowers().add(human);

                        realm.commitTransaction();

                        realm.close();
                    }
                    else if (o instanceof NotifyUnFollow) {

                        NotifyUnFollow notifyUnFollow = (NotifyUnFollow) o;

                        Realm realm = Realm.getDefaultInstance();

                        MyData myData = realm.where(MyData.class).findFirst();

                        realm.beginTransaction();

                        boolean found = false;

                        int followIndex = 0;

                        for (kasper.android.custom_twitter.models.database.Human human : myData.getHuman().getFollowers()) {
                            if (human.getHumanId() == notifyUnFollow.getUnFollowerId()) {
                                found = true;
                                break;
                            }
                            followIndex++;
                        }

                        if (found) {
                            myData.getHuman().getFollowers().remove(followIndex);
                        }

                        realm.commitTransaction();

                        realm.close();
                    }
                    else if (o instanceof NotifyFollowRequestAccepted) {

                        NotifyFollowRequestAccepted notifyFollowRequestAccepted = (NotifyFollowRequestAccepted) o;

                        Realm realm = Realm.getDefaultInstance();

                        realm.beginTransaction();

                        MyData myData = realm.where(MyData.class).findFirst();

                        int counter = 0;

                        for (kasper.android.custom_twitter.models.database.Human human : myData.getHuman().getRequested()) {
                            if (human.getHumanId() == notifyFollowRequestAccepted.getHumanId()) {
                                break;
                            }
                            counter++;
                        }

                        myData.getHuman().getRequested().remove(counter);

                        kasper.android.custom_twitter.models.database.Human dHuman = new kasper.android.custom_twitter.models.database.Human();
                        dHuman.setHumanId(notifyFollowRequestAccepted.getHumanId());

                        myData.getHuman().getFollowing().add(dHuman);

                        realm.commitTransaction();

                        realm.close();

                    }
                    else if (o instanceof NotifyFollowRequestIgnored) {

                        NotifyFollowRequestIgnored notifyFollowRequestAccepted = (NotifyFollowRequestIgnored) o;

                        Realm realm = Realm.getDefaultInstance();

                        realm.beginTransaction();

                        MyData myData = realm.where(MyData.class).findFirst();

                        int counter = 0;

                        for (kasper.android.custom_twitter.models.database.Human human : myData.getHuman().getRequested()) {
                            if (human.getHumanId() == notifyFollowRequestAccepted.getHumanId()) {
                                break;
                            }
                            counter++;
                        }

                        myData.getHuman().getRequested().remove(counter);

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