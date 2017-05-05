package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;
import dao.CotacoesDAO;
import dao.EmpresaDAO;
import dao.NoticiasDAO;
import entities.Cotacoes;
import entities.Empresas;
import entities.EmpresasCotacoes;
import entities.Noticias;

@Resource
public class EmpresasController {

	private final EmpresaDAO empDAO;
	private final CotacoesDAO cotaDAO;
	private final NoticiasDAO noticDAO;

	private final Result result;

	public EmpresasController(EmpresaDAO empDAO, CotacoesDAO cotaDAO, 
			NoticiasDAO noticDAO, Result result) {
		this.empDAO = empDAO;
		this.cotaDAO = cotaDAO;
		this.noticDAO = noticDAO;
		this.result = result;
	}

	public List<Empresas> busca(String nome) {
		return empDAO.busca(nome);
	}

	@Get("/produtos/busca.json")
	public void buscaJson(String busca) {
		result.use(Results.json()).withoutRoot().from(empDAO.busca(busca)).serialize();
	}

	@Get("/acoes/grupos/")
	public void getAcoes(String s,String q){

		List<Empresas> empresas = empDAO.buscaAcoes(s, q);
		jsonCotacoes(empresas);

	}

	@Get("/acoes")
	public void getAcoes(String q){
		List<Empresas> empresas = empDAO.getEmpresaNome(q);
		jsonCotacoes(empresas);
	}
	
	private void jsonCotacoes(List<Empresas> empresas){
		
		List<EmpresasCotacoes> empresasCotacoes = new ArrayList<EmpresasCotacoes>();

		for (Empresas empresa : empresas) {

			Map<String, List<Cotacoes>> cotacoes = new HashMap<String, List<Cotacoes>>();
			String[] codigos = getCodigosNegociacao(empresa);
			List<Noticias> noticias = noticDAO.busca(empresa.getCnpj());
			
			for (String codigo : codigos) {
				List<Cotacoes> cotacs = cotaDAO.busca(codigo);
				cotacoes.put(codigo, cotacs);
			}
			
			empresasCotacoes.add(new EmpresasCotacoes(empresa, cotacoes,noticias));

		}
		result.use(Results.json()).withoutRoot().from(empresasCotacoes)
		.include("empresa").include("cotacoes").include("noticias").serialize();
	}

	private String[] getCodigosNegociacao(Empresas empresa){

		String cods = empresa.getCod_negociacao();
		String codigosNegoci[] = cods.split(",");
		return codigosNegoci;
	}

	public void stocks(){

		result.include("setoresList", empDAO.listaMacroGrupos("setor"));
		result.include("subSetoresList", empDAO.listaMacroGrupos("sub_setor"));
		result.include("segmentosList", empDAO.listaMacroGrupos("segmento"));

	}

}
