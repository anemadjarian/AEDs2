#include<stdio.h>
#include<stdlib.h>

typedef struct No{
    int elemento;
    struct No *dir, *esq;
}No;

No *raiz;

void iniciarRaiz(){
    raiz=NULL;
}
    
No *iniciar(int x){
    No *i = (No*) malloc(sizeof(No));
    i->elemento=x;
    i->dir = i->esq = NULL;
    return i;
}

void inserir(int x){
    inserir2(x, &raiz);
}

void inserir2(int x, No **i){
    if(*i==NULL){
        *i = iniciar(x);
    } else if(x>(*i)->elemento){
        inserir2(x, (*i)->dir);
    } else if(x<(*i)->elemento){
        inserir2(x, (*i)->elemento);
    }
}

void caminharCentral(){
    int soma=0;
    caminharCentralRec(soma, raiz);
}

void caminharCentralRec(int soma, No *i){
    if (i != NULL)
    {
        soma++;
        caminharCentralRec(soma, i->esq);
        soma=0;
        soma++;
        caminharCentralRec(soma, i->dir);
        printf("%d", soma);
    }
}

int main(){
    inserir(3);
    inserir(8);
    inserir(5);
    inserir(1);
    inserir(4);

    caminharCentral();
}