#include<stdio.h>

void preencheArray(int array[], int n){
    for(int i=0; i<n; i++){
        scanf("%d", &array[i]);
    }
}

void swap(int array[], int j){
    int aux = array[j];
    array[j] = array[j+1];
    array[j+1] = aux;
}

void insertionSort(int array[], int n){
    for(int i=1; i<n; i++){
        int tmp=arra[i];
        int j=i-1;
        while((j>=0) && (array[j]>tmp)){
            array[j+1] = array[j];
            j--;
        }
        array[j+1] = tmp;
    }
}

void mostrar(int array[], int n){
    printf(" [");
    for(int i=0; i<n; i++){
        printf("%d ", array[i]);
    }
    printf("]");
}

int main(){
    int n;
    scanf("%d", &n);
    int array[n];

    preencheArray(array, n);

    insertionSort(array, n);

    mostrar(array, n);
}