package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.caelum.vraptor.ioc.Component;
import crawler.Informacao;

@Component
public class InformacoesDAO {

	private final Session session;
	
	public InformacoesDAO(Session session) {
		this.session = session;
	}
	
	public void inserir(Informacao info) {
		Transaction tx = session.beginTransaction();
		session.save(info);
		tx.commit();
	}
	
}
