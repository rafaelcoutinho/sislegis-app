package br.gov.mj.sislegis.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;

import br.gov.mj.sislegis.app.enumerated.Origem;
import br.gov.mj.sislegis.app.model.documentos.Briefing;
import br.gov.mj.sislegis.app.model.documentos.Emenda;
import br.gov.mj.sislegis.app.model.documentos.NotaTecnica;
import br.gov.mj.sislegis.app.model.pautacomissao.ProposicaoPautaComissao;
import br.gov.mj.sislegis.app.model.pautacomissao.ProposicaoPautaComissaoFutura;
import br.gov.mj.sislegis.app.rest.serializers.CompactListRoadmapComissaoSerializer;
import br.gov.mj.sislegis.app.rest.serializers.CompactSetProposicaoSerializer;
import br.gov.mj.sislegis.app.rest.serializers.EfetividadeSALDeserializer;
import br.gov.mj.sislegis.app.rest.serializers.ResultadoCongressoDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
//@formatter:off
@NamedNativeQueries({
  @NamedNativeQuery(
          name    =   "getAllProposicoesSeguidas",
          query   =   "SELECT * " +
                      "FROM Proposicao a where a.id in (select distinct proposicoesSeguidas_id from Usuario_ProposicaoSeguida)",
                      resultClass=Proposicao.class
  ),
  @NamedNativeQuery(
		  name = "listFromReuniao",
		  query="SELECT * FROM Proposicao p where  p.id in (select proposicao_id from reuniaoproposicao r where  r.reuniao_id=:rid)",
		  resultClass=Proposicao.class
		  
		  )
  
})

@NamedQueries({ 
	@NamedQuery(
			name = "findByUniques", 
			query = "select p from Proposicao p where p.idProposicao=:idProposicao and p.origem=:origem"),
	@NamedQuery(
				name = "findWithNotas", 
				query = "select p from Proposicao p where p.notatecnicas is not empty"),
	@NamedQuery(
				name = "findByPosicionamento", 
				query = "select p from Proposicao p where p.posicionamentoAtual.id=:id"),
	@NamedQuery(
			name = "findPautadas", 
			query = "select p from Proposicao p where p.ultima.pautaReuniaoComissao.data>:data")
	,
	@NamedQuery(
			name = "getAllProposicao4Usuario", 
			query = "select p from Proposicao p where p.responsavel.id=:userId"),
			@NamedQuery(
					name = "getAllProposicaoPosicionada4Usuario", 
					query = "select p from Proposicao p where p.posicionamentoAtual.usuario.id=:userId"),
	@NamedQuery(
			name = "findNaoPautadas",  
			query = "select p from Proposicao p where p.ultima is null"),
			@NamedQuery(
					name = "getAllProposicaoPosicionada4UsuarioPeriodo", 
					query = "select p from Proposicao p where p.responsavel.id=:userId and p.foiAtribuida>:s  and  p.foiAtribuida<=:e and p.foiAnalisada<=:e and p.foiAnalisada is not null and p.posicionamentoAtual.posicionamento=:posicionamento"),		
			
	@NamedQuery(
			name = "contadorPosicionamentosPorEquipe",  
			query = "select count(p.id),p.responsavel.equipe.nome,p.posicionamentoAtual.posicionamento.nome from Proposicao p where p.responsavel.equipe=:equipe and p.foiAnalisada>:s and p.foiAnalisada<=:e and p.posicionamentoAtual is not null group by p.responsavel.equipe.nome, p.posicionamentoAtual.posicionamento.nome"),
					
					
	@NamedQuery(
			name = "contadorPosicionamentosPorResponavel",  
			query = "select count(p.id),p.posicionamentoAtual.posicionamento.nome from Proposicao p where p.responsavel=:responsavel and p.foiAtribuida>:s and  p.foiAtribuida<=:e and p.foiAnalisada<=:e and p.foiAnalisada is not null and p.estado<>:estado group by p.posicionamentoAtual.posicionamento.nome"),
	
	@NamedQuery(
			name = "getAllProposicaoPosicionadaComResultado",  
			query = "select p from Proposicao p where p.resultadoCongresso is not null and p.resultadoCongresso<>:emTramitacao and p.teveResultado>:s and  p.teveResultado<=:e and p.posicionamentoAtual.posicionamento=:posicionamento "),
	@NamedQuery(
			name = "contadorResultadoCongressoPorPosicionamento",  
			query = "select count(p.id) from Proposicao p where p.teveResultado>:s and p.teveResultado<=:e and p.posicionamentoAtual.posicionamento=:posicionamento and p.resultadoCongresso=:resultadoCongresso"),
	@NamedQuery(
			name = "contadorEmendasComResultado",  
			query = "select count(p.id) from Proposicao p where p.teveResultado>:s and p.teveResultado<=:e and p.efetividade=:emendas and p.resultadoCongresso<>:resultadoCongresso"),
	@NamedQuery(
			name = "contadorComResultado",  
			query = "select count(p.id) from Proposicao p where p.teveResultado>:s and p.teveResultado<=:e and p.resultadoCongresso<>:resultadoCongresso")			
						
	
	
})
//@formatter:on
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Proposicao extends AbstractEntity {

	private static final long serialVersionUID = 7949894944142814382L;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", nullable = false)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated", nullable = false)
	private Date updated;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(nullable = false, unique = true)
	private Integer idProposicao;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado")
	private EstadoProposicao estado;

	@Column(name = "comAtencaoEspecial", nullable = true)
	private Long comAtencaoEspecial;
	@Column(name = "foiencaminhada", nullable = true)
	private Long foiEncaminhada;
	@Column(name = "foiatribuida", nullable = true)
	private Long foiAtribuida;
	@Column(name = "foianalisada", nullable = true)
	private Long foiAnalisada;
	@Column(name = "foirevisada", nullable = true)
	private Long foiRevisada;
	@Column(name = "foidespachada", nullable = true)
	private Long foiDespachada;

	@Column(name = "teveresultado", nullable = true)
	private Long teveResultado;

	@JsonDeserialize(using = EfetividadeSALDeserializer.class)
	@Enumerated(EnumType.STRING)
	@Column(name = "efetividade")
	private EfetividadeSAL efetividade;

	@JsonDeserialize(using = ResultadoCongressoDeserializer.class)
	@Enumerated(EnumType.STRING)
	@Column(name = "resultado_congresso")
	private ResultadoCongresso resultadoCongresso;

	@Column
	private String tipo;

	@Column
	private String ano;

	@Column
	private String numero;

	@Column
	private String situacao;

	@Column(name = "parecer_sal", length = 5000)
	private String parecerSAL;
	@Column(name = "tramitacao", length = 5000)
	private String tramitacao;

	@Column(name = "explicacao_sal", length = 5000)
	private String explicacao;

	@Column
	private String autor;

	@Enumerated(EnumType.STRING)
	@Column
	private Origem origem;

	@Column(length = 2000)
	private String resultadoASPAR;

	@Column(name = "ultima_comissao")
	private String comissao;

	@Transient
	private Integer seqOrdemPauta;

	@Transient
	private String sigla;

	@Column(length = 2000)
	private String ementa;

	@Column
	private String linkProposicao;

	@Transient
	private String linkPauta;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "posicionamento_atual_id", nullable = true)
	private PosicionamentoProposicao posicionamentoAtual;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "posicionamento_supar_id", nullable = true)
	private Posicionamento posicionamentoSupar;

	@Transient
	private Boolean posicionamentoPreliminar;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Usuario responsavel;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idequipe", referencedColumnName = "id")
	private Equipe equipe;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "proposicao")
	// @OrderBy("pautaReuniaoComissao")
	private Set<ProposicaoPautaComissao> pautasComissoes = new HashSet<ProposicaoPautaComissao>();

	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id", insertable = false, updatable = false, referencedColumnName = "proposicaoid", nullable = true)
	ProposicaoPautaComissaoFutura ultima;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tagproposicao", joinColumns = { @JoinColumn(name = "proposicao_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id", referencedColumnName = "id") })
	private List<Tag> tags;

	@Transient
	private List<Comentario> listaComentario = new ArrayList<>();

	@Transient
	private Set<EncaminhamentoProposicao> listaEncaminhamentoProposicao = new HashSet<>();

	@Transient
	private List<ProposicaoPautaComissao> listaPautasComissao = new ArrayList<>();

	@Column(nullable = false)
	private boolean isFavorita;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "proposicao")
	@OrderBy("data ASC")
	private SortedSet<AlteracaoProposicao> alteracoesProposicao = new TreeSet<AlteracaoProposicao>();

	@Transient
	private Integer totalComentarios = 0;
	@Transient
	private Integer totalNotasTecnicas = 0;
	@Transient
	private Integer totalEmendas = 0;
	@Transient
	private Integer totalBriefings = 0;
	@Transient
	private Integer totalParecerAreaMerito = 0;
	@Transient
	private Integer totalEncaminhamentos = 0;

	@Transient
	private Integer totalPautasComissao = 0;

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "proposicoesFilha")
	private Set<Proposicao> proposicoesPai;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "proposicao_proposicao", joinColumns = { @JoinColumn(name = "proposicao_id") }, inverseJoinColumns = { @JoinColumn(name = "proposicao_id_filha") })
	private Set<Proposicao> proposicoesFilha;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "proposicao_elaboracao_normativa", joinColumns = { @JoinColumn(name = "proposicao_id") }, inverseJoinColumns = { @JoinColumn(name = "elaboracao_normativa_id") })
	private Set<ElaboracaoNormativa> elaboracoesNormativas;

	@OrderBy("ordem")
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "proposicao")
	@JsonSerialize(using = CompactListRoadmapComissaoSerializer.class)
	private List<RoadmapComissao> roadmapComissoes;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "proposicao")
	private List<ProcessoSei> processosSei;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "proposicao", cascade = CascadeType.REMOVE)
	private List<NotaTecnica> notatecnicas = new ArrayList<NotaTecnica>();
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "proposicao", cascade = CascadeType.REMOVE)
	private List<Briefing> briefings = new ArrayList<Briefing>();
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "proposicao", cascade = CascadeType.REMOVE)
	private List<Emenda> emendas = new ArrayList<Emenda>();

	public Proposicao() {
		super();
		this.created = this.updated = new Date();
		this.estado = EstadoProposicao.INCLUIDO;
	}

	public String getSigla() {
		if (Objects.isNull(sigla))
			sigla = getTipo() + " " + getNumero() + "/" + getAno();
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Integer getIdProposicao() {
		return idProposicao;
	}

	public void setIdProposicao(Integer idProposicao) {
		this.idProposicao = idProposicao;
	}

	public String getTipo() {
		if (tipo != null && !tipo.isEmpty()) {
			tipo = tipo.trim();
		}
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getEmenta() {
		return ementa;
	}

	public void setEmenta(String ementa) {
		this.ementa = ementa;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		if (autor.length() > 255) {
			System.err.println("autor muito longo:" + this);
			autor = autor.substring(0, 255);
		}
		this.autor = autor;
	}

	public String getComissao() {
		if (comissao == null || comissao.length() == 0) {
			if (!pautasComissoes.isEmpty() && getUltima() != null && getUltima().getPautaReuniaoComissao() != null) {
				// para algumas proposicoes da camara o campo com dados da
				// comissao atual está vazio.
				// por exemplo:
				// http://www.camara.gov.br/SitCamaraWS/Proposicoes.asmx/ListarProposicoes?sigla=PL&numero=2323&ano=2011&datApresentacaoIni=&datApresentacaoFim=&idTipoAutor=&parteNomeAutor=&siglaPartidoAutor=&siglaUFAutor=&generoAutor=&codEstado=&codOrgaoEstado=&emTramitacao=&v=4
				return getUltima().getPautaReuniaoComissao().getComissao();// pautasComissoes.first().getPautaReuniaoComissao().getComissao();
			}
		}
		return comissao;
	}

	public void setComissao(String comissao) {
		this.comissao = comissao;
	}

	public Integer getSeqOrdemPauta() {
		return seqOrdemPauta;
	}

	public void setSeqOrdemPauta(Integer seqOrdemPauta) {
		this.seqOrdemPauta = seqOrdemPauta;
	}

	public Origem getOrigem() {
		return origem;
	}

	public void setOrigem(Origem origem) {
		this.origem = origem;
	}

	public String getLinkProposicao() {
		if (origem == null) {
			return null;
		}
		switch (origem) {
		case CAMARA:
			return "http://www2.camara.leg.br/proposicoesWeb/fichadetramitacao?idProposicao=" + getIdProposicao();
		case SENADO:
			return "http://www.senado.leg.br/atividade/materia/detalhes.asp?p_cod_mate=" + getIdProposicao();

		default:
			break;
		}
		return linkProposicao;
	}

	public void setLinkProposicao(String linkProposicao) {
		this.linkProposicao = linkProposicao;
	}

	public String getLinkPauta() {
		return linkPauta;
	}

	public void setLinkPauta(String linkPauta) {
		this.linkPauta = linkPauta;
	}

	public PosicionamentoProposicao getPosicionamentoAtual() {
		return posicionamentoAtual;
	}

	public void setPosicionamentoAtual(PosicionamentoProposicao posicionamento) {
		this.posicionamentoAtual = posicionamento;
	}

	public Boolean isPosicionamentoPreliminar() {
		return posicionamentoPreliminar;
	}

	public void setPosicionamentoPreliminar(Boolean posicionamentoPreliminar) {
		this.posicionamentoPreliminar = posicionamentoPreliminar;
	}

	public List<Comentario> getListaComentario() {
		return this.listaComentario;
	}

	@JsonIgnore
	public void setListaComentario(final List<Comentario> listaComentario) {
		this.listaComentario = listaComentario;
	}

	public Set<EncaminhamentoProposicao> getListaEncaminhamentoProposicao() {
		return listaEncaminhamentoProposicao;
	}

	@JsonIgnore
	public void setListaEncaminhamentoProposicao(Set<EncaminhamentoProposicao> listaEncaminhamentoProposicao) {
		this.listaEncaminhamentoProposicao = listaEncaminhamentoProposicao;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<ProposicaoPautaComissao> getListaPautasComissao() {
		return listaPautasComissao;
	}

	public void setListaPautasComissao(List<ProposicaoPautaComissao> listaPautasComissao) {
		this.listaPautasComissao = listaPautasComissao;
	}

	public Usuario getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Usuario responsavel) {
		this.responsavel = responsavel;
		if (responsavel == null) {

		} else if (responsavel.getEquipe() != null) {
			this.equipe = responsavel.getEquipe();
		}
	}

	public String getResultadoASPAR() {
		return resultadoASPAR;
	}

	public void setResultadoASPAR(String resultadoASPAR) {
		this.resultadoASPAR = resultadoASPAR;
	}

	public boolean isFavorita() {
		return isFavorita;
	}

	public void setFavorita(boolean isFavorita) {
		this.isFavorita = isFavorita;
	}

	public Integer getTotalComentarios() {
		if (CollectionUtils.isNotEmpty(listaComentario)) {
			totalComentarios = listaComentario.size();
		}
		return totalComentarios;
	}

	public void setTotalComentarios(Integer totalComentarios) {
		this.totalComentarios = totalComentarios;
	}

	public Integer getTotalEncaminhamentos() {
		if (CollectionUtils.isNotEmpty(listaEncaminhamentoProposicao)) {
			totalEncaminhamentos = listaEncaminhamentoProposicao.size();
		}
		return totalEncaminhamentos;
	}

	public void setTotalEncaminhamentos(Integer totalEncaminhamentos) {
		this.totalEncaminhamentos = totalEncaminhamentos;
	}

	public Integer getTotalPautasComissao() {
		if (CollectionUtils.isNotEmpty(listaPautasComissao)) {
			totalPautasComissao = listaPautasComissao.size();
		}
		return totalPautasComissao;
	}

	public void setTotalPautasComissao(Integer totalPautasComissao) {
		this.totalPautasComissao = totalPautasComissao;
	}

	@JsonSerialize(using = CompactSetProposicaoSerializer.class)
	public Set<Proposicao> getProposicoesPai() {
		return proposicoesPai;
	}

	@JsonSerialize(using = CompactSetProposicaoSerializer.class)
	public Set<Proposicao> getProposicoesFilha() {
		return proposicoesFilha;
	}

	public void setProposicoesPai(Set<Proposicao> proposicoesPai) {
		this.proposicoesPai = proposicoesPai;
	}

	public void setProposicoesFilha(Set<Proposicao> proposicoesFilha) {
		this.proposicoesFilha = proposicoesFilha;
	}

	public Set<ElaboracaoNormativa> getElaboracoesNormativas() {
		return elaboracoesNormativas;
	}

	public void setElaboracoesNormativas(Set<ElaboracaoNormativa> elaboracoesNormativas) {
		this.elaboracoesNormativas = elaboracoesNormativas;
	}

	public List<RoadmapComissao> getRoadmapComissoes() {
		return roadmapComissoes;
	}

	public void setRoadmapComissoes(List<RoadmapComissao> etapasRoadmapComissoes) {
		this.roadmapComissoes = etapasRoadmapComissoes;
	}

	public List<ProcessoSei> getProcessosSei() {
		return processosSei;
	}

	public void setProcessosSei(List<ProcessoSei> processosSei) {
		this.processosSei = processosSei;
	}

	@JsonIgnore
	public Set<AlteracaoProposicao> getAlteracoesProposicao() {
		return alteracoesProposicao;
	}

	@JsonIgnore
	public AlteracaoProposicao getLastAlteracoesProposicao() {
		return alteracoesProposicao.last();
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";

		result += "idProposicao: " + idProposicao;

		result += ", tipo: " + tipo;

		result += ", ano: " + ano;

		result += ", numero: " + numero;
		if (autor != null && !autor.trim().isEmpty())
			result += ", autor: " + autor;
		if (comissao != null && !comissao.trim().isEmpty())
			result += ", comissao: " + comissao;
		if (seqOrdemPauta != null)
			result += ", seqOrdemPauta: " + seqOrdemPauta;
		if (situacao != null)
			result += ", situacao: " + situacao;
		return result;
	}

	public void addAlteracao(AlteracaoProposicao altera) {
		altera.setProposicao(this);
		alteracoesProposicao.add(altera);

	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String siglaSituacao) {
		this.situacao = siglaSituacao;
	}

	@JsonIgnore
	public Set<ProposicaoPautaComissao> getPautasComissoes() {
		return pautasComissoes;
	}

	public ProposicaoPautaComissao getPautaComissaoAtual() {
		// // TODO isto pode ficar melhor.
		ProposicaoPautaComissao mostRecent = null;

		for (Iterator<ProposicaoPautaComissao> iterator = pautasComissoes.iterator(); iterator.hasNext();) {
			ProposicaoPautaComissao ppc = (ProposicaoPautaComissao) iterator.next();
			if (mostRecent == null || mostRecent.getPautaReuniaoComissao().getData().before(ppc.getPautaReuniaoComissao().getData())) {
				mostRecent = ppc;
			}

		}
		return mostRecent;
	}

	public EstadoProposicao getEstado() {
		return estado;
	}

	public void setEstado(EstadoProposicao estado) {
		this.estado = estado;
	}

	public String getParecerSAL() {
		return parecerSAL;
	}

	public void setParecerSAL(String parecerSAL) {
		this.parecerSAL = parecerSAL;
	}

	public String getExplicacao() {
		return explicacao;
	}

	public void setExplicacao(String explicacao) {
		this.explicacao = explicacao;
	}

	public Integer getTotalNotasTecnicas() {
		return totalNotasTecnicas;
	}

	public void setTotalNotasTecnicas(Integer totalNotasTecnicas) {
		this.totalNotasTecnicas = totalNotasTecnicas;
	}

	public Integer getTotalParecerAreaMerito() {
		return totalParecerAreaMerito;
	}

	public void setTotalParecerAreaMerito(Integer totalParecerAreaMerito) {
		this.totalParecerAreaMerito = totalParecerAreaMerito;
	}

	public Posicionamento getPosicionamentoSupar() {
		return posicionamentoSupar;
	}

	public void setPosicionamentoSupar(Posicionamento posicionamentoSupar) {
		this.posicionamentoSupar = posicionamentoSupar;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	@PrePersist
	protected void onCreate() {
		updated = created = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updated = new Date();
	}

	public Integer getTotalEmendas() {
		return totalEmendas;
	}

	public void setTotalEmendas(Integer totalEmendas) {
		this.totalEmendas = totalEmendas;
	}

	public Integer getTotalBriefings() {
		return totalBriefings;
	}

	public void setTotalBriefings(Integer totalBriefings) {
		this.totalBriefings = totalBriefings;
	}

	public ProposicaoPautaComissaoFutura getUltima() {
		return ultima;
	}

	public void setUltima(ProposicaoPautaComissaoFutura ultima) {
		this.ultima = ultima;
	}

	public EfetividadeSAL getEfetividade() {
		return efetividade;
	}

	public void setEfetividade(EfetividadeSAL resultadoFinal) {
		this.efetividade = resultadoFinal;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setTramitacao(String tramitacao) {
		this.tramitacao = tramitacao;

	}

	public String getTramitacao() {
		return tramitacao;
	}

	public Long getFoiEncaminhada() {
		return foiEncaminhada;
	}

	public void setFoiEncaminhada(Long foiEncaminhada) {
		this.foiEncaminhada = foiEncaminhada;
	}

	public Long getFoiAnalisada() {
		return foiAnalisada;
	}

	public void setFoiAnalisada(Long foiAnalisada) {
		this.foiAnalisada = foiAnalisada;
	}

	public Long getFoiRevisada() {
		return foiRevisada;
	}

	public void setFoiRevisada(Long foiRevisada) {
		this.foiRevisada = foiRevisada;
	}

	public Long getFoiDespachada() {
		return foiDespachada;
	}

	public void setFoiDespachada(Long foiDespachada) {
		this.foiDespachada = foiDespachada;
	}

	public Long getComAtencaoEspecial() {
		return comAtencaoEspecial;
	}

	public void marcarAtencaoEspecial() {
		comAtencaoEspecial = System.currentTimeMillis();
	}

	public void desmarcarAtencaoEspecial() {
		comAtencaoEspecial = null;
	}

	public Long getFoiAtribuida() {
		return foiAtribuida;
	}

	public void setFoiAtribuida(Long foiAtribuida) {
		this.foiAtribuida = foiAtribuida;
	}

	public ResultadoCongresso getResultadoCongresso() {
		return resultadoCongresso;
	}

	public void setResultadoCongresso(ResultadoCongresso resultadoCongresso) {
		this.resultadoCongresso = resultadoCongresso;
	}

	public Long getTeveResultado() {
		return teveResultado;
	}

	public void setTeveResultado(Long teveResultado) {
		this.teveResultado = teveResultado;
	}
}
