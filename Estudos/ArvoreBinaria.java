import java.util.*;
class No{
    int elemento;
    int qtd;
    No dir, esq;

    public No(){
        this.elemento=0;
        this.qtd=0;
        this.dir=null;
        this.esq=null;
    }

    public No(int elemento){
        this.elemento=elemento;
        this.qtd=0;
        this.dir=null;
        this.esq=null;
    }

    public No(int elemento, No dir, No esq){
        this.elemento=elemento;
        this.qtd=0;
        this.dir=dir;
        this.esq=esq;
    }
}
public class ArvoreBinaria {
    No raiz;

    ArvoreBinaria(){
        raiz=null;
    }

    void inserir(int x){
        raiz = inserir(x, raiz);
    }

    No inserir(int x, No i){
        if(i==null){
            No tmp = new No(x);
        } else if(x>i.elemento){
            i.dir = inserir(x, i.dir);
        } else if(x<i.elemento){
            i.esq = inserir(x, i.esq);
        } else{
            i.qtd++;
        }
        return i;
    }

    void inserirPai(int x) throws Exception{
         if(raiz==null){
            No tmp = new No(x);
         } else if(x > raiz.elemento){
            inserirPai(x, raiz.dir, raiz);
         } else if(x < raiz.elemento){
            inserirPai(x, raizesq, raiz);
         } else{
            throw new Exception("ERRO");
         }
    }
}