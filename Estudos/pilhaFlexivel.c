#include<stdio.h>
#include<stdlib.h>

typedef struct Celula{
    int elemento;
    struct Celula *prox;
} Celula;

typedef struct Pilha{
    struct Celula *topo;
} Pilha;

void construtor(Pilha *p){
    p->topo=NULL;
}

void inserir(Pilha *p, int x){
    Celula *tmp = (Celula*)malloc(sizeof(Celula));
    tmp->elemento = x;
    tmp->prox = p->topo;
    p->topo = tmp;
    tmp = NULL;
}

int remover(Pilha *p){
    if(p->topo==NULL){
        return 0;
    }
    int removido = p->topo->elemento;
    Celula *tmp = p->topo;
    p->topo = p->topo->prox;
    tmp->prox=NULL;
    tmp=NULL;
    return removido;
}

void mostrar(Pilha *p){
    for(Celula *i=p->topo; i != NULL; i=i->prox){
        printf("%d ", i->elemento);
    }
    printf("\n");
}

int main(){
    Pilha p;
    construtor(&p);

    int n;
    scanf("%d", &n);

    for(int i=0; i<n; i++){
        int cmd;
        scanf("%d", &cmd);
        if(cmd == 1){
            int x;
            scanf("%d", &x);
            inserir(&p, x);
        } else if( cmd == 2){
            int removido = remover(&p);
            printf("Removido: %d", removido);
        } else if(cmd == 3){
            mostrar(&p);
        }
    }
}