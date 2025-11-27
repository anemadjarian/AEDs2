#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

#define MAX_GAMES 10000
#define MATRICULA "890191"

// ------------------- ESTRUTURA DO JOGO -------------------
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
    if (!s) return duplicarString("");
    size_t len = strlen(s);
    char* p = malloc(len + 1);
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
    if ((s[0] == '"' && s[len - 1] == '"') || (s[0] == '\'' && s[len - 1] == '\'')) {
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
        arr = realloc(arr, sizeof(char*) * (count + 1));
        apararEspacos(token);
        removerAspas(token);
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
    char* out = malloc(4096);
    strcpy(out, "[");
    for (int i = 0; i < len; i++) {
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

// Função para limpar a string de \n e espaços
void clean(char *s){
    int len = strlen(s);
    while(len>0 && (s[len-1]=='\n' || s[len-1]=='\r')) s[--len]='\0';
}

// ------------------- AVL -------------------
typedef struct NoAVL {
    Game game;
    struct NoAVL *esq;
    struct NoAVL *dir;
    int altura;
} NoAVL;

typedef struct {
    NoAVL *raiz;
    long comparacoes;
} ArvoreAVL;

int altura(NoAVL *n) { return (n==NULL)?0:n->altura; }
int fator(NoAVL *n) { return (n==NULL)?0:altura(n->esq)-altura(n->dir); }

NoAVL* rotacaoDir(NoAVL *y){
    NoAVL *x = y->esq;
    NoAVL *T2 = x->dir;
    x->dir = y;
    y->esq = T2;
    y->altura = 1 + ((altura(y->esq)>altura(y->dir))?altura(y->esq):altura(y->dir));
    x->altura = 1 + ((altura(x->esq)>altura(x->dir))?altura(x->esq):altura(x->dir));
    return x;
}

NoAVL* rotacaoEsq(NoAVL *x){
    NoAVL *y = x->dir;
    NoAVL *T2 = y->esq;
    y->esq = x;
    x->dir = T2;
    x->altura = 1 + ((altura(x->esq)>altura(x->dir))?altura(x->esq):altura(x->dir));
    y->altura = 1 + ((altura(y->esq)>altura(y->dir))?altura(y->esq):altura(y->dir));
    return y;
}

// Inserção AVL
NoAVL* inserirRec(ArvoreAVL *arv, NoAVL *no, Game g){
    if(no==NULL){
        NoAVL *novo = malloc(sizeof(NoAVL));
        novo->game = g;
        novo->esq = novo->dir = NULL;
        novo->altura = 1;
        return novo;
    }

    arv->comparacoes++;
    int cmp = strcmp(g.name, no->game.name);
    if(cmp < 0) no->esq = inserirRec(arv, no->esq, g);
    else if(cmp > 0) no->dir = inserirRec(arv, no->dir, g);
    else return no;

    no->altura = 1 + ((altura(no->esq) > altura(no->dir)) ? altura(no->esq) : altura(no->dir));
    int fb = fator(no);

    if(fb > 1 && strcmp(g.name, no->esq->game.name) < 0) return rotacaoDir(no);
    if(fb < -1 && strcmp(g.name, no->dir->game.name) > 0) return rotacaoEsq(no);
    if(fb > 1 && strcmp(g.name, no->esq->game.name) > 0){ no->esq = rotacaoEsq(no->esq); return rotacaoDir(no);}
    if(fb < -1 && strcmp(g.name, no->dir->game.name) < 0){ no->dir = rotacaoDir(no->dir); return rotacaoEsq(no);}
    return no;
}

void inserir(ArvoreAVL *arv, Game g){ arv->raiz = inserirRec(arv, arv->raiz, g); }
int pesquisarRec(ArvoreAVL *arv, NoAVL *no, char *nome, char *caminho){
    if(no == NULL){
        return 0;
    }

    arv->comparacoes++;

    int cmp = strcmp(nome, no->game.name);

    if(cmp == 0){
        return 1;
    }
    else if(cmp < 0){
        strcat(caminho, "esq ");
        return pesquisarRec(arv, no->esq, nome, caminho);
    }
    else{
        strcat(caminho, "dir ");
        return pesquisarRec(arv, no->dir, nome, caminho);
    }
}


void pesquisarEImprimir(ArvoreAVL *arv, char *nome){
    char caminho[500];
    strcpy(caminho, "raiz ");

    int achou = pesquisarRec(arv, arv->raiz, nome, caminho);

    if(achou)
        printf("%s: %sSIM\n\n", nome, caminho);
    else
        printf("%s: %sNAO\n\n", nome, caminho);
}


// Pesquisa com caminho
int pesquisarRec(ArvoreAVL *arv, NoAVL *no, char *nome, char *caminho){
    if(no == NULL) return 0;

    arv->comparacoes++;

    int cmp = strcmp(nome, no->game.name);

    if(cmp == 0){
        return 1;
    }
    else if(cmp < 0){
        strcat(caminho, "esq ");
        return pesquisarRec(arv, no->esq, nome, caminho);
    }
    else{
        strcat(caminho, "dir ");
        return pesquisarRec(arv, no->dir, nome, caminho);
    }
}

int pesquisarComCaminho(ArvoreAVL *arv, char *nome, char *caminho){
    strcpy(caminho, "raiz ");
    return pesquisarRec(arv, arv->raiz, nome, caminho);
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
    fgets(line,sizeof(line),fp); // cabeçalho
    while(fgets(line,sizeof(line),fp)){
        line[strcspn(line,"\r\n")]='\0';
        lerLinhaCsv(line,count++);
        if(count>=MAX_GAMES) break;
    }
    fclose(fp);
    return count;
}

// ------------------- MAIN -------------------
int main(){
    jogos = malloc(sizeof(Game)*MAX_GAMES);
    int nGames = loadCSVFull("games.csv", jogos);

    ArvoreAVL arv; arv.raiz=NULL; arv.comparacoes=0;
    char line[256]; Game lista[MAX_GAMES]; int nLista=0;

    // Ler IDs para inserção
    while(fgets(line,sizeof(line),stdin)){
        clean(line);
        if(strcmp(line,"FIM")==0) break;
        int id = atoi(line);
        for(int i=0;i<nGames;i++){
            if(jogos[i].id==id){ lista[nLista++] = jogos[i]; break; }
        }
    }

    clock_t inicio = clock();
    for(int i=0;i<nLista;i++) inserir(&arv, lista[i]);

    // Pesquisas
    while(fgets(line,sizeof(line),stdin)){
    clean(line);
    if(strcmp(line,"FIM")==0) break;
    pesquisarEImprimir(&arv, line);
}

    clock_t fim = clock();

    // Criar log
    FILE *log = fopen(MATRICULA "_arvoreAVL.txt","w");
    fprintf(log,"890191\nNUMERO DE COMPARACOES: %ld\nTEMPO DE EXECUCAO (ms): %ld\n",
            arv.comparacoes,(fim-inicio)*1000/CLOCKS_PER_SEC);
    fclose(log);

    free(jogos);
    return 0;
}
