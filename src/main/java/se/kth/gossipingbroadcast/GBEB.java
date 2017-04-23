package se.kth.gossipingbroadcast;

import se.kth.croupier.util.CroupierHelper;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mikael on 2017-04-03.
 */
public class GBEB extends ComponentDefinition {

    private KAddress self;
    private HashMap<KAddress, Object> past;

    private Positive<Network> net =  requires(Network.class);
    private Positive<CroupierPort> bs = requires(CroupierPort.class);   //bs = croupierPort
    private Negative<GBEBPort> gbeb = provides(GBEBPort.class);

    public GBEB(Init init) {
        this.self = init.self;
        past = new HashMap<>();

        //subscriptions
        subscribe(broadcastHandler, gbeb);
        subscribe(croupierPortHandler, bs);
        subscribe(historyDeliverHandler, net);
        subscribe(historyResponseHandler, net);

    }

    private Handler<GBEBroadcast> broadcastHandler = new Handler<GBEBroadcast>() {
        @Override
        public void handle(GBEBroadcast gbeBroadcast) {
            past.put(self, gbeBroadcast.getMessage());
        }
    };

    private Handler<CroupierSample> croupierPortHandler = new Handler<CroupierSample>() {
        @Override
        public void handle(CroupierSample croupierSample) {
            List<KAddress> sample = CroupierHelper.getSample(croupierSample);
            for(KAddress peer : sample) {
                KHeader header = new BasicHeader(self, peer, Transport.TCP);
                KContentMsg msg = new BasicContentMsg(header, new HistoryRequest());
                trigger(msg, net);
            }
        }
    };

    private ClassMatchedHandler<HistoryRequest, KContentMsg<?, ?, HistoryRequest>> historyDeliverHandler = new ClassMatchedHandler<HistoryRequest, KContentMsg<?, ?, HistoryRequest>>() {
        @Override
        public void handle(HistoryRequest historyRequest, KContentMsg<?, ?, HistoryRequest> historyRequestKContentMsg) {
            KHeader header = new BasicHeader(self, historyRequestKContentMsg.getSource(), Transport.TCP);
            KContentMsg msg = new BasicContentMsg(header, new HistoryResponse(past));
            trigger(msg, net);
        }
    };

    private ClassMatchedHandler<HistoryResponse, KContentMsg<?, ?, HistoryResponse>> historyResponseHandler = new ClassMatchedHandler<HistoryResponse, KContentMsg<?, ?, HistoryResponse>>() {
        @Override
        public void handle(HistoryResponse historyResponse, KContentMsg<?, ?, HistoryResponse> historyResponseKContentMsg) {
            HashMap<KAddress, Object> history = new HashMap<>(historyResponse.getPast());
            HashMap<KAddress, Object> unseen = new HashMap<>();
            for(Map.Entry<KAddress, Object> entry : history.entrySet()) {
                if(!past.containsKey(entry.getKey())) {
                    unseen.put(entry.getKey(), entry.getValue());
                }
            }

            for(Map.Entry<KAddress, Object> peer : unseen.entrySet()) {
                trigger(new GBEBDeliver(peer.getKey(), peer.getValue()), gbeb);     // in RB
            }

            past.putAll(unseen);
        }
    };



    public static class Init extends se.sics.kompics.Init<GBEB> {
        public KAddress self;

        public Init(KAddress self) {
            this.self = self;
        }
    }
}
