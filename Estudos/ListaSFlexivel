import java.util.*;

class Celula {
    int elemento;
    Celula prox;

    public Celula() {
        this(0);
    }

    public Celula(int elemento) {
        this.elemento = elemento;
        this.prox = null;
    }
}

public class ListaSFlexivel {
    Celula primeiro, ultimo;

    public ListaSFlexivel() {
        primeiro = new Celula(); // célula cabeça
        ultimo = primeiro;
    }

    int tamanho() {
        int cont = 0;
        for (Celula i = primeiro.prox; i != null; i = i.prox) cont++;
        return cont;
    }

    void inserirInicio(int x) {
        Celula tmp = new Celula(x);
        tmp.prox = primeiro.prox;
        primeiro.prox = tmp;
        if (primeiro == ultimo) {
            ultimo = tmp;
        }
    }

    void inserirFim(int x) {
        Celula tmp = new Celula(x);
        ultimo.prox = tmp;
        ultimo = tmp;
    }

    void inserir(int x, int pos) throws Exception {
        int n = tamanho();
        if (pos < 0 || pos > n) throw new Exception("Posição inválida");
        if (pos == 0) inserirInicio(x);
        else if (pos == n) inserirFim(x);
        else {
            Celula i = primeiro;
            for (int j = 0; j < pos; j++, i = i.prox);
            Celula tmp = new Celula(x);
            tmp.prox = i.prox;
            i.prox = tmp;
        }
    }

    int removerInicio() throws Exception {
        if (primeiro == ultimo) throw new Exception("Erro: lista vazia");
        Celula tmp = primeiro.prox;
        int removido = tmp.elemento;
        primeiro.prox = tmp.prox;
        if (tmp == ultimo) ultimo = primeiro;
        return removido;
    }

    int removerFim() throws Exception {
        if (primeiro == ultimo) throw new Exception("Erro: lista vazia");
        Celula i;
        for (i = primeiro; i.prox != ultimo; i = i.prox);
        int removido = ultimo.elemento;
        ultimo = i;
        ultimo.prox = null;
        return removido;
    }

    int remover(int pos) throws Exception {
        int n = tamanho();
        if (pos < 0 || pos >= n) throw new Exception("Posição inválida");
        if (pos == 0) return removerInicio();
        if (pos == n - 1) return removerFim();
        Celula i = primeiro;
        for (int j = 0; j < pos; j++, i = i.prox);
        Celula tmp = i.prox;
        int removido = tmp.elemento;
        i.prox = tmp.prox;
        return removido;
    }

    void mostrar() {
        for (Celula i = primeiro.prox; i != null; i = i.prox)
            System.out.print(i.elemento + " ");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        ListaSFlexivel lista = new ListaSFlexivel();
        lista.inserirFim(1);
        lista.inserirFim(2);
        lista.inserirInicio(0);
        lista.inserir(3, 3);
        lista.mostrar(); // 0 1 2 3
        lista.removerInicio();
        lista.removerFim();
        lista.mostrar(); // 1 2
    }
}