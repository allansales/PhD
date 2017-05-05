package crawler.noticias;

public class Comentario {
	
	private String idNoticia;
	private String comentario;
	private String idUsuario;
	private String votosThumbsUp;
	private String votosThumbsDown;
	private String quantidade_respostas;
	
	public Comentario(){
		
	}
	
	public Comentario(String idNoticia, String comentario, String idUsuario, String votosThumbsUp, String votosThumbsDown, String quantidade_respostas){
		setIdNoticia(idNoticia);
		setComentario(comentario);
		setIdUsuario(idUsuario);
		setVotosThumbsUp(votosThumbsUp);
		setVotosThumbsDown(votosThumbsDown);
		setQuantidade_respostas(quantidade_respostas);
	}
	
	public String getIdNoticia() {
		return idNoticia;
	}
	public void setIdNoticia(String idNoticia) {
		this.idNoticia = idNoticia;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public String getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getVotosThumbsUp() {
		return votosThumbsUp;
	}
	public void setVotosThumbsUp(String votosThumbsUp) {
		this.votosThumbsUp = votosThumbsUp;
	}
	public String getVotosThumbsDown() {
		return votosThumbsDown;
	}
	public void setVotosThumbsDown(String votosThumbsDown) {
		this.votosThumbsDown = votosThumbsDown;
	}

	public String getQuantidade_respostas() {
		return quantidade_respostas;
	}

	public void setQuantidade_respostas(String quantidade_respostas) {
		this.quantidade_respostas = quantidade_respostas;
	}
	
}
