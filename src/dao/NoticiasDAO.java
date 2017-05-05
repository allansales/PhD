package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;
import entities.Noticias;

@Component
public class NoticiasDAO {

	private final Session session;
	private final int QTD = 20;
	
	public NoticiasDAO(Session session) {
		this.session = session;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Noticias> busca(String cnpj) {
		return session.createCriteria(Noticias.class)
			      .add(Restrictions.eq("cnpj", cnpj))
			      .addOrder(Order.desc("data_noticia"))
			      .setMaxResults(QTD)
			      .list();
	}
}
