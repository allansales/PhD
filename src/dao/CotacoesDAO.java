package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;
import entities.Cotacoes;

@Component
public class CotacoesDAO {

	private final Session session;

	public CotacoesDAO(Session session) {
		this.session = session;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Cotacoes> busca(String codigoNegociacao) {
		return session.createCriteria(Cotacoes.class).setProjection(Projections.projectionList()
				.add(Projections.property("data_pregao"))
				.add(Projections.property("preco_medio"))
				.add(Projections.property("total_negocios"))
				)
			      .add(Restrictions.eq("cod_negociacao", codigoNegociacao))
			      .addOrder(Order.asc("data_pregao"))
			      .list();
	}
}
