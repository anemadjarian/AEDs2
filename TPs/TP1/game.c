#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_STRING 100
#define MAX_ARRAY 50
#define MAX_LINE 1000

typedef struct Game
{
    int id;
    char name[MAX_STRING];
    char releaseDate[11];
    int estimatedOwners;
    float price;

    char supportedLanguages[MAX_ARRAY][MAX_STRING];
    int qtdLanguages;

    int metacriticScore;
    float userScore;
    int achievements;

    char publishers[MAX_ARRAY][MAX_STRING];
    int qtdPublishers;

    char developers[MAX_ARRAY][MAX_STRING];
    int qtdDevelopers;

    char categories[MAX_ARRAY][MAX_STRING];
    int qtdCategories;

    char genres[MAX_ARRAY][MAX_STRING];
    int qtdGenres;

    char tags[MAX_ARRAY][MAX_STRING];
    int qtdTags;

} Game;

//-----Funções Auxiliares-----
char *extrairCampoEntreAspas(char **linha)
{
    // Extrai manualmente o conteúdo entre aspas duplas (") e avança o ponteiro

    char *inicio = strchr(*linha, '\"');
    if (!inicio)
        return NULL;
    inicio++; // pula a primeira aspa

    char *fim = strchr(inicio, '\"');
    if (!fim)
        return NULL;

    int tamanho = fim - inicio;
    char *campo = (char *)malloc(tamanho + 1);
    strncpy(campo, inicio, tamanho);
    campo[tamanho] = '\0';

    *linha = fim + 1; // avança o ponteiro da linha para depois da última aspa
    return campo;
}

void cleanString(char *str)
{
    // Remove aspas simples e espaços no início/fim
    int len = strlen(str);
    int start = 0, end = len - 1;

    while (start <= end && (str[start] == '\'' || str[start] == ' '))
        start++;
    while (end >= start && (str[end] == '\'' || str[end] == ' '))
        end--;

    int j = 0;
    for (int i = start; i <= end; i++)
        str[j++] = str[i];
    str[j] = '\0';
}

void transformArray(char *token, char arr[MAX_ARRAY][MAX_STRING], int *qtd)
{
    int j = 0;
    // strtok vai separar usando colchetes, aspas simples e duplas, vírgula
    char *aux = strtok(token, "[]\"',");
    while (aux != NULL)
    {
        cleanString(aux); // remove espaços e aspas simples restantes
        if (strlen(aux) > 0)
        {
            strcpy(arr[j++], aux);
        }
        aux = strtok(NULL, "[]\"',");
    }
    *qtd = j;
}

void lerArray(char **ptr, char arr[MAX_ARRAY][MAX_STRING], int *qtd)
{
    *qtd = 0;
    while (**ptr == ' ') (*ptr)++; // pula espaços

    if (**ptr == '\"')
    {
        char *campoAspa = extrairCampoEntreAspas(ptr);
        transformArray(campoAspa, arr, qtd);
        free(campoAspa);
    }
    else if (**ptr == '[')
    {
        int k = 0;
        char campoArr[MAX_LINE];
        campoArr[k++] = *(*ptr)++;
        while (**ptr != '\0' && **ptr != ']') campoArr[k++] = *(*ptr)++;
        if (**ptr == ']') campoArr[k++] = *(*ptr)++;
        campoArr[k] = '\0';
        transformArray(campoArr, arr, qtd);
    }
    else if (**ptr != ',' && **ptr != '\0')
    {
        int k = 0;
        char campoSimples[MAX_LINE];
        while (**ptr != ',' && **ptr != '\0') campoSimples[k++] = *(*ptr)++;
        campoSimples[k] = '\0';
        cleanString(campoSimples);
        if (strlen(campoSimples) > 0)
        {
            strcpy(arr[0], campoSimples);
            *qtd = 1;
        }
    }

    if (**ptr == ',') (*ptr)++;
}

void transformDate(char *token, int cont, Game *jogo)
{
    char dia[3] = "01", mes[3] = "01", ano[5];

    // Dia
    char *virgula = strchr(token, ',');
    if (virgula != NULL && (virgula - token) >= 2)
    {
        if (*(virgula - 2) >= '0' && *(virgula - 2) <= '9')
            dia[0] = *(virgula - 2);
        if (*(virgula - 1) >= '0' && *(virgula - 1) <= '9')
            dia[1] = *(virgula - 1);
    }

    // Mês
    if (strstr(token, "Jan"))
        strcpy(mes, "01");
    else if (strstr(token, "Feb"))
        strcpy(mes, "02");
    else if (strstr(token, "Mar"))
        strcpy(mes, "03");
    else if (strstr(token, "Apr"))
        strcpy(mes, "04");
    else if (strstr(token, "May"))
        strcpy(mes, "05");
    else if (strstr(token, "Jun"))
        strcpy(mes, "06");
    else if (strstr(token, "Jul"))
        strcpy(mes, "07");
    else if (strstr(token, "Aug"))
        strcpy(mes, "08");
    else if (strstr(token, "Sep"))
        strcpy(mes, "09");
    else if (strstr(token, "Oct"))
        strcpy(mes, "10");
    else if (strstr(token, "Nov"))
        strcpy(mes, "11");
    else if (strstr(token, "Dec"))
        strcpy(mes, "12");

    // Ano
    int len = strlen(token);
    if (len >= 4)
        strcpy(ano, token + len - 4);
    else
        strcpy(ano, "0000");

    sprintf(jogo[cont].releaseDate, "%s/%s/%s", dia, mes, ano);
}

void cleanNumber(char *token, char *newStr)
{
    int j = 0;
    for (int i = 0; token[i] != '\0'; i++)
    {
        if ((token[i] >= '0' && token[i] <= '9') || token[i] == '.' || token[i] == ',')
            newStr[j++] = (token[i] == ',') ? '.' : token[i];
    }
    newStr[j] = '\0';
}

void freeToPlay(char *token, int cont, Game *jogo)
{
    if (token[0] == 'F')
        jogo[cont].price = 0.0;
    else
    {
        char newStr[20];
        cleanNumber(token, newStr);
        jogo[cont].price = (strlen(newStr) > 0) ? atof(newStr) : 0.0;
    }
}
//-----FIM Funções Auxliares-----

// Preencher Vetor
void preencheVetor(Game jogo[], char line[], int cont)
{
    jogo[cont].qtdLanguages = 0;
    jogo[cont].qtdPublishers = 0;
    jogo[cont].qtdDevelopers = 0;
    jogo[cont].qtdCategories = 0;
    jogo[cont].qtdGenres = 0;
    jogo[cont].qtdTags = 0;

    line[strcspn(line, "\n")] = '\0'; // remove \n
    char *ptr = line;
    char campo[MAX_LINE];
    char newStr[20];
    int i;

    // ---------- ID ----------
    i = 0;
    while (*ptr != ',' && *ptr != '\0') campo[i++] = *ptr++;
    campo[i] = '\0';
    jogo[cont].id = atoi(campo);
    if (*ptr == ',') ptr++;

    // ---------- Nome ----------
    i = 0;
    while (*ptr != ',' && *ptr != '\0') campo[i++] = *ptr++;
    campo[i] = '\0';
    strcpy(jogo[cont].name, campo);
    if (*ptr == ',') ptr++;

    // ---------- Data ----------
    if (*ptr == '\"')
    {
        char *dataCampo = extrairCampoEntreAspas(&ptr);
        transformDate(dataCampo, cont, jogo);
        free(dataCampo);
        if (*ptr == ',') ptr++;
    }
    else
    {
        strcpy(jogo[cont].releaseDate, "01/01/0000");
        while (*ptr != ',' && *ptr != '\0') ptr++;
        if (*ptr == ',') ptr++;
    }

    // ---------- Estimated Owners ----------
    i = 0;
    while (*ptr != ',' && *ptr != '\0') campo[i++] = *ptr++;
    campo[i] = '\0';
    cleanNumber(campo, newStr);
    jogo[cont].estimatedOwners = atoi(newStr);
    if (*ptr == ',') ptr++;

    // ---------- Price ----------
    i = 0;
    while (*ptr != ',' && *ptr != '\0') campo[i++] = *ptr++;
    campo[i] = '\0';
    freeToPlay(campo, cont, jogo);
    if (*ptr == ',') ptr++;

    // ---------- Supported Languages ----------
    lerArray(&ptr, jogo[cont].supportedLanguages, &jogo[cont].qtdLanguages);

    // ---------- Metacritic ----------
    i = 0;
    while (*ptr != ',' && *ptr != '\0') campo[i++] = *ptr++;
    campo[i] = '\0';
    jogo[cont].metacriticScore = (strlen(campo) > 0) ? atoi(campo) : 0;
    if (*ptr == ',') ptr++;

    // ---------- UserScore ----------
    i = 0;
    while (*ptr != ',' && *ptr != '\0') campo[i++] = *ptr++;
    campo[i] = '\0';
    if (strcmp(campo, "tbd") == 0 || strlen(campo) == 0)
        jogo[cont].userScore = -1.0;
    else
        jogo[cont].userScore = atof(campo);
    if (*ptr == ',') ptr++;

    // ---------- Achievements ----------
    i = 0;
    while (*ptr != ',' && *ptr != '\0') campo[i++] = *ptr++;
    campo[i] = '\0';
    jogo[cont].achievements = (strlen(campo) > 0) ? atoi(campo) : 0;
    if (*ptr == ',') ptr++;

    // ---------- Publishers ----------
    lerArray(&ptr, jogo[cont].publishers, &jogo[cont].qtdPublishers);

    // ---------- Developers ----------
    lerArray(&ptr, jogo[cont].developers, &jogo[cont].qtdDevelopers);

    // ---------- Categories ----------
    lerArray(&ptr, jogo[cont].categories, &jogo[cont].qtdCategories);

    // ---------- Genres ----------
    lerArray(&ptr, jogo[cont].genres, &jogo[cont].qtdGenres);

    // ---------- Tags ----------
    lerArray(&ptr, jogo[cont].tags, &jogo[cont].qtdTags);
}

// Pesquisa Sequencial
int pesquisaSequencial(Game *jogo, int n, int idPesquisa)
{
    for (int i = 0; i < n; i++)
        if (jogo[i].id == idPesquisa)
            return i;
    return -1;
}

// Mostrar
void printar(Game *jogo, int resp)
{
    printf("=> %d ## %s ## %s ## %d ## %.2f ## [",
           jogo[resp].id, jogo[resp].name, jogo[resp].releaseDate, jogo[resp].estimatedOwners, jogo[resp].price);

    for (int i = 0; i < jogo[resp].qtdLanguages; i++)
    {
        printf("%s%s", jogo[resp].supportedLanguages[i], (i < jogo[resp].qtdLanguages - 1) ? ", " : "");
    }
    printf("] ## %d ## %.1f ## %d ## [", jogo[resp].metacriticScore, jogo[resp].userScore, jogo[resp].achievements);

    for (int i = 0; i < jogo[resp].qtdPublishers; i++)
        printf("%s%s", jogo[resp].publishers[i], (i < jogo[resp].qtdPublishers - 1) ? ", " : "");
    printf("] ## [");
    for (int i = 0; i < jogo[resp].qtdDevelopers; i++)
        printf("%s%s", jogo[resp].developers[i], (i < jogo[resp].qtdDevelopers - 1) ? ", " : "");
    printf("] ## [");
    for (int i = 0; i < jogo[resp].qtdCategories; i++)
        printf("%s%s", jogo[resp].categories[i], (i < jogo[resp].qtdCategories - 1) ? ", " : "");
    printf("] ## [");
    for (int i = 0; i < jogo[resp].qtdGenres; i++)
        printf("%s%s", jogo[resp].genres[i], (i < jogo[resp].qtdGenres - 1) ? ", " : "");
    printf("] ## [");
    for (int i = 0; i < jogo[resp].qtdTags; i++)
        printf("%s%s", jogo[resp].tags[i], (i < jogo[resp].qtdTags - 1) ? ", " : "");
    printf("] ##\n");
}

int main()
{
    Game *jogo = (Game *)malloc(2000 * sizeof(Game));
    if (!jogo)
    {
        printf("Erro de alocacao\n");
        return 1;
    }

    FILE *arq = fopen("/tmp/games.csv", "r");
    if (!arq)
    {
        printf("Erro ao abrir o arquivo.\n");
        return 1;
    }

    char line[MAX_LINE];
    int cont = 0;

    fgets(line, sizeof(line), arq);
    while (fgets(line, sizeof(line), arq) != NULL)
    {
        preencheVetor(jogo, line, cont);
        cont++;
    }

    char idInput[50];
    int resp;

    fgets(idInput, sizeof(idInput), stdin); // tira o cabeçalho

    idInput[strcspn(idInput, "\n")] = '\0'; // tranforma o \n em \0

    while (strcmp(idInput, "FIM") != 0)
    {
        int idPesquisa = atoi(idInput);
        resp = pesquisaSequencial(jogo, cont, idPesquisa);

        if (resp != -1)
            printar(jogo, resp);
        else
            printf("Jogo com ID %d nao encontrado.\n", idPesquisa);

        fgets(idInput, sizeof(idInput), stdin);
        idInput[strcspn(idInput, "\n")] = '\0';
    }

    fclose(arq);
    free(jogo);
    return 0;
}