package com.wang.util;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO 1. check if there is any problem caused by thread local
 * <p/>
 * <p/>
 * Created by Tang Fulin on 14-4-25.
 */
public class WbHttpClient extends DefaultHttpClient {

    private static final String VERSION = "2.1";
    public final WbTracer tracer = new WbTracer();
    private boolean doTrackTime = true; // if false, WbHttpClient === DefaultHttpClient
    private boolean postOnlyWifi = true;
    private Thread backgroundThread;
    private String lastTraceInfo;

    // Just keep the same way of new DefaultHttpClient
    public WbHttpClient(final ClientConnectionManager conman, final HttpParams params) {
        super(conman, params);
        setDoTrackTime(doTrackTime);
    }

    public WbHttpClient(final ClientConnectionManager conman) {
        this(conman, null);
    }

    public WbHttpClient(final HttpParams params) {
        this(null, params);
    }

    public WbHttpClient() {
        this(null, null);
    }

    //////////////////////////////////////////////////////////////

    public String getContentAsString(HttpUriRequest request) throws IOException {
        HttpResponse response = execute(request);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        String result = readStream(inputStream);
        return result;
    }

    public String getURL(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        return getContentAsString(get);
    }

    public String getLastTraceInfo() {
        return lastTraceInfo;
    }

    /**
     * @return
     * @see org.apache.http.impl.client.AbstractHttpClient#execute(HttpHost, HttpRequest, HttpContext)
     */
    @Override
    protected HttpContext createHttpContext() {
        // it is not the perfect place to do this, but it is the earliest place to do so
        Thread.currentThread().setName("WbHttpClient");

        return super.createHttpContext();
    }

    @Override
    protected HttpParams determineParams(HttpRequest req) {
        startTrace(req);
        return super.determineParams(req);
    }

    private void startTrace(final HttpRequest req) {
        String url;
        if (req instanceof HttpRequestBase) {
            url = ((HttpRequestBase) req).getURI().toString();
        } else {
            url = req.getRequestLine().getUri();
        }

        tracer.startTrace(url);
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        getParams().setParameter(ClientPNames.CONNECTION_MANAGER_FACTORY, new WbTraceConnFactory());
        return super.createClientConnectionManager();
    }

    @Override
    protected HttpParams createHttpParams() {
        HttpParams params = super.createHttpParams();
        HttpProtocolParams.setUserAgent(params, "WbHttpClient/" + VERSION);
        return params;
    }

    @Override
    protected HttpRequestExecutor createRequestExecutor() {
        return new WbHttpRequestExecutor();
    }

    //////////////////////////////////////////////////////////////

    class WbHttpRequestExecutor extends HttpRequestExecutor {
        public WbHttpRequestExecutor() {
            super();
        }

        @Override
        public void postProcess(final HttpResponse response, final HttpProcessor processor, final HttpContext context) throws HttpException, IOException {
            super.postProcess(response, processor, context);
            tracer.endTrace();
        }
    }

    class WbTraceConnFactory implements ClientConnectionManagerFactory {
        @Override
        public ClientConnectionManager newInstance(final HttpParams params, final SchemeRegistry schemeRegistry) {
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", getDefaultWbSSLFactory(), 443));

            return new WbTraceConnManager(params, registry);
        }
    }

    static SSLSocketFactory wbSSLFactory = null;

    private synchronized SSLSocketFactory getDefaultWbSSLFactory() {
        if (wbSSLFactory != null) {
            return wbSSLFactory;
        }

        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            wbSSLFactory = new WbTraceSSLSocketFactory(trustStore);
            wbSSLFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
//            Logger.e("init ssl trust store error", e);
        }
        return wbSSLFactory;
    }

    class WbTraceSSLSocketFactory extends SSLSocketFactory implements LayeredSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public WbTraceSSLSocketFactory(KeyStore trustStore) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
            super(trustStore);
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
            tracer.sslStart();
            Socket sslSocket = sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
            tracer.sslEnd();
            return sslSocket;
        }

        @Override
        public Socket createSocket() throws IOException {
            tracer.sslStart();
            Socket sslSocket = sslContext.getSocketFactory().createSocket();
            tracer.sslEnd();
            return sslSocket;
        }

    }

    class WbTraceConnManager extends SingleClientConnManager implements ClientConnectionManager {
        public WbTraceConnManager(final HttpParams params, final SchemeRegistry schemeRegistry) {
            super(params, schemeRegistry);
        }

        @Override
        protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
            return new WbTraceClientConnectionOperator(schreg);
        }
    }

    class WbTraceClientConnectionOperator extends DefaultClientConnectionOperator implements ClientConnectionOperator {
        public WbTraceClientConnectionOperator(SchemeRegistry schreg) {
            super(schreg);
        }

        @Override
        public OperatedClientConnection createConnection() {
            return new WbTraceClientConnection();
        }

        @Override
        public void openConnection(OperatedClientConnection conn, HttpHost target, InetAddress local, HttpContext context, HttpParams params) throws IOException {
            // do this before it really needs, for timing
            tracer.dnsStart();
            InetAddress[] addresses = InetAddress.getAllByName(target.getHostName());
            tracer.dnsEnd();

            super.openConnection(conn, target, local, context, params);
        }
    }

    class WbTraceClientConnection extends DefaultClientConnection implements OperatedClientConnection {

        @Override
        public void opening(Socket sock, HttpHost target) throws IOException {
            tracer.tcpStart();
            super.opening(sock, target);
        }

        @Override
        public void openCompleted(boolean secure, HttpParams params) throws IOException {
            super.openCompleted(secure, params);
            tracer.tcpEnd();
        }

        @Override
        public void shutdown() throws IOException {
            super.shutdown();
        }

        @Override
        public void close() throws IOException {
            super.close();
        }

        @Override
        protected void bind(final Socket socket, final HttpParams params) throws IOException {
            super.bind(socket, params);
        }

        @Override
        public void update(Socket sock, HttpHost target, boolean secure, HttpParams params) throws IOException {
            super.update(sock, target, secure, params);
        }

        @Override
        public void flush() throws IOException {
            super.flush();
        }

        @Override
        public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
            // it is not the perfect place to do this, but is a good one
            String ip = getLocalAddress() + ":" + getLocalPort() + "->" + getRemoteAddress() + ":" +
                    getRemotePort();
            tracer.setIP(ip);

            tracer.requestStart();
            super.sendRequestHeader(request);
            tracer.requestEnd();
        }

        @Override
        public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
            tracer.requestBodyStart();
            super.sendRequestEntity(request);
            tracer.requestEnd();
        }

        @Override
        public HttpResponse receiveResponseHeader() throws HttpException, IOException {
            tracer.responseStart();
            HttpResponse result = super.receiveResponseHeader();
            // in case client discard the body
            tracer.responseEnd();
            return result;
        }

        @Override
        public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
            tracer.responseBodyStart();
            super.receiveResponseEntity(response);
            tracer.recordSize(response.getEntity().getContentLength());

            tracer.responseEnd();
        }
    }

    //////////////////////////////////////////////////////////////

    // do the real job: track performance time, and post to server in background

    class TraceInfo implements Comparable {
        String url;
        String ip;
        String phone;

        // All time with -"Start" suffix below, are related to startTimestamp, not 1970.1.1
        long startTimestamp = 0L;
        long duration = 0L;

        // DNS
        long dnsStart = 0L;
        long dnsDuration = 0L;

        // SSL
        long sslStart = 0L;
        long sslDuration = 0L;

        // TCP
        long tcpStart = 0L;
        long tcpDuration = 0L;

        // Request write
        long requestStart = 0L;
        long requestDuration = 0L;

        long requestHeaderStart = 0L;
        long requestHeaderDuration = 0L;

        long requestBodyStart = 0L;
        long requestBodyDuration = 0L;

        // wait
        long waitForFirstByteStart = 0L;
        long waitForFirstByteDuration = 0L;

        // Response Read = first byte to last byte, not include wait time
        long responseStart = 0L;
        long responseDuration = 0L;

        long responseHeaderStart = 0L;
        long responseHeaderDuration = 0L;

        long responseBodyStart = 0L;
        long responseBodyDuration = 0L;
        
        long entitySize = 0L;
        
        int  gzip = 0;

        // must update every http call
        Map<String, String> deviceInfo = new HashMap<String, String>();

        public TraceInfo(String url) {
            this.url = url;
            startTimestamp = System.currentTimeMillis();
        }

        public String toParamString() {
            String s = toString();
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (Exception e) {
                return s;
            }
        }

        public String toString() {
        	StringBuilder sb = new StringBuilder();
        	sb.append("d"+dnsDuration);
        	sb.append("c"+tcpDuration);
        	sb.append("f"+waitForFirstByteDuration);
        	sb.append("t"+responseDuration);
        	sb.append("s"+entitySize);
        	sb.append("z"+"");
        	
        	return sb.toString();
        }
        
//        public String toString() {
//            StringBuilder sb = new StringBuilder();
//            sb.append("WbTrace{" + url + "(" + ip + ")");
//            sb.append(",all[" + startTimestamp + "," + duration + "]");
//            sb.append(",dns[" + dnsStart + "," + dnsDuration + "]");
//            sb.append(",ssl[" + sslStart + "," + sslDuration + "]");
//            sb.append(",tcp[" + tcpStart + "," + tcpDuration + "]");
//            sb.append(",req[" + requestStart + "," + requestDuration + "(");
//            sb.append(requestHeaderStart + "," + requestHeaderDuration + ")(");
//            sb.append(requestBodyStart + "," + requestBodyDuration + ")]");
//            sb.append(",wat[" + waitForFirstByteStart + "," + waitForFirstByteDuration + "]");
//            sb.append(",rsp[" + responseStart + "," + responseDuration + "(");
//            sb.append(responseHeaderStart + "," + responseHeaderDuration + ")(");
//            sb.append(responseBodyStart + "," + responseBodyDuration + ")]");
//            sb.append("}");
//            sb.append(deviceInfo.toString());
//            return sb.toString();
//        }

        @Override
        public int compareTo(final Object o) {
            if (!(o instanceof TraceInfo)) {
                return 0;
            }

            TraceInfo ti = (TraceInfo) o;
            if (startTimestamp > ti.startTimestamp) {
                return 1;
            } else if (startTimestamp < ti.startTimestamp) {
                return -1;
            }

            return 0;
        }
    }

    // we use url for current traces' key, that means we do NOT support
    // multi calls to the same url parallel from client.
    // if that happens, our current trace data will be ruined !
    class WbTracer {
        //String traceServerUrl = "http://api.weibo.com/2/proxy/perf/sync.json";
        String traceServerUrl = "http://10.73.89.90:8080/2/proxy/perf/sync.json";

        Map<String, TraceInfo> currentTraces = new ConcurrentHashMap<String, TraceInfo>();
        Set<TraceInfo> traces = new HashSet<TraceInfo>();
        ThreadLocal<String> currentUrl = new ThreadLocal<String>();

        void startTrace(String url) {
            TraceInfo ti = new TraceInfo(url);
            currentUrl.set(url);

            TraceInfo last = currentTraces.put(url, ti);
            if (last != null) {
                // log error!
                synchronized (traces) {
                    traces.add(last);
                }
            }
            // TODO add deviceinfo here
            //
            // TODO call PhoneUtils.setGlobalContext from app onCreate()
//            ti.phone = PhoneUtils.getPhoneId();
        }

        void setIP(String ip) {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.ip = ip;
        }

        void dnsStart() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.dnsStart = System.currentTimeMillis() - ti.startTimestamp;
        }

        void dnsEnd() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.dnsDuration = System.currentTimeMillis() - ti.startTimestamp - ti.dnsStart;
        }

        void tcpStart() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.tcpStart = System.currentTimeMillis() - ti.startTimestamp;
        }

        void tcpEnd() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.tcpDuration = System.currentTimeMillis() - ti.startTimestamp - ti.tcpStart;
        }

        void sslStart() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.sslStart = System.currentTimeMillis() - ti.startTimestamp;
        }

        void sslEnd() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.sslDuration = System.currentTimeMillis() - ti.startTimestamp - ti.sslStart;
        }

        void requestStart() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.requestStart = System.currentTimeMillis() - ti.startTimestamp;
            ti.requestHeaderStart = ti.requestStart;
        }

        void requestBodyStart() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.requestHeaderDuration = System.currentTimeMillis() - ti.startTimestamp - ti.requestHeaderStart;
            ti.requestBodyStart = System.currentTimeMillis() - ti.startTimestamp;
        }

        void requestEnd() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.requestDuration = System.currentTimeMillis() - ti.startTimestamp - ti.requestStart;
            if (ti.requestHeaderDuration <= 0) {
                ti.requestHeaderDuration = ti.requestDuration;
            }
            if (ti.requestBodyStart > 0) {
                ti.requestBodyDuration = System.currentTimeMillis() - ti.startTimestamp - ti.requestBodyStart;
            }

            ti.waitForFirstByteStart = System.currentTimeMillis() - ti.startTimestamp;
        }

        void responseStart() {

            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.responseStart = System.currentTimeMillis() - ti.startTimestamp;
            ti.responseHeaderStart = ti.responseStart;

            ti.waitForFirstByteDuration = ti.responseStart - ti.waitForFirstByteStart;
        }

        void responseBodyStart() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.responseHeaderDuration = System.currentTimeMillis() - ti.startTimestamp - ti.responseHeaderStart;
            ti.responseBodyStart = System.currentTimeMillis() - ti.startTimestamp;
        }

        void responseEnd() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.get(url);
            ti.responseDuration = System.currentTimeMillis() - ti.startTimestamp - ti.responseStart;

            if (ti.responseHeaderDuration <= 0) {
                ti.responseHeaderDuration = ti.responseDuration;
            }
            if (ti.responseBodyStart > 0) {
                ti.responseBodyDuration = System.currentTimeMillis() - ti.startTimestamp - ti.responseBodyStart;
            }
        }

        void recordSize(long size) {
        	String url = currentUrl.get();
        	TraceInfo ti = currentTraces.get(url);
        	ti.entitySize = size;
        } 
        
        void recordGzip(int gzip) {
        	String url = currentUrl.get();
        	TraceInfo ti = currentTraces.get(url);
        	ti.gzip = gzip;
        } 
        
        void endTrace() {
            String url = currentUrl.get();
            TraceInfo ti = currentTraces.remove(url);
            ti.duration = System.currentTimeMillis() - ti.startTimestamp;

            // do not trace the "syncTrace" calls
            if (url.contains(traceServerUrl)) {
                return;
            }

            lastTraceInfo = ti.toString();

            synchronized (traces) {
                traces.add(ti);
            }
        }

        // post data to traceServer
        void syncTraces() {
            List<TraceInfo> oldtraces = new ArrayList<TraceInfo>();
            synchronized (traces) {
                oldtraces.addAll(traces);
                traces.clear();
            }

//            Collections.sort(oldtraces);
//            for (TraceInfo ti : oldtraces) {
//                syncTrace(ti);
//            }
        }

        void syncTrace(final TraceInfo ti) {
            String url = traceServerUrl + "?t=" + ti.toParamString();
            HttpGet traceGet = new HttpGet(url);
            try {
                HttpResponse response = execute(traceGet);
                int retCode = response.getStatusLine().getStatusCode();
                if (retCode == HttpStatus.SC_OK || retCode == HttpStatus.SC_NOT_FOUND) {
                    // it is ok to get a 404, because all we need is a access log
                } else {
//                    Logger.warn("sync result error: " + response.getStatusLine().getReasonPhrase());
                }
                response.getEntity().consumeContent();
            } catch (IOException e) {
//                Logger.error("sync error: " + ti, e);
            }
        }

  
        public String toString() {
            List<TraceInfo> oldtraces = new ArrayList<TraceInfo>();
            synchronized (traces) {
                oldtraces.addAll(traces);
                traces.clear();
            }

            Collections.sort(oldtraces);

            StringBuilder sb = new StringBuilder();

            for (TraceInfo ti : oldtraces) {
                sb.append(ti + "\n");
            }
            return sb.toString();
        }
    }

    public void setDoTrackTime(boolean doTrackTime) {
        this.doTrackTime = doTrackTime;
        onChangeDoTrackTime();
    }

    private void onChangeDoTrackTime() {
        if (backgroundThread == null) {
            backgroundThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            trackTime();
                            Thread.sleep(2000);
                        }
                    } catch (InterruptedException e) {
                        // log info
                        return;
                    } catch (Throwable t) {
                        // log error
                        return;
                    }
                }
            }, "WbHttpClient-background-thread"
            );
            backgroundThread.setDaemon(true);
        }

        if (doTrackTime) {
            backgroundThread.start();
        } else {
            backgroundThread.interrupt();
        }
    }

    // do post log to server
    private void trackTime() {
        if (postOnlyWifi) {
            //            if (!PhoneUtils.isWIFI()) {
            //                return;
            //            }
        }

        tracer.syncTraces();
    }

    private static String readStream(InputStream inputStream) throws IOException {
        InputStreamReader is = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(is);
        String read = null;
        StringBuffer sb = new StringBuffer();
        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        String result = sb.toString();

        return result;
    }

    /**
     * output like:
     * <p/>
     * WbTrace{https://api.weibo.com/2/(/10.229.13.116:61681->/123.125.106.226:443),all[1400046519215,559],
     * dns[38,32],ssl[77,352],tcp[429,40],req[498,3(498,3)(0,0)],wat[501,45],rsp[546,13(546,11)(557,2)]}{}
     * <p/>
     * WbTrace{https://api.weibo.com/2/(/10.229.13.116:61681->/123.125.106.226:443),all[1400046528797,7],
     * dns[0,0],ssl[0,0],tcp[0,0],req[2,0(2,0)(0,0)],wat[2,0],rsp[2,5(2,5)(7,0)]}{}
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
//        String url = "https://api.weibo.com/proxy/echo?access_token=2.00DtpjEDBOC92Ec275a913a6MfCzgC";
       System.out.println("ok");
    	String url = "http://hot.weibo.com/";
    	
        WbHttpClient client = new WbHttpClient();
//        client.setDoTrackTime(true);

//        for (int i = 1; i <= 20; ++i) {
            String result = client.getURL(url);
//            System.out.println(i);

//            Thread.sleep(500);
//        }

        System.out.println(client.tracer);
    }


}
