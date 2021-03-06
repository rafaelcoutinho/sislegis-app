package br.gov.mj.sislegis.app.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import br.gov.mj.sislegis.app.model.Equipe;
import br.gov.mj.sislegis.app.model.Papel;
import br.gov.mj.sislegis.app.model.Proposicao;
import br.gov.mj.sislegis.app.model.Usuario;
import br.gov.mj.sislegis.app.model.pautacomissao.AgendaComissao;

@Local
public interface UsuarioService extends Service<Usuario> {

	List<Usuario> findByNome(String nome);

	List<Usuario> findByIdEquipe(Long idEquipe);

	Usuario findByEmail(String email);

	List<Usuario> findByNomeOnLDAP(String nome);

	List<Usuario> listUsuariosSeguidoresDeComissao(AgendaComissao agenda);

	Usuario loadComAgendasSeguidas(Long id);

	/**
	 * Lista todos os usuários que seguem a proposicao passada.
	 * 
	 * @param proposicao
	 * @return
	 */
	List<Usuario> listUsuariosSeguidoresDeProposicao(Proposicao proposicao);

	Collection<Proposicao> proposicoesSeguidas(Long id);


	Usuario findOrCreateByEmail(String string, String email);

	Set<Usuario> listUsuariosPorPapel(Papel secretario);

	Set<Usuario> listUsuariosPorPapelDeEquipe(Papel diretor, Equipe equipe);

	void deleteByIdForce(Long id);

}
