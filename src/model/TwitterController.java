package model;

import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import crawler.twitter.search.TwitterSearch;
import dao.EmpresaDAO;
import dao.InformacoesDAO;

@Resource
public class TwitterController {

	private final InformacoesDAO infDAO;
	private final EmpresaDAO empDAO;


	public TwitterController(InformacoesDAO infDAO, EmpresaDAO empDAO){
		this.infDAO = infDAO;
		this.empDAO = empDAO;
	}

	@Get("/twitter/inserir/")
	public void inserirTwitters(){

		String searchDateStart = "01/01/2010";
		String searchDateFinish = "31/12/2013";
		
		TwitterSearch search = new TwitterSearch();

		//for para todos os nomes acoes
		List<String> empresas = empDAO.listaMacroGrupos("nome_empresa");
		for (String empresa : empresas) {
			search.insereInformacao(searchDateStart,searchDateFinish, "ação", empresa, infDAO);
		}
		
		//for para todos os setores
		List<String> setores = empDAO.listaMacroGrupos("setor");
		for (String setors : setores) {
			search.insereInformacao(searchDateStart,searchDateFinish, "setor", setors, infDAO);

		}
		
		//for para todos os subsetores
		List<String> subSetores = empDAO.listaMacroGrupos("sub_setor");
		for (String subSetrs : subSetores) {
			search.insereInformacao(searchDateStart,searchDateFinish, "sub_setor", subSetrs, infDAO);
		}
		
		//for para todos os segmentos
		List<String> segmentos = empDAO.listaMacroGrupos("segmento");
		for (String segm : segmentos) {
			search.insereInformacao(searchDateStart,searchDateFinish, "segmento", segm, infDAO);
		}

	}
}
