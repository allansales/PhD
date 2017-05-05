package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;
import entities.Empresas;

@Component
public class EmpresaDAO {

	private final Session session;

	public EmpresaDAO(Session session) {
		this.session = session;
	}

	public void salva(Empresas empresa){
		Transaction tx = session.beginTransaction();
		session.save(empresa);
		tx.commit();

	}

	public Empresas getEmpresa(String cnpj){
		Empresas empresa = (Empresas) session.load(Empresas.class, cnpj);
		return empresa;
	}
	
	public List<String> listaMacroGrupos(String tipo){
		
		@SuppressWarnings("unchecked")
		List<String> grupo = session.createCriteria(Empresas.class)
			    .setProjection( Projections.projectionList()
			        .add(Projections.groupProperty(tipo) )
			    )
			    .list();
		return grupo;
		
	}

	@SuppressWarnings("unchecked")
	public List<Empresas> busca(String nome) {
		return session.createCriteria(Empresas.class).setProjection(Projections.property("nome_empresa"))
			      .add(Restrictions.ilike("nome_empresa", nome, MatchMode.ANYWHERE))
			      .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Empresas> getEmpresaNome(String nome){
		return  session.createCriteria(Empresas.class)
			      .addOrder( Order.desc("setor"))
			      .addOrder( Order.desc("sub_setor"))
			      .addOrder( Order.desc("segmento"))
			      .addOrder( Order.desc("atividade_principal"))
			      .addOrder( Order.desc("nome_empresa"))
			      .add(Restrictions.ilike("nome_empresa", nome, MatchMode.ANYWHERE))
			      .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Empresas> buscaAcoes(String granularidade, String nome){

		return  session.createCriteria(Empresas.class)
			      .addOrder( Order.desc("setor"))
			      .addOrder( Order.desc("sub_setor"))
			      .addOrder( Order.desc("segmento"))
			      .addOrder( Order.desc("atividade_principal"))
			      .addOrder( Order.desc("nome_empresa"))
			      .add(Restrictions.ilike(granularidade, nome, MatchMode.ANYWHERE))
			      .list();
	
	}
	

}
