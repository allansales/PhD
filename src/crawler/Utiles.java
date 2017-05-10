package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.jsoup.nodes.Document;

public class Utiles {


	public final static long ZERO = 0;

	public static String formatoYYYYMMddHHmm(String data, String horaMinuto){
		String diaMesAno [] = data.split("/");
		String dia = diaMesAno[0];
		String mes = diaMesAno[1];
		String ano = diaMesAno[2];
		
		data = ano+mes+dia+horaMinuto;

		return data;

	}
	
	public static long dataToTimestamp(String data, String horaMinuto){

		long unixtime = ZERO;

		if(!data.isEmpty()){
			data = formatoYYYYMMddHHmm(data,horaMinuto);
			DateFormat dfm = new SimpleDateFormat("yyyyMMddHHmm");  

			dfm.setTimeZone(TimeZone.getTimeZone("GMT"));//Specify your timezone 

			try {
				unixtime = dfm.parse(data).getTime();
			} catch (ParseException e) {
				System.out.println("Impossivel converter a data: "+data+"para o formato timestamp");
			}  
			unixtime=unixtime/1000;
		}


		return unixtime;

	}

	public static String encondeConsulta(String consulta){

		try {
			return consulta = URLEncoder.encode(consulta,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Tipo de condificacao nao suportada pela consulta.");
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static String timesTampData(long unixTimestamp){
		//1386106407
		java.sql.Timestamp stamp = new java.sql.Timestamp((long)unixTimestamp*1000); 
		Date date = new Date(stamp.getTime()); 
		return date.toGMTString();
	}
	
	public static void writeFile(Document doc, String titulo){
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(titulo))) {
			String content = doc.toString();
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		System.out.println(dataToTimestamp("19/04/2007","0324"));
		
	}

}
