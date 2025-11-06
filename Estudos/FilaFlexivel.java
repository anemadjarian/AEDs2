import java.util.*;
class Celula{
    public int elemento;
    public Celula prox;

    public Celula(){
        this(0);
    }

    public Celula(int elemento){
        this.elemento=elemento;
        this.prox=null;
    }
}
public class FilaFlexivel {
    Celula primeiro,ultimo;

    public FilaFlexivel(){
        this.primeiro=this.ultimo=null;
    }

    void inserir(int x) {
        Celula tmp = new Celula(x);
        if (primeiro == null) {
            primeiro = ultimo = tmp;
        } else {
            ultimo.prox = tmp;
            ultimo = tmp;
        }
    }
    

    int remover() throws Exception{
        if(primeiro==null){
            throw new Exception("Fila vazia");
        }
        int removido = primeiro.elemento;
        Celula tmp = primeiro;
        primeiro = primeiro.prox;
        tmp.prox=null;
        tmp=null;
        return removido;
    }

    void mostrar(){
        for(Celula i=primeiro; i != null; i=i.prox){
            System.out.print(i.elemento + " ");
        }
        System.out.println();
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        FilaFlexivel fila = new FilaFlexivel();

        int n = sc.nextInt();
        for(int i=0; i<n; i++){
            int cmd = sc.nextInt();
            if(cmd==1){
                int x = sc.nextInt();
                fila.inserir(x);
            } else if(cmd==2){
                try{
                    int removido=fila.remover();
                    System.out.println("Removido: " + removido);
                } catch(Exception e){
                    System.out.println(e.getMessage());
                }
            } else if(cmd==3){
                fila.mostrar();
            }
        }
    }
}