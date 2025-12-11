#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

#define MAX_GAMES 10000
#define MATRICULA "890191"
#define TAM_TAB 21

typedef struct {
    int id;
    char* name;
    char* releaseDate;
    int estimatedOwners;
    float price;
    char** supportedLanguages; int supportedLanguages_len;
    int metacriticScore;
    float userScore;
    int achievements;
    char** publishers; int publishers_len;
    char** developers; int developers_len;
    char** categories; int categories_len;
    char** genres; int genres_len;
    char** tags; int tags_len;
} Game;

static Game* jogos;

// ------------------- FUNÇÕES AUXILIARES DE STRING -------------------
static char* duplicarString(const char* s) {
    if (!s) {
        char* p = malloc(1);
        p[0] = '\0';
        return p;
    }
    size_t len = strlen(s);
    char* p = malloc(len + 1);
    if (!p) { perror("malloc"); exit(EXIT_FAILURE); }
    strcpy(p, s);
    return p;
}

static void apararEspacos(char* s) {
    if (!s) return;
    int i = 0;
    while (isspace((unsigned char)s[i])) i++;
    if (i) memmove(s, s + i, strlen(s + i) + 1);
    int len = strlen(s);
    while (len > 0 && isspace((unsigned char)s[len - 1])) s[--len] = '\0';
}

static void removerAspas(char* s) {
    if (!s) return;
    apararEspacos(s);
    int len = strlen(s);
    if (len >= 2 && ((s[0] == '"' && s[len - 1] == '"') || (s[0] == '\'' && s[len - 1] == '\''))) {
        memmove(s, s + 1, len - 2);
        s[len - 2] = '\0';
    }
}

static char** dividirPorVirgula(const char* s, int* len) {
    if (!s || strlen(s) == 0) { *len = 0; return NULL; }
    char* copia = duplicarString(s);
    char* token = strtok(copia, ",");
    char** arr = NULL;
    int count = 0;
    while (token) {
        apararEspacos(token);
        removerAspas(token);
        arr = realloc(arr, sizeof(char*) * (count + 1));
        arr[count++] = duplicarString(token);
        token = strtok(NULL, ",");
    }
    free(copia);
    *len = count;
    return arr;
}

static char** removerColchetes(const char* s, int* len) {
    if (!s) { *len = 0; return NULL; }
    char* copia = duplicarString(s);
    apararEspacos(copia);
    int L = strlen(copia);
    if (L >= 2 && copia[0] == '[' && copia[L - 1] == ']') {
        copia[L - 1] = '\0';
        memmove(copia, copia + 1, L - 1);
    }
    char** arr = dividirPorVirgula(copia, len);
    free(copia);
    return arr;
}

static char* arrayParaString(char** arr, int len) {
    if (!arr || len == 0) return duplicarString("[]");
    size_t bufsize = 4096;
    char* out = malloc(bufsize);
    if (!out) { perror("malloc"); exit(EXIT_FAILURE); }
    strcpy(out, "[");
    for (int i = 0; i < len; i++) {
        if (strlen(out) + strlen(arr[i]) + 4 >= bufsize) {
            bufsize *= 2;
            out = realloc(out, bufsize);
            if (!out) { perror("realloc"); exit(EXIT_FAILURE); }
        }
        strcat(out, arr[i]);
        if (i < len - 1) strcat(out, ", ");
    }
    strcat(out, "]");
    return out;
}

// ------------------- FUNÇÃO DE PRINTAR -------------------
static void imprimirJogo(Game* g) {
    printf("=> %d ## %s ## %s ## %d ## %.2f ## ",
           g->id, g->name, g->releaseDate, g->estimatedOwners, g->price);

    char* langs = arrayParaString(g->supportedLanguages, g->supportedLanguages_len);
    printf("%s ## ", langs);
    free(langs);

    printf("%d ## %.1f ## %d ## ", g->metacriticScore, g->userScore, g->achievements);

    char* pubs = arrayParaString(g->publishers, g->publishers_len);
    char* devs = arrayParaString(g->developers, g->developers_len);
    char* cats = arrayParaString(g->categories, g->categories_len);
    char* gens = arrayParaString(g->genres, g->genres_len);
    char* tags = arrayParaString(g->tags, g->tags_len);

    printf("%s ## %s ## %s ## %s ## %s ##\n", pubs, devs, cats, gens, tags);

    free(pubs); free(devs); free(cats); free(gens); free(tags);
}

// Função para limpar a string de \n e espaços finais
void clean(char *s){
    int len = strlen(s);
    while(len>0 && (s[len-1]=='\n' || s[len-1]=='\r')) s[--len]='\0';
}

// ------------------- LEITURA CSV -------------------
void salvarCampo(Game* g, int campo, const char* valor){
    switch(campo){
        case 0: g->id = valor ? atoi(valor) : 0; break;
        case 1: g->name = duplicarString(valor); removerAspas(g->name); break;
        case 2: g->releaseDate = duplicarString(valor ? valor : "01/01/0000"); break;
        case 3: g->estimatedOwners = valor ? atoi(valor) : 0; break;
        case 4: g->price = valor ? atof(valor) : 0.0f; break;
        case 5: g->supportedLanguages = removerColchetes(valor, &g->supportedLanguages_len); break;
        case 6: g->metacriticScore = valor ? atoi(valor) : -1; break;
        case 7: g->userScore = valor ? atof(valor) : -1.0f; break;
        case 8: g->achievements = valor ? atoi(valor) : 0; break;
        case 9: g->publishers = dividirPorVirgula(valor, &g->publishers_len); break;
        case 10: g->developers = dividirPorVirgula(valor, &g->developers_len); break;
        case 11: g->categories = removerColchetes(valor, &g->categories_len); break;
        case 12: g->genres = removerColchetes(valor, &g->genres_len); break;
        case 13: g->tags = removerColchetes(valor, &g->tags_len); break;
    }
}

void lerLinhaCsv(char* linha, int idx){
    int campo = 0, tam = 0, col = 0;
    char acumulado[20000]; int asp = 0;
    for(int j=0; linha[j]; j++){
        char c = linha[j];
        if(c=='"') { asp = !asp; continue; }
        if(c=='[') col++; if(c==']') col--;
        if(c==',' && !asp && col==0){
            acumulado[tam]='\0';
            salvarCampo(&jogos[idx], campo++, acumulado);
            tam=0;
        } else acumulado[tam++] = c;
    }
    acumulado[tam]='\0';
    salvarCampo(&jogos[idx], campo, acumulado);
}

int loadCSVFull(const char *file, Game *arr){
    FILE *fp = fopen(file,"r");
    if(!fp) return 0;
    char line[20000]; int count=0;
    if(!fgets(line,sizeof(line),fp)){ fclose(fp); return 0; } // cabeçalho
    while(fgets(line,sizeof(line),fp)){
        line[strcspn(line,"\r\n")]='\0';
        lerLinhaCsv(line,count++);
        if(count>=MAX_GAMES) break;
    }
    fclose(fp);
    return count;
}

// ------------------- TABELA HASH INDIRETA (LISTA SIMPLES) -------------------
typedef struct Node {
    Game game;
    struct Node* next;
} Node;

typedef struct {
    Node* buckets[TAM_TAB];
    long comparacoes;
} HashTable;

long comparacoes_global = 0;

int asciiHash(const char* name){
    if (!name) return 0;
    int sum = 0;
    for (int i=0; name[i]; i++) sum += (unsigned char)name[i];
    return sum % TAM_TAB;
}

Node* criarNo(Game g){
    Node* n = malloc(sizeof(Node));
    if(!n){ perror("malloc"); exit(EXIT_FAILURE); }
    n->game = g;
    n->next = NULL;
    return n;
}

void inicializarHash(HashTable* ht){
    for(int i=0;i<TAM_TAB;i++) ht->buckets[i] = NULL;
    ht->comparacoes = 0;
}

void inserirHash(HashTable* ht, Game g){
    int idx = asciiHash(g.name);
    Node* head = ht->buckets[idx];
    if (!head) {
        ht->buckets[idx] = criarNo(g);
        return;
    }
    // verificar duplicata e inserir no final
    Node* cur = head;
    while(cur->next){
        ht->comparacoes++; comparacoes_global++; // comparação por nome
        if(strcmp(cur->game.name, g.name) == 0) return; // duplicado
        cur = cur->next;
    }
    // última comparação com o último nó (antes de anexar)
    ht->comparacoes++; comparacoes_global++;
    if(strcmp(cur->game.name, g.name) == 0) return;
    cur->next = criarNo(g);
}

int pesquisarHash(HashTable* ht, char* nome, char* caminho) {
    int idx = asciiHash(nome);
    Node* cur = ht->buckets[idx];
    int pos = 0;

    while (cur) {
        if (strcmp(cur->game.name, nome) == 0) {
            sprintf(caminho, "(Posicao: %d) ", idx);
            return 1; // achou
        }
        cur = cur->next;
        pos++;
    }

    // não achou
    sprintf(caminho, "(Posicao: %d) ", idx);
    return 0;
}


// ------------------- PESQUISA E IMPRESSÃO -------------------
void pesquisarEImprimirHash(HashTable* ht, char* nome){
    char caminho[1024];
    caminho[0] = '\0';
    int achou = pesquisarHash(ht, nome, caminho);
    if(achou)
        printf("%s:  %sSIM\n", nome, caminho);
    else
        printf("%s:  %sNAO\n", nome, caminho);
}

// ------------------- MAIN -------------------
int main(){
    jogos = malloc(sizeof(Game)*MAX_GAMES);
    if(!jogos){ perror("malloc"); return 1; }

    int nGames = loadCSVFull("/tmp/games.csv", jogos);
    if(nGames == 0){
        fprintf(stderr, "Erro ao abrir /tmp/games.csv ou arquivo vazio\n");
        free(jogos);
        return 1;
    }

    HashTable ht;
    inicializarHash(&ht);

    char line[256]; Game lista[MAX_GAMES]; int nLista=0;

    // Ler IDs para inserção (stdin)
    while(fgets(line,sizeof(line),stdin)){
        clean(line);
        if(strcmp(line,"FIM")==0) break;
        int id = atoi(line);
        for(int i=0;i<nGames;i++){
            if(jogos[i].id==id){ lista[nLista++] = jogos[i]; break; }
        }
    }

    clock_t inicio = clock();
    // inserir na tabela hash
    for(int i=0;i<nLista;i++) inserirHash(&ht, lista[i]);

    // Pesquisas por nome
    while(fgets(line,sizeof(line),stdin)){
        clean(line);
        if(strcmp(line,"FIM")==0) break;
        pesquisarEImprimirHash(&ht, line);
    }
    clock_t fim = clock();

    // Criar log
    char filename[64];
    sprintf(filename, "%s_hashIndireta.txt", MATRICULA);
    FILE *log = fopen(filename,"w");
    if(log){
        fprintf(log,"%s\nNUMERO DE COMPARACOES: %ld\nTEMPO DE EXECUCAO (ms): %ld\n",
                MATRICULA, ht.comparacoes, (fim-inicio)*1000/CLOCKS_PER_SEC);
        fclose(log);
    } else {
        perror("fopen log");
    }


    free(jogos);
    return 0;
}
