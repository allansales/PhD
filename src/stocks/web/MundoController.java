package stocks.web;

import java.util.List;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class MundoController {

	private final MundoDAO dao;
	private final Result result;

	public MundoController(MundoDAO dao, Result result) {
		this.dao = dao;
		this.result = result;
	}

	public List<String> lista() {
		return dao.paises();
	}
}
