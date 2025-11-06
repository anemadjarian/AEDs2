#include <stdio.h>
#include <string.h>

int main() {
    char comando[10];

    while (1) {
        scanf("%s", comando);

        if (strcmp(comando, "FIM") == 0) {
            break;
        }

        int n = atoi(comando);
        int m;
        scanf("%d", &m);

        int matriz[n][m];
        int resp[n][m];

        // Ler matriz
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                scanf("%d", &matriz[i][j]);
            }
        }

        // Processar matriz
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (matriz[i][j] == 1) {
                    resp[i][j] = 9;
                } else {
                    int soma = 0;

                    if (i > 0) soma += matriz[i - 1][j];       // cima
                    if (i < n - 1) soma += matriz[i + 1][j];   // baixo
                    if (j > 0) soma += matriz[i][j - 1];       // esquerda
                    if (j < m - 1) soma += matriz[i][j + 1];   // direita

                    resp[i][j] = soma;
                }
            }
        }

        // Mostrar resultado
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                printf("%d", resp[i][j]);
            }
            printf("\n");
        }
    }

    return 0;
}
