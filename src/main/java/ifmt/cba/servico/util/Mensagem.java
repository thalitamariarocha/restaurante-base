package ifmt.cba.servico.util;

public class Mensagem {

    private String erro;

    public Mensagem() {
        this.erro = "";
    }

    public Mensagem(String texto) {
        this.erro = texto;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String texto) {
        this.erro = texto;
    }

    
}
