package br.gov.mj.sislegis.app.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@XmlRootElement
@JsonIgnoreProperties({ "idProposicao" })
public class EncaminhamentoProposicao extends AbstractEntity {

	private static final long serialVersionUID = 7949894944142814382L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	private String detalhes;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Comentario comentario;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Comentario comentarioFinalizacao;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tipo_encaminhamento_id")
	private TipoEncaminhamento tipoEncaminhamento;

	@ManyToOne(fetch = FetchType.EAGER)
	private Proposicao proposicao;

	@OneToOne(fetch = FetchType.EAGER)
	private Usuario responsavel;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataHoraLimite;

	private Boolean finalizado;

	public Comentario getComentario() {
		return comentario;
	}

	public void setComentario(Comentario comentario) {
		this.comentario = comentario;
	}

	public Comentario getComentarioFinalizacao() {
		return comentarioFinalizacao;
	}

	public void setComentarioFinalizacao(Comentario comentarioFinalizacao) {
		this.comentarioFinalizacao = comentarioFinalizacao;
	}

	public TipoEncaminhamento getTipoEncaminhamento() {
		return tipoEncaminhamento;
	}

	public void setTipoEncaminhamento(TipoEncaminhamento tipoEncaminhamento) {
		this.tipoEncaminhamento = tipoEncaminhamento;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(String descricao) {
		this.detalhes = descricao;
	}

	public Proposicao getProposicao() {
		if (!Objects.isNull(this.proposicao)) {
			Proposicao p = new Proposicao();
			p.setId(proposicao.getId());
			this.proposicao = p;
		}
		return proposicao;
	}

	public void setProposicao(Proposicao proposicao) {
		this.proposicao = proposicao;
	}

	public Usuario getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Usuario responsavel) {
		this.responsavel = responsavel;
	}

	public Date getDataHoraLimite() {
		return dataHoraLimite;
	}

	public void setDataHoraLimite(Date dataHoraLimite) {
		this.dataHoraLimite = dataHoraLimite;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (id != null)
			result += "id: " + id;
		if (comentario != null)
			result += ", comentario: " + comentario;
		if (tipoEncaminhamento != null)
			result += ", tipoEncaminhamento: " + tipoEncaminhamento;
		if (proposicao != null)
			result += ", proposicao: " + proposicao;
		if (responsavel != null)
			result += ", responsavel: " + responsavel;
		if (dataHoraLimite != null)
			result += ", dataHoraLimite: " + dataHoraLimite;
		return result;
	}

	public Boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(Boolean finalizado) {
		this.finalizado = finalizado;
	}
}
