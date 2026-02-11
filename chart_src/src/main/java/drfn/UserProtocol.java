package drfn;

import java.util.Hashtable;

/**
 * 차트 API 인터페이스. <h3><b>UserProtocol</b></h3></br>
 *
 * @author lyk525@drfn.co.kr
 * @date 2012. 4. 4.
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserProtocol {
	abstract public void requestInfo(int tag, Hashtable<String, Object> data);
}