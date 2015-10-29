package br.gov.mj.sislegis.app.parser.senado.xstream;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.gov.mj.sislegis.app.enumerated.Origem;
import br.gov.mj.sislegis.app.model.Proposicao;
import br.gov.mj.sislegis.app.util.SislegisUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Materia")
public class Materia {
	@XStreamAlias("IdentificacaoMateria")
	IdentificacaoMateria identificacaoMateria;
	@XStreamAlias("DadosBasicosMateria")
	DadosBasicosMateria DadosBasicosMateria;

	@XStreamAlias("AutoresPrincipais")
	AutoresPrincipais autoresPrincipais;

	@XStreamAlias("Relatoria")
	Relatoria relatoria;

	@XStreamAlias("SituacaoAtual")
	SituacaoAtual situacaoAtual;

	@Override
	public String toString() {

		return identificacaoMateria.toString();
	}

	public String getRelator() {
		if (relatoria != null && relatoria.relatores != null && !relatoria.relatores.isEmpty()) {
			Relator relator = relatoria.relatores.get(0);
			String desc = "";
			if (relator.tratamento != null && relator.tratamento.length() > 0) {
				desc = relator.tratamento + " ";
			}
			if (relator.nome != null && relator.nome.length() > 0) {
				desc += relator.nome;
			}
			return desc;
		}
		return "";
	}

	// Cada webservice tem uma estrutura, a abaixo é para a pauta. Acima é para
	// proposicao direto
	@XStreamAlias("autoria")
	Autoria autoria;

	@XStreamAlias("Situacoes")
	Situacoes situacoes;

	@XStreamAlias("Ano")
	String ano;
	@XStreamAlias("Codigo")
	Integer codigo;
	@XStreamAlias("Numero")
	String numero;

	@XStreamAlias("Subtipo")
	String subtipo;
	@XStreamAlias("Ementa")
	String ementa;

	public Proposicao toProposicao() {
		Proposicao p = new Proposicao();
		if (identificacaoMateria == null) {
			identificacaoMateria = new IdentificacaoMateria();
			identificacaoMateria.AnoMateria = ano;
			identificacaoMateria.CodigoMateria = codigo;
			identificacaoMateria.NumeroMateria = numero;
			identificacaoMateria.SiglaSubtipoMateria = subtipo;
		}
		p.setIdProposicao(identificacaoMateria.CodigoMateria);
		p.setNumero(identificacaoMateria.NumeroMateria);
		p.setAno(identificacaoMateria.AnoMateria);
		p.setTipo(identificacaoMateria.SiglaSubtipoMateria);

		if (autoresPrincipais != null && !autoresPrincipais.autores.isEmpty()) {
			AutorPrincipal autor = autoresPrincipais.autores.get(0);
			p.setAutor(autor.getDescricao());
		} else if (autoria != null && autoria.Autor != null) {
			p.setAutor(autoria.Autor.getDescricao());
		}
		p.setOrigem(Origem.SENADO);
		if (situacaoAtual == null) {
			if (situacoes == null || situacoes.situacao == null) {
				Logger.getLogger(SislegisUtil.SISLEGIS_LOGGER).log(Level.WARNING, "Nao carregou a situacao atual ");
			}
		} else if (situacaoAtual.autuacoes == null) {
			Logger.getLogger(SislegisUtil.SISLEGIS_LOGGER).log(Level.WARNING,
					"Nao carregou autuacoes da situacao atual");
		} else if (situacaoAtual.autuacoes.autuacoes == null) {
			Logger.getLogger(SislegisUtil.SISLEGIS_LOGGER).log(Level.WARNING,
					"Nao carregou autuacoes da situacao atual");
		} else if (!situacaoAtual.autuacoes.autuacoes.isEmpty()) {
			p.setComissao(situacaoAtual.autuacoes.autuacoes.get(0).Local.SiglaLocal);
			p.setSituacao(situacaoAtual.autuacoes.autuacoes.get(0).Situacao.SiglaSituacao);
		}
		if (DadosBasicosMateria != null) {
			p.setEmenta(DadosBasicosMateria.EmentaMateria);
		} else {
			p.setEmenta(ementa);
		}

		p.setOrigem(Origem.SENADO);
		p.setLinkProposicao("http://www.senado.leg.br/atividade/materia/detalhes.asp?p_cod_mate=" + p.getIdProposicao());
		return p;

	}

	public static void configXstream(XStream xstream) {
		xstream.processAnnotations(Materia.class);
		xstream.processAnnotations(IdentificacaoMateria.class);
		xstream.processAnnotations(DadosBasicosMateria.class);
		xstream.processAnnotations(AutoresPrincipais.class);
		xstream.processAnnotations(AutorPrincipal.class);
		Relatoria.configXstream(xstream);

		SituacaoAtual.configXstream(xstream);

	}
}
