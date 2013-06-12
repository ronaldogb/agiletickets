package br.com.caelum.agiletickets.controllers;

import static br.com.caelum.vraptor.view.Results.status;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;

import com.google.common.base.Strings;

@Resource
public class EspetaculosController {

	private static final String DESCRIÇÃO = "Descrição";
	private static final String NOME = "Nome";
	private static final String MSG_SESSAO_RESERVADA 			= "Sessao reservada com sucesso";
	private static final String MSG_ESCOLHER_LUGAR				= "Voce deve escolher um lugar ou mais";
	private static final String MSG_INGRESSOS_INDISPONIVEIS 	= "Nao existem ingressos disponíveis";
	private static final String MSG_SESSOES_CRIADAS_COM_SUCESSO = " sessoes criadas com sucesso";
	private static final String MSG_ESPETACULO_NAO_ENCONTRADO 	= "Espetáculo não encontrado";
	
	private final Agenda agenda;
	private Validator validator;
	private Result result;
	private final DiretorioDeEstabelecimentos estabelecimentos;

	public EspetaculosController(Agenda agenda, DiretorioDeEstabelecimentos estabelecimentos, Validator validator, Result result) {
		this.agenda = agenda;
		this.estabelecimentos = estabelecimentos;
		this.validator = validator;
		this.result = result;
	}

	@Get @Path("/espetaculos")
	public List<Espetaculo> lista() {
		
		result.include("estabelecimentos", estabelecimentos.todos());
		return agenda.espetaculos();
	}

	@Post @Path("/espetaculos")
	public void adicionaEspetaculo(Espetaculo espetaculo) {
		// aqui eh onde fazemos as varias validacoes
		// se nao tiver nome, avisa o usuario
		// se nao tiver descricao, avisa o usuario
				
		validaTextoEmBranco(espetaculo.getNome(), NOME);
		validaTextoEmBranco(espetaculo.getDescricao(), DESCRIÇÃO);
		validator.onErrorRedirectTo(this).lista();

		agenda.cadastra(espetaculo);
		result.redirectTo(this).lista();
	}
    private void validaTextoEmBranco (String texto, String variavel ) {
    	if (Strings.isNullOrEmpty(texto)) {
			validator.add(new ValidationMessage(variavel + " do espetáculo nao pode estar em branco", ""));
		}
    	
    }

	@Get @Path("/sessao/{id}")
	public void sessao(Long id) {
		Sessao sessao = agenda.sessao(id);
		if (sessao == null) {
			result.notFound();
		}

		result.include("sessao", sessao);
	}

	@Post @Path("/sessao/{sessaoId}/reserva")
	public void reserva(Long sessaoId, final Integer quantidade) {
		Sessao sessao = agenda.sessao(sessaoId);
		if (sessao == null) {
			result.notFound();
		}
		
		validarReserva(quantidade, sessao);

		sessao.reserva(quantidade);
		result.include("message", MSG_SESSAO_RESERVADA);

		result.redirectTo(IndexController.class).index();
	}

	@Get @Path("/espetaculo/{espetaculoId}/sessoes")
	public void sessoes(Long espetaculoId) {
		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);

		result.include("espetaculo", espetaculo);
	}

	
	private void validarReserva(final Integer quantidade, Sessao sessao) {
		if (quantidade < 1) {
			validator.add(new ValidationMessage(MSG_ESCOLHER_LUGAR, ""));
		} else {
			if (!sessao.podeReservar(quantidade)) {
				validator.add(new ValidationMessage(MSG_INGRESSOS_INDISPONIVEIS, ""));
			}
		}

		validator.onErrorRedirectTo(this).sessao(sessao.getId());
	}



	@Post @Path("/espetaculo/{espetaculoId}/sessoes")
	public void cadastraSessoes(Long espetaculoId, LocalDate inicio, LocalDate fim, LocalTime horario, Periodicidade periodicidade) {
		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);

		// cria sessoes baseado no periodo de inicio e fim passados pelo usuario
		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);

		agenda.agende(sessoes);

		result.include("message", sessoes.size() + MSG_SESSOES_CRIADAS_COM_SUCESSO);
		result.redirectTo(this).lista();
	}

	private Espetaculo carregaEspetaculo(Long espetaculoId) {
		Espetaculo espetaculo = agenda.espetaculo(espetaculoId);
		validarCarregaEspetaculo(espetaculo);
		return espetaculo;
	}

	private void validarCarregaEspetaculo(Espetaculo espetaculo) {
		if (espetaculo == null) {
			validator.add(new ValidationMessage(MSG_ESPETACULO_NAO_ENCONTRADO, ""));
		}
		validator.onErrorUse(status()).notFound();
	}
}
