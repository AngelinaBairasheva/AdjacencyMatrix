import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class Parser
{
	public Parser(){}
	
	public void getMatrix(String url, int count) throws IOException{
		int[][] matrix = new int[count][count];
		HashMap<String, Integer> map = new HashMap<>();
		map.put(url, 0);
		
		int c = 1;
	 /* Start of Fix SSL-Connection*/
		TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
			public void checkClientTrusted(X509Certificate[] certs, String authType) { }
			public void checkServerTrusted(X509Certificate[] certs, String authType) { }

		} };

		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) { return true; }
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        /* End of the fix SSL-Connection*/
		for(int i = 0; i < count; i++){
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");

			for (Element link : links) {
				String href = link.attr("abs:href");
				if(map.size() < count && !map.containsKey(href)) map.put(href, c++);
				if(map.get(href) != null) matrix[i][map.get(href)] = 1;
			}



			url = getKeyByValue(map, i + 1);
		}

		try(FileWriter writer = new FileWriter("D:\\OIP\\MyJavaConsoleApp\\matrix.txt", false)){
			for(int i = 0; i < count; i++){
				for(int j = 0; j < count; j++){
					writer.write(matrix[i][j] + " ");
				}
				writer.write('\n');
			}

			for(Map.Entry<String, Integer> pair : map.entrySet()){
				writer.write(pair.getKey() + " " + pair.getValue() + "\n");
			}
		}
	}

	private String getKeyByValue(HashMap<String, Integer> map, int value){
		for(Map.Entry<String, Integer> pair : map.entrySet()){
			if( pair.getValue() == value) return pair.getKey();
		}

		return null;
	}
}
