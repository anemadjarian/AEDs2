import java.util.*;

class Contato{
    public String nome, email, telefone;
    public int cpf;

    public Contato(){
        this("", "", "", 0);
    }

    public Contato(String nome, String email, String telefone, int cpf){
        this.nome=nome;
        this.email=email;
        this.telefone=telefone;
        this.cpf=cpf;
    }
}

class Celula{
    public Contato contato;
    public Celula prox;

    public Celula(){
        this(null, null);
    }

    public Celula(Contato contato){
        this(contato, null);
     }

    public Celula(Contato contato, Celula prox){
        this.contato=contato;
        this.prox=prox;
    }
}

class No{
    public char letra;
    public No esq, dir;
    public Celula primeiro, ultimo;

    public No(char letra){
        this.letra=letra;
        this.esq=this.dir=null;
        this.primeiro = new Celula();
        this.ultimo=primeiro;
    }
}

class Agenda{
    No raiz;

    public Agenda(){
        char a = new No("A");
    }

    void inserir(Contato contato){
        if()
    }
}