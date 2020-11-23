package processamento;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SincronizacaoReceitaTest {

    private static final long TEMPO_BASE = 5000;
    private static final long NUMERO_CONTAS_SICREDI = 4000000;
    private static final long TEMPO_MAXIMO_PROCESSAMENTO_MILISSEGUNDOS = 144000000;
    private String pathBase;

    @Before
    public void iniciliazaTeste(){
        this.pathBase = System.getProperty("user.dir")+"\\src\\main\\resources\\";
    }

    @Test
    public void atualiza5ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("5contasTeste.csv",5);
    }

    @Test
    public void atualiza100ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("100contasTeste.csv",100);
    }

    @Test
    public void atualiza1000ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("1000contasTeste.csv",1000);
    }

    @Test
    public void atualiza10000ContasSucesso() throws IOException{
        testePerformanceTempoSicronizacaoReceita("10000contasTeste.csv",10000);
    }

    @Test
    public void atualiza100000ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("100000contasTeste.csv",100000);
    }

    private void testePerformanceTempoSicronizacaoReceita(String fileName,long numeroContas) throws IOException {
        long tempoInicio = System.currentTimeMillis();
        Path caminhoArquivo = SincronizacaoReceita.atualizaContas(pathBase+fileName);
        long tempoFim = System.currentTimeMillis();
        long tempoPassado = tempoFim - tempoInicio;
        boolean arquivoExiste = Files.exists(caminhoArquivo);
        Assert.assertEquals(true,arquivoExiste);
        Long tempoMaximoProcessamentoMilissegundos = obtemTempoMaximoProcessamentoMilissegundos(numeroContas);
        Assert.assertTrue(tempoMaximoProcessamentoMilissegundos>=tempoPassado);
    }


    private long obtemTempoMaximoProcessamentoMilissegundos(long numeroContas){
        return TEMPO_BASE + (numeroContas * TEMPO_MAXIMO_PROCESSAMENTO_MILISSEGUNDOS)/NUMERO_CONTAS_SICREDI;
    }


}
