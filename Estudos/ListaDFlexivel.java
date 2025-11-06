import java.util.*;
class Celula{
    int elemento;
    Celula prox, ant;

    public Celula(){
        this.elemento=0;
        this.prox=null;
        this.ant=null;
    }

    public Celula(int elemento){
        this.elemento=elemento;
        this.prox=this.ant=null;
    }
}
public class ListaDFlexivel {
    Celula primeiro, ultimo;

    public ListaDFlexivel(){
        this.primeiro=new Celula();
        this.ultimo=primeiro;
    }

    void inserir
}