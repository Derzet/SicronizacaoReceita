package processamento.externo.receita.modelo;

import java.security.InvalidParameterException;

public class ContaReceita {
    private String agencia;
    private String conta;
    private double saldo;
    private String status;
    private Boolean atualizada;
    private static String regexConta = "\\d*";

    public ContaReceita(String agencia, String conta, double saldo, String status, Boolean atualizada) {
        this.agencia = agencia; //formatar de acordo com a conta receita
        setConta(conta);
        this.saldo = saldo;
        this.status = status;
        this.atualizada = atualizada;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getConta() {
        return conta;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getAtualizada() {
        return atualizada;
    }

    public void setConta(String conta) {
        conta = conta.replace("-","");
        if(conta.matches(regexConta)){
            this.conta = conta;
        }else{
            throw new InvalidParameterException("Conta Inválida! Não respeita o padrão númerico!");
        }
    }

    public void setAtualizada(Boolean atualizada) {
        this.atualizada = atualizada;
    }

}
