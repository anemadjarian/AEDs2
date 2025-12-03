#include <stdio.h>

int my_strlen(char s[]) {
    int i = 0;
    while (s[i] != '\0') i++;
    return i;
}

int my_strcmp(char a[], char b[]) {
    int i = 0;
    while (a[i] != '\0' && b[i] != '\0') {
        if (a[i] < b[i]) return -1;
        if (a[i] > b[i]) return 1;
        i++;
    }
    if (a[i] == '\0' && b[i] == '\0') return 0;
    if (a[i] == '\0') return -1;
    return 1;
}

void my_strcpy(char dest[], char src[]) {
    int i = 0;
    while (src[i] != '\0') {
        dest[i] = src[i];
        i++;
    }
    dest[i] = '\0';
}

void ordenar(int n, char matriz[][50]) {
    char temp[50];

    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (my_strcmp(matriz[j], matriz[j+1]) > 0) {
                my_strcpy(temp, matriz[j]);
                my_strcpy(matriz[j], matriz[j+1]);
                my_strcpy(matriz[j+1], temp);
            }
        }
    }
}

int main() {

    char input[50];

    while (fgets(input, sizeof(input), stdin)) {

        int len = my_strlen(input);
        if (len > 0 && input[len - 1] == '\n')
            input[len - 1] = '\0';

        if (input[0] == '0' && input[1] == '\0')
            break;

        char matriz[50][50];
        int cont = 0;

        while (!(input[0] == '0' && input[1] == '\0')) {

            my_strcpy(matriz[cont], input);
            cont++;

            fgets(input, sizeof(input), stdin);

            len = my_strlen(input);
            if (len > 0 && input[len - 1] == '\n')
                input[len - 1] = '\0';
        }

        ordenar(cont, matriz);

        for (int i = 0; i < cont; i++)
            printf("%s\n", matriz[i]);

        printf("\n");
    }

    return 0;
}
