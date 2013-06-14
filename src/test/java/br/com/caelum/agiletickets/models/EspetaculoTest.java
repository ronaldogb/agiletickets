package br.com.caelum.agiletickets.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.testng.Assert;

public class EspetaculoTest {

	@Test
	public void deveInformarSeEhPossivelReservarAQuantidadeDeIngressosDentroDeQualquerDasSessoes() {
		Espetaculo ivete = new Espetaculo();

		ivete.getSessoes().add(sessaoComIngressosSobrando(1));
		ivete.getSessoes().add(sessaoComIngressosSobrando(3));
		ivete.getSessoes().add(sessaoComIngressosSobrando(2));

		assertTrue(ivete.Vagas(5));
	}

	@Test
	public void deveInformarSeEhPossivelReservarAQuantidadeExataDeIngressosDentroDeQualquerDasSessoes() {
		Espetaculo ivete = new Espetaculo();

		ivete.getSessoes().add(sessaoComIngressosSobrando(1));
		ivete.getSessoes().add(sessaoComIngressosSobrando(3));
		ivete.getSessoes().add(sessaoComIngressosSobrando(2));

		assertTrue(ivete.Vagas(6));
	}

	@Test
	public void DeveInformarSeNaoEhPossivelReservarAQuantidadeDeIngressosDentroDeQualquerDasSessoes() {
		Espetaculo ivete = new Espetaculo();

		ivete.getSessoes().add(sessaoComIngressosSobrando(1));
		ivete.getSessoes().add(sessaoComIngressosSobrando(3));
		ivete.getSessoes().add(sessaoComIngressosSobrando(2));

		assertFalse(ivete.Vagas(15));
	}

	@Test
	public void DeveInformarSeEhPossivelReservarAQuantidadeDeIngressosDentroDeQualquerDasSessoesComUmMinimoPorSessao() {
		Espetaculo ivete = new Espetaculo();

		ivete.getSessoes().add(sessaoComIngressosSobrando(3));
		ivete.getSessoes().add(sessaoComIngressosSobrando(3));
		ivete.getSessoes().add(sessaoComIngressosSobrando(4));

		assertTrue(ivete.Vagas(5, 3));
	}

	@Test
	public void DeveInformarSeEhPossivelReservarAQuantidadeExataDeIngressosDentroDeQualquerDasSessoesComUmMinimoPorSessao() {
		Espetaculo ivete = new Espetaculo();

		ivete.getSessoes().add(sessaoComIngressosSobrando(3));
		ivete.getSessoes().add(sessaoComIngressosSobrando(3));
		ivete.getSessoes().add(sessaoComIngressosSobrando(4));

		assertTrue(ivete.Vagas(10, 3));
	}

	@Test
	public void DeveInformarSeNaoEhPossivelReservarAQuantidadeDeIngressosDentroDeQualquerDasSessoesComUmMinimoPorSessao() {
		Espetaculo ivete = new Espetaculo();

		ivete.getSessoes().add(sessaoComIngressosSobrando(2));
		ivete.getSessoes().add(sessaoComIngressosSobrando(2));
		ivete.getSessoes().add(sessaoComIngressosSobrando(2));

		assertFalse(ivete.Vagas(5, 3));
	}

	private Sessao sessaoComIngressosSobrando(int quantidade) {
		Sessao sessao = new Sessao();
		sessao.setTotalIngressos(quantidade * 2);
		sessao.setIngressosReservados(quantidade);

		return sessao;
	}
	
	@Test	
	public void CriaSessaoComDataFimMaiorQueDataInicioEPeriodicidadeDiaria () {
		LocalDate dtInicio = new LocalDate();
		LocalDate dtFim = dtInicio.plusDays(2);
		LocalTime horario = new LocalTime();
		Espetaculo esp = new Espetaculo();
		
		List<Sessao> sessoes = esp.criaSessoes(dtInicio, dtFim, horario, Periodicidade.DIARIA);
		
		Assert.assertEquals(3, sessoes.size());
		
		Sessao sessaoCriada = sessoes.get(0);
		
		DateTime dataHoraSessao = dtInicio.toDateTime(horario);
		
		Assert.assertEquals( dataHoraSessao, sessaoCriada.getInicio());		
	
	}
	@Test	
	public void CriaSessaoComPeriodoDeUmDiaPeriodicidadeDiaria () {
		LocalDate dtInicio = new LocalDate();
		LocalDate dtFim = new LocalDate();
		LocalTime horario = new LocalTime();
		Espetaculo esp = new Espetaculo();
		
		List<Sessao> sessoes = esp.criaSessoes(dtInicio, dtFim, horario, Periodicidade.DIARIA);
		
		Assert.assertEquals(1, sessoes.size());
		
		Sessao sessaoCriada = sessoes.get(0);
		
		DateTime dataHoraSessao = dtInicio.toDateTime(horario);
		
		Assert.assertEquals( dataHoraSessao, sessaoCriada.getInicio());		
	
	}
	
	@Test
	public void NaoCriaSessoesSeDataFinalEhMenorQueDataInicial() {
		LocalDate dtInicio = new LocalDate();
		LocalDate dtFim = dtInicio.minusDays(1);
		LocalTime horario = new LocalTime();
		Espetaculo esp = new Espetaculo();	
		
		List<Sessao> sessoes = esp.criaSessoes(dtInicio, dtFim, horario, Periodicidade.DIARIA);
		
		Assert.assertEquals(0, sessoes.size());
	}
	
	@Test
	public void NaoCriaSessoesSeHorarioEhNulo() {
		LocalDate dtInicio = new LocalDate();
		LocalDate dtFim = new LocalDate();
		LocalTime horario = null;
		Espetaculo esp = new Espetaculo();	
		
		List<Sessao> sessoes = esp.criaSessoes(dtInicio, dtFim, horario, Periodicidade.DIARIA);
		Assert.assertEquals(0, sessoes.size());
		
	}
	
	@Test
	public void CriaSessaoComPeriodoDeDuasSemanasEPeriodicidadeSemanal() {
		LocalDate dtInicio = new LocalDate();
		LocalDate dtFim	= new LocalDate(2013, 06, 27);
		LocalTime horario = new LocalTime();
		Espetaculo esp = new Espetaculo();
		
		List<Sessao> sessoes = esp.criaSessoes(dtInicio, dtFim, horario, Periodicidade.SEMANAL);
		Assert.assertEquals(3, sessoes.size());
		Assert.assertEquals("13/06/13", sessoes.get(0).getDia());
		Assert.assertEquals("20/06/13", sessoes.get(1).getDia());
		Assert.assertEquals("27/06/13", sessoes.get(2).getDia());
	}
	
	@Test
	public void CriaSessaoComPeriodoDeUmaSemanaEPeriodicidadeSemanal() {
		LocalDate dtInicio = new LocalDate();
		LocalDate dtFim	= new LocalDate(2013, 06, 19);
		LocalTime horario = new LocalTime();
		Espetaculo esp = new Espetaculo();
		
		List<Sessao> sessoes = esp.criaSessoes(dtInicio, dtFim, horario, Periodicidade.SEMANAL);
		Assert.assertEquals(1, sessoes.size());
		Assert.assertEquals("13/06/13", sessoes.get(0).getDia());
	}

}
