package stocks.client;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Util {

	public static String SERVER_ADDRESS = "https://2efbb796.ngrok.io/stocks.poc/stocks";
	public static String username;
	public static String accesskey;
	
	public static Map<String, String> sendRequest(Map<String, String> params) {
		Map<String, String> response = new HashMap<>();
		try {
			if (accesskey != null)
				params.put("key", accesskey);
			Document d = Jsoup.connect(SERVER_ADDRESS).data(params).post();
			String data = d.getElementsByTag("body").text().trim();
			
			if (data.equals("Invalid Action") || data.isEmpty()) return response;
			String[] split = data.split("&");
			for (String s: split) {
				String[] param = s.split("=");
				response.put(param[0], URLDecoder.decode(param[1], "UTF-8"));
			}
			return response;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "You or the server is experiencing issues. Your request was not sent properly.", "Error", JOptionPane.ERROR_MESSAGE);
			return response;
		}
	}
	
}
