#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

typedef struct No
{
    int elemento;
    int *dir, *esq;
} No;

No *raiz;

void construtor()
{
    raiz = NULL;

    No no;
    no.elemento = NULL;
    no.dir = NULL;
    no.esq = NULL;
}

void inserir(int x)
{
    inserirRec(x, &raiz);
}

void inserirRec(int x, No **i)
{
    if (*i == NULL)
    { // Se tiver vazio, apenas insere
        *i = novoNo(x);
    }
    else if (x < (*i)->elemento)
    { // Se o elemento for menor, volta recursivo para a esquerda
        inserirRec(x, &((*i)->esq));
    }
    else if (x > (*i)->elemento)
    { // Se o elemento for maior, volta recursivo para a direita
        inserirRec(x, &((*i)->dir));
    }
    else
    {
        errx(1, "Erro ao inserir!");
    }
}

void caminharCentral()
{
    printf("[ ");
    caminharCentralRec(raiz);
    printf("]\n");
}

void caminharCentralRec(No *i)
{
    if (i != NULL)
    {
        caminharCentralRec(i->esq);
        printf("%d ", i->elemento);
        caminharCentralRec(i->dir);
    }
}

int main()
{
    int x = 0;
    while (x >= 0)
    {
        scanf("%d", &x);
        inserir(x);
    }
    mostrar();
}