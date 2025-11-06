#include<stdio.h>
#include<stdlib.h>

typedef struct Celula{
    int elemento;
    struct Celula *prox;
}Celula;

typedef struct Fila{
    struct Celula *primeiro;
    struct Celula *ultimo;
}Fila;

void construtor(Fila *f){
    f->primeiro=NULL;
    f->ultimo=NULL;
}

void inserir(Fila *f, int x){
    Celula *tmp = (Celula*)malloc(sizeof(Celula));
    tmp->elemento = x;
    tmp->prox = NULL;

    if(f->primeiro==f->ultimo){
        f->primeiro=f->ultimo=tmp;
    } else{
        f->ultimo->prox=tmp;
        f->ultimo=tmp;
        tmp=NULL;
    }
}

int remover(Fila *f){
    if(f->primeiro==NULL){
        printf("Fila vazia");
        exit(1);
    }
    int removido = f->primeiro->elemento;
    Celula *tmp = f->primeiro;
    f->primeiro = f->primeiro->prox;
    tmp->prox = NULL;
    tmp=NULL;
    free(tmp);
    return removido;  
}

void mostrar(Fila *f) {
    for (Celula *i = f->primeiro; i != NULL; i = i->prox) {
        printf("%d ", i->elemento);
    }
    printf("\n");
}

int main(){
    Fila f;
    construtor(&f);

    int n;
    scanf("%d", & n);

    for(int i=0; i<n; i++){
        int cmd;
        scanf("%d", &cmd);

        if(cmd==1){
            int x;
            scanf("%d", &x);
            inserir(&f, x);
        } else if(cmd==2){
            int removido=remover(&f);
            printf("Removido: %d", removido);
        } else if(cmd ==3){
            mostrar(&f);
        }
    }
}