package processamento.arquivo.csv.receita.mapeador.exception;

public class DadosReceitaNaoPreenchidoException extends RuntimeException {
    public static final String ERRO_DADOS_NAO_PREENCHIDOS = "Dados Receita não foram totalmente preenchidos!";
    public DadosReceitaNaoPreenchidoException(){
        super(ERRO_DADOS_NAO_PREENCHIDOS);
    }
}
