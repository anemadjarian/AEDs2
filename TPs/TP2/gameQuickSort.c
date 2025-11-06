#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MAX_STRING 100
#define MAX_ARRAY 50
#define MAX_LINE 1000
#define MATRICULA "890191"

// Variáveis globais para contagem
long long numComparacoes = 0;
long long numMovimentacoes = 0;

//---------------------------------------------------------------
// Struct
//---------------------------------------------------------------
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

//---------------------------------------------------------------
// Funções Auxiliares
//---------------------------------------------------------------
char *extrairCampoEntreAspas(char **linha)
{
    char *inicio = strchr(*linha, '\"'); // Procura aspas, anda de caracter de caracter
    if (!inicio)
        return NULL; // Retorna null se não tiver
    inicio++;
    char *fim = strchr(inicio, '\"'); // procura aspas depois de ter achado a primeira
    if (!fim)
        return NULL; // Retorna null se não tiver

    int tamanho = fim - inicio;
    char *campo = (char *)malloc(tamanho + 1); // Aloca tamanho para o tamanho da string
    strncpy(campo, inicio, tamanho);           // Copia os caracteres a partir do ponteiro inicio e coloca dentro de campo
    campo[tamanho] = '\0';
    *linha = fim + 1; // vai pra proxima palavra da linha

    return campo;
}

void cleanString(char *str)
{
    int len = strlen(str); // tamanho
    int start = 0, end = len - 1;

    while (start <= end && (str[start] == '\'' || str[start] == ' '))
        start++; // Pula caracteres não desejados
    while (end >= start && (str[end] == '\'' || str[end] == ' '))
        end--; // Pula caracteres não desejados de tras pra frente

    int j = 0;
    for (int i = start; i <= end; i++)
        str[j++] = str[i]; // Muda a string
    str[j] = '\0';
}

void transformArray(char *token, char arr[MAX_ARRAY][MAX_STRING], int *qtd)
{
    // Para array de strings
    int j = 0;
    char *aux = strtok(token, "[]\","); // Dividir string em partes menores, qualquer caracter dentro de aspas
    while (aux != NULL)
    {
        cleanString(aux); // Limpa a string
        if (strlen(aux) > 0)
            strcpy(arr[j++], aux);    // Se o array não estiver vazio, copia o aux para array original
        aux = strtok(NULL, "[]\","); // Chama strtok novamente para pegar o próximo token
    }
    *qtd = j; // Numeros de strings
}

void lerArray(char **ptr, char arr[MAX_ARRAY][MAX_STRING], int *qtd)
{
    *qtd = 0;
    while (**ptr == ' ')
        (*ptr)++; // Ignora espaços

    if (**ptr == '\"')
    { // Se começar com aspas, chama o remover aspas e depois separa as strings
        char *campoAspa = extrairCampoEntreAspas(ptr);
        transformArray(campoAspa, arr, qtd);
        free(campoAspa);
    }
    else if (**ptr == '[')
    { // Se começar com [
        int k = 0;
        char campoArr[MAX_LINE];   // Buffer
        campoArr[k++] = *(*ptr)++; // Copia [ para o buffer e avança um caracter
        while (**ptr != '\0' && **ptr != ']')
            campoArr[k++] = *(*ptr)++; // Copia todos os caracteres dentro dos colchetes, até encontrar ] ou o fim da string
        if (**ptr == ']')
            campoArr[k++] = *(*ptr)++; // Copia ] para o buffer
        campoArr[k] = '\0';
        transformArray(campoArr, arr, qtd); // Separa strings
    }
    else if (**ptr != ',' && **ptr != '\0')
    { // Se não tiver virgula considera uma string so
        int k = 0;
        char campoSimples[MAX_LINE];
        while (**ptr != ',' && **ptr != '\0')
            campoSimples[k++] = *(*ptr)++;
        campoSimples[k] = '\0';
        cleanString(campoSimples);
        if (strlen(campoSimples) > 0)
        {
            strcpy(arr[0], campoSimples);
            *qtd = 1;
        }
    }
    if (**ptr == ',')
        (*ptr)++; // Pula a virgula do final
}

void transformDate(char *token, int cont, Game *jogo)
{
    char dia[3] = "01", mes[3] = "01", ano[5];
    char *virgula = strchr(token, ','); // Procura virgula

    // DIA
    if (virgula != NULL && (virgula - token) >= 2)
    {
        if (*(virgula - 2) >= '0' && *(virgula - 2) <= '9')
            dia[0] = *(virgula - 2); // Copia o primeiro digito do dia
        if (*(virgula - 1) >= '0' && *(virgula - 1) <= '9')
            dia[1] = *(virgula - 1); // Copia o segundo digito do dia
    }

    // MES
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

    // ANO
    int len = strlen(token); // Tamanho do token
    if (len >= 4)
        strcpy(ano, token + len - 4); // Copia os quatro ultimos numeros
    else
        strcpy(ano, "0000");

    sprintf(jogo[cont].releaseDate, "%s/%s/%s", dia, mes, ano);
}

void cleanNumber(char *token, char *newStr)
{
    int j = 0;
    for (int i = 0; token[i] != '\0'; i++)
    {
        if ((token[i] >= '0' && token[i] <= '9') || token[i] == '.' || token[i] == ',') // Verifca se é numero
            newStr[j++] = (token[i] == ',') ? '.' : token[i];                           // muda virgula pra ponto
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

        // Comparação correta para "0.0"
        if (strcmp(newStr, "0.0") == 0)
            jogo[cont].price = 0.0;
        else
            jogo[cont].price = (strlen(newStr) > 0) ? atof(newStr) : 0.0;
    }
}

//---------------------------------------------------------------
// Preencher
//---------------------------------------------------------------
void preencheVetor(Game jogo[], char line[], int cont)
{
    // Inicioaliza contadores de strings
    jogo[cont].qtdLanguages = 0;
    jogo[cont].qtdPublishers = 0;
    jogo[cont].qtdDevelopers = 0;
    jogo[cont].qtdCategories = 0;
    jogo[cont].qtdGenres = 0;
    jogo[cont].qtdTags = 0;

    // Variáveis úteis
    line[strcspn(line, "\n")] = '\0'; // Transforma /n em /0
    char *ptr = line;                 // Ponteiro para os caracteres da linha
    char campo[MAX_LINE];
    char newStr[20];
    int i;

    // ID
    i = 0;
    while (*ptr != ',' && *ptr != '\0')
        campo[i++] = *ptr++; // Percorre a linha até achar , ou fim da string
    campo[i] = '\0';
    jogo[cont].id = atoi(campo);
    if (*ptr == ',')
        ptr++;

    // Nome
    i = 0;
    while (*ptr != ',' && *ptr != '\0')
        campo[i++] = *ptr++;
    campo[i] = '\0';
    strcpy(jogo[cont].name, campo);
    if (*ptr == ',')
        ptr++;

    // Data
    if (*ptr == '\"')
    { // Se começar com aspas limpa aspas e tranforma data
        char *dataCampo = extrairCampoEntreAspas(&ptr);
        transformDate(dataCampo, cont, jogo);
        free(dataCampo);
        if (*ptr == ',')
            ptr++;
    }
    else
    {
        strcpy(jogo[cont].releaseDate, "01/01/0000");
        while (*ptr != ',' && *ptr != '\0')
            ptr++;
        if (*ptr == ',')
            ptr++;
    }

    // Estimated Owners
    i = 0;
    while (*ptr != ',' && *ptr != '\0')
        campo[i++] = *ptr++;
    campo[i] = '\0';
    cleanNumber(campo, newStr);
    jogo[cont].estimatedOwners = atoi(newStr);
    if (*ptr == ',')
        ptr++;

    // Price
    i = 0;
    while (*ptr != ',' && *ptr != '\0')
        campo[i++] = *ptr++;
    campo[i] = '\0';
    freeToPlay(campo, cont, jogo);
    if (*ptr == ',')
        ptr++;

    // Arrays
    lerArray(&ptr, jogo[cont].supportedLanguages, &jogo[cont].qtdLanguages); // Verifica os arrays de string

    // Metacritic
    i = 0;
    while (*ptr != ',' && *ptr != '\0')
        campo[i++] = *ptr++;
    campo[i] = '\0';
    jogo[cont].metacriticScore = (strlen(campo) > 0) ? atoi(campo) : 0;
    if (*ptr == ',')
        ptr++;

    // UserScore
    i = 0;
    while (*ptr != ',' && *ptr != '\0')
        campo[i++] = *ptr++;
    campo[i] = '\0';
    if (strcmp(campo, "tbd") == 0 || strlen(campo) == 0)
        jogo[cont].userScore = -1.0;
    else
        jogo[cont].userScore = atof(campo);
    if (*ptr == ',')
        ptr++;

    // Achievements
    i = 0;
    while (*ptr != ',' && *ptr != '\0')
        campo[i++] = *ptr++;
    campo[i] = '\0';
    jogo[cont].achievements = (strlen(campo) > 0) ? atoi(campo) : 0;
    if (*ptr == ',')
        ptr++;

    // Outros arrays
    lerArray(&ptr, jogo[cont].publishers, &jogo[cont].qtdPublishers);
    lerArray(&ptr, jogo[cont].developers, &jogo[cont].qtdDevelopers);
    lerArray(&ptr, jogo[cont].categories, &jogo[cont].qtdCategories);
    lerArray(&ptr, jogo[cont].genres, &jogo[cont].qtdGenres);
    lerArray(&ptr, jogo[cont].tags, &jogo[cont].qtdTags);
}

//---------------------------------------------------------------
// Funções Principais
//---------------------------------------------------------------
void swap(Game *a, Game *b)
{
    Game temp = *a;
    *a = *b;
    *b = temp;
    numMovimentacoes += 3;
}

void inverteDataParaComparacao(char *dataOriginal, char *dataInvertida) {
    // dataOriginal esperada: DD/MM/AAAA
    char dia[3], mes[3], ano[5];

    // Extrai dia, mês e ano
    strncpy(dia, dataOriginal, 2);
    dia[2] = '\0';
    strncpy(mes, dataOriginal + 3, 2);
    mes[2] = '\0';
    strncpy(ano, dataOriginal + 6, 4);
    ano[4] = '\0';

    // Monta data invertida AAAA/MM/DD
    sprintf(dataInvertida, "%s/%s/%s", ano, mes, dia);
}


void inverteData(char *dataOriginal, char *dataNova) {
    char dia[3] = "01", mes[3] = "01", ano[5];
    char *virgula = strchr(dataOriginal, ','); // Corrigido

    // DIA
    if (virgula != NULL && (virgula - dataOriginal) >= 2) {
        if (*(virgula - 2) >= '0' && *(virgula - 2) <= '9')
            dia[0] = *(virgula - 2);
        if (*(virgula - 1) >= '0' && *(virgula - 1) <= '9')
            dia[1] = *(virgula - 1);
    }

    // MES
    if (strstr(dataOriginal, "Jan")) strcpy(mes, "01");
    else if (strstr(dataOriginal, "Feb")) strcpy(mes, "02");
    else if (strstr(dataOriginal, "Mar")) strcpy(mes, "03");
    else if (strstr(dataOriginal, "Apr")) strcpy(mes, "04");
    else if (strstr(dataOriginal, "May")) strcpy(mes, "05");
    else if (strstr(dataOriginal, "Jun")) strcpy(mes, "06");
    else if (strstr(dataOriginal, "Jul")) strcpy(mes, "07");
    else if (strstr(dataOriginal, "Aug")) strcpy(mes, "08");
    else if (strstr(dataOriginal, "Sep")) strcpy(mes, "09");
    else if (strstr(dataOriginal, "Oct")) strcpy(mes, "10");
    else if (strstr(dataOriginal, "Nov")) strcpy(mes, "11");
    else if (strstr(dataOriginal, "Dec")) strcpy(mes, "12");

    // ANO
    int len = strlen(dataOriginal);
    if (len >= 4)
        strcpy(ano, dataOriginal + len - 4);
    else
        strcpy(ano, "0000");

    sprintf(dataNova, "%s/%s/%s", ano, mes, dia);
}

int comparar(Game a, Game b) {
    char dataA[11], dataB[11];

    inverteDataParaComparacao(a.releaseDate, dataA);
    inverteDataParaComparacao(b.releaseDate, dataB);

    int comp = strcmp(dataA, dataB);
    if (comp != 0)
        return comp; // ordena por data
    else
        return a.id - b.id; // se datas iguais, ordena por ID
}


// QuickSort
void quickSort(Game *vetor, int inicio, int fim) {
    int i = inicio;
    int j = fim;
    Game pivo = vetor[(inicio + fim) / 2];

    while (i <= j) {
        while (comparar(vetor[i], pivo) < 0) i++;
        while (comparar(vetor[j], pivo) > 0) j--;

        if (i <= j) {
            swap(&vetor[i], &vetor[j]);
            i++;
            j--;
        }
    }

    if (inicio < j) quickSort(vetor, inicio, j);
    if (i < fim) quickSort(vetor, i, fim);
}


void printar(Game *jogo, int resp)
{
    if (jogo[resp].price == 0 || ((int)(jogo[resp].price * 10)) % 10 == 0)
    {
        printf("=> %d ## %s ## %s ## %d ## %.1f ## [",
               jogo[resp].id, jogo[resp].name, jogo[resp].releaseDate,
               jogo[resp].estimatedOwners, jogo[resp].price);
    }
    else
    {
        printf("=> %d ## %s ## %s ## %d ## %.2f ## [",
               jogo[resp].id, jogo[resp].name, jogo[resp].releaseDate,
               jogo[resp].estimatedOwners, jogo[resp].price);
    }

    for (int i = 0; i < jogo[resp].qtdLanguages; i++)
        printf("%s%s", jogo[resp].supportedLanguages[i],
               (i < jogo[resp].qtdLanguages - 1) ? ", " : "");
    printf("] ## %d ## %.1f ## %d ## [",
           jogo[resp].metacriticScore, jogo[resp].userScore, jogo[resp].achievements);

    for (int i = 0; i < jogo[resp].qtdPublishers; i++)
        printf("%s%s", jogo[resp].publishers[i],
               (i < jogo[resp].qtdPublishers - 1) ? ", " : "");
    printf("] ## [");

    for (int i = 0; i < jogo[resp].qtdDevelopers; i++)
        printf("%s%s", jogo[resp].developers[i],
               (i < jogo[resp].qtdDevelopers - 1) ? ", " : "");
    printf("] ## [");

    for (int i = 0; i < jogo[resp].qtdCategories; i++)
        printf("%s%s", jogo[resp].categories[i],
               (i < jogo[resp].qtdCategories - 1) ? ", " : "");
    printf("] ## [");

    for (int i = 0; i < jogo[resp].qtdGenres; i++)
        printf("%s%s", jogo[resp].genres[i],
               (i < jogo[resp].qtdGenres - 1) ? ", " : "");
    printf("] ## [");

    for (int i = 0; i < jogo[resp].qtdTags; i++)
        printf("%s%s", jogo[resp].tags[i],
               (i < jogo[resp].qtdTags - 1) ? ", " : "");
    printf("] ##\n");
}

void escreverLog(double tempo)
{
    FILE *log = fopen(MATRICULA "_quicsort.txt", "w");
    if (log != NULL)
    {
        fprintf(log, "%s\t%lld\t%lld\t%lf\n",
                MATRICULA, numComparacoes, numMovimentacoes, tempo);
        fclose(log);
    }
    else
    {
        fprintf(stderr, "ERRO: Nao foi possivel criar o arquivo de log.\n");
    }
}

//---------------------------------------------------------------
// Função principal
//---------------------------------------------------------------
int main()
{
    // Meu array jogos
    Game *jogo = (Game *)malloc(2000 * sizeof(Game));
    if (!jogo)
    {
        printf("Erro de alocacao\n");
        return 1;
    }

    // Arquivo CSV
    FILE *arq = fopen("/tmp/games.csv", "r");
    if (!arq)
    {
        printf("Erro ao abrir o arquivo.\n");
        free(jogo);
        return 1;
    }

    // Linhas
    char line[MAX_LINE];
    int cont = 0;

    fgets(line, sizeof(line), arq); // Cabeçalho
    while (fgets(line, sizeof(line), arq) != NULL)
    {
        preencheVetor(jogo, line, cont);
        cont++;
    }
    fclose(arq);

    // Pesquisa
    int cont2 = 0;
    Game *pesquisa = (Game *)malloc(cont * sizeof(Game));
    if (!pesquisa)
    {
        printf("Erro de alocacao no array de pesuisa\n");
        return 1;
    }
    char idInput[50];
    if (fgets(idInput, sizeof(idInput), stdin) == NULL)
    {
        free(jogo);
        return 0;
    }
    idInput[strcspn(idInput, "\n")] = '\0';
    while (strcmp(idInput, "FIM") != 0)
    {
        int idPesquisa = atoi(idInput);
        int encontrado = 0;
        for (int i = 0; i < cont; i++)
        {
            if (jogo[i].id == idPesquisa)
            {
                pesquisa[cont2] = jogo[i];
                cont2++;
                encontrado = 1;
                break;
            }
        }
        if (!encontrado)
            printf("Jogo com ID %d nao encontrado.\n", idPesquisa);

        if (fgets(idInput, sizeof(idInput), stdin) == NULL)
            break;
        idInput[strcspn(idInput, "\n")] = '\0';
    }

    // Ordenação
    clock_t inicio = clock(); // Tempo de execução
    quickSort(pesquisa, 0, cont2-1);
    clock_t fim = clock();
    double tempoExecucao = (double)(fim - inicio) / CLOCKS_PER_SEC;

    // Printar
    for (int i = 0; i < cont2; i++)
    {
        printar(pesquisa, i);
    }

    // Arquivo LOG
    escreverLog(tempoExecucao);

    free(pesquisa);
    free(jogo);
    return 0;
}