package stocks.web;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class MundoDAO {

	public List<String> paises() {
		 List<String> result = new ArrayList<String>();
		 result.add("Brasil");
		 result.add("Portugal");
		 result.add("Japão");
		 result.add("Canadá");
		 result.add("Paraguai");
		 return result;
	}
}
