package com.wang.mina.httpgzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.wang.util.WangLogger;

/**
 * A {@link MessageEncoder} that encodes {@link HttpResponseMessage}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class HttpResponseEncoder implements MessageEncoder {
  private static final Set<Class<?>> TYPES;

  static {
    Set<Class<?>> types = new HashSet<Class<?>>();
    types.add(HttpResponseMessage.class);
    TYPES = Collections.unmodifiableSet(types);
  }

  private static final byte[] CRLF = new byte[] {0x0D, 0x0A};

  public HttpResponseEncoder() {}

  public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

    HttpResponseMessage msg = (HttpResponseMessage) message;
    try {
      ByteBuffer buf = ByteBuffer.allocate(1024);
      // Enable auto-expand for easier encoding
      buf.setAutoExpand(true);

      try {
    	  
    	if (msg.getDataType()==HttpResponseMessage.DATA_TYPE_NORMAL ||
    	    msg.getDataType()==HttpResponseMessage.DATA_TYPE_HEADER) {
        // output all headers except the content length
        CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
        buf.putString("HTTP/1.1 ", encoder);
        buf.putString(String.valueOf(msg.getResponseCode()), encoder);
        switch (msg.getResponseCode()) {
          case HttpResponseMessage.HTTP_STATUS_SUCCESS:
            buf.putString(" OK", encoder);
            break;
          case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
            buf.putString(" Not Found", encoder);
            break;
        }
        buf.put(CRLF);
        Iterator<Entry<String, String>> it = msg.getHeaders().entrySet().iterator();
        while (it.hasNext()) {
          Entry<String, String> entry = it.next();
          buf.putString((String) entry.getKey(), encoder);
          buf.putString(": ", encoder);
          buf.putString((String) entry.getValue(), encoder);
          buf.put(CRLF);
        }
        // now the content length is the body length
        buf.putString("Content-Length: ", encoder);
        if (msg.getTransferredFile() == null) {
            if (msg.getDataType()==msg.DATA_TYPE_NORMAL) {
        	    buf.putString(String.valueOf(msg.getBodyLength()), encoder);
            }else{
            	buf.putString(String.valueOf(Long.MAX_VALUE), encoder);
            }
        } else {
          buf.putString(String.valueOf(msg.getTransferredFile().length()), encoder);
        }
        buf.put(CRLF);
        buf.put(CRLF);

      }
      }catch (CharacterCodingException ex) {
        ex.printStackTrace();
      }

       if ( msg.getDataType() == HttpResponseMessage.DATA_TYPE_BODY) {
           ByteBuffer bodyBuffer = gzip(msg.getBody());
           buf.put(bodyBuffer);
           buf.put((byte)'\r');
           buf.put((byte)'\n');
           buf.flip();
           bodyBuffer.clear();
           out.write(buf);
           out.flush();
       }else if ( msg.getDataType()==HttpResponseMessage.DATA_TYPE_NORMAL ) {
            ByteBuffer bodyBuffer = gzip(msg.getBody());
            buf.put(bodyBuffer);
            buf.flip();
            bodyBuffer.clear();
            out.write(buf);
            out.flush();
        }else if ( msg.getDataType()==HttpResponseMessage.DATA_TYPE_TAIL ){
            ByteBuffer tailBuffer = ByteBuffer.allocate(5);
        	
            tailBuffer.put((byte)17);
        	tailBuffer.put((byte)'\r');
            tailBuffer.put((byte)'\n');
        	tailBuffer.put((byte)'\r');
            tailBuffer.put((byte)'\n');
            tailBuffer.flip();
            buf.put(tailBuffer);
            buf.flip();
            tailBuffer.clear();
            out.write(buf);
            out.flush();
        }else{
            buf.flip();
            out.write(buf);
            out.flush();
        }
//      }
    } finally {
      msg.clear();
    }
  }

  public Set<Class<?>> getMessageTypes() {
    return TYPES;
  }
  
  private ByteBuffer gzip(ByteBuffer byteBuffer) {  
	  
	  int beforeSize = byteBuffer.limit();
	  ByteBuffer resultBuffer = ByteBuffer.allocate(byteBuffer.limit()/2);
	  resultBuffer.setAutoExpand(true);
	  try{
	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      GZIPOutputStream gzip = new GZIPOutputStream(bos);
	      
	      int len = byteBuffer.limit();
	      byte[] byteArray = new byte[len];
	      byteBuffer.get(byteArray, 0, len);
	      gzip.write( byteArray );
	      gzip.finish();
	      gzip.close();
	      
	      resultBuffer.put(bos.toByteArray());
	      resultBuffer.flip();
	      bos.close();
	  }catch(IOException e) {
		  WangLogger.error("gzip error in HttpResponseEncoder" + e);
	  }
	  
	  int afterSize = resultBuffer.limit();
	  return resultBuffer;
	  
	  
  }
  
}