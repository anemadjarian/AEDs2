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

class PilhaFlexivel{
    Celula topo;

    public PilhaFlexivel(){
        topo=null;
    }

    void inserir(int x){
        Celula tmp = new Celula(x);
        tmp.prox = topo;
        topo = tmp;
    }

    int remover() throws Exception {
        if(topo == null){
            throw new Exception("Pilha Vazia");
        }
        int removido = topo.elemento;
        Celula tmp = topo;
        topo = topo.prox;
        tmp.prox = null;
        tmp = null;
        return removido;
    }

    void mostrar(){
        for(Celula i=topo; i != null; i=i.prox){
            System.out.print(i.elemento + " ");
        }
        System.out.println();
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        PilhaFlexivel pilha = new PilhaFlexivel();

        int n = sc.nextInt();

        for(int i=0; i<n; i++){
            int cmd = sc.nextInt();
            if(cmd == 1){
                int x = sc.nextInt();
                pilha.inserir(x);
            } else if(cmd == 2){
                try {
                    int removido = pilha.remover();
                    System.out.println("Removido: " + removido);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if(cmd == 3){
                pilha.mostrar();
            }
        }
    }
} 