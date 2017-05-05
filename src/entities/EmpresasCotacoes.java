package entities;

import java.util.List;
import java.util.Map;

public class EmpresasCotacoes {

	private Empresas empresa;
	private Map<String,List<Cotacoes>> cotacoes;
	private List<Noticias> noticias;
	
	public EmpresasCotacoes(Empresas empresa, Map<String, List<Cotacoes>> cotacoes, 
			List<Noticias> noticias) {
		this.empresa = empresa;
		this.cotacoes = cotacoes;
		this.noticias = noticias;
	}
	
	

	public List<Noticias> getNoticias() {
		return noticias;
	}



	public void setNoticias(List<Noticias> noticias) {
		this.noticias = noticias;
	}



	public Empresas getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresas empresa) {
		this.empresa = empresa;
	}

	public Map<String, List<Cotacoes>> getCotacoes() {
		return cotacoes;
	}

	public void setCotacoes(Map<String, List<Cotacoes>> cotacoes) {
		this.cotacoes = cotacoes;
	}
	
}
