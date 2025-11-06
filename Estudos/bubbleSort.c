#include <stdio.h>

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

void bubbleSort(int array[], int n){
    for(int i=n-1; i>0; i--){
        for(int j=0; j<i; j++){
            if(array[j]>array[j+1]){
                swap(array, j);
            }
        }
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

    bubbleSort(array, n);

    mostrar(array, n);
}