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

			dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			try {
				unixtime = dfm.parse(data).getTime();
			} catch (ParseException e) {
				System.out.println("Impossivel converter a data: "+data+" para o formato timestamp");
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
	
	public static String dataEscritaParaNumerica(String mes){
		switch (mes) {
		case "Janeiro":
			mes = "01";
			break;
		case "Fevereiro":
			mes = "02";
			break;
		case "Março":
			mes = "03";
			break;
		case "Abril":
			mes = "04";
			break;
		case "Maio":
			mes = "05";
			break;
		case "Junho":
			mes = "06";
			break;
		case "Julho":
			mes = "07";
			break;
		case "Agosto":
			mes = "08";
			break;
		case "Setembro":
			mes = "09";
			break;
		case "Outubro":
			mes = "10";
			break;
		case "Novembro":
			mes = "11";
			break;
		case "Dezembro":
			mes = "12";
			break;
		}
		return mes;
	}

}
