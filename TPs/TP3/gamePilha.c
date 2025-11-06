#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>

#ifdef _WIN32
#define strcasecmp _stricmp
#else
#include <strings.h>
#endif

// --------------------------------------------------
// ESTRUTURA DE DADOS
// --------------------------------------------------
typedef struct Game {
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

// --------------------------------------------------
// FUNÇÕES AUXILIARES DE STRING
// --------------------------------------------------
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
    if (!s || strlen(s) == 0) {
        *len = 0;
        return NULL;
    }

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
    if (!s) {
        *len = 0;
        return NULL;
    }

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

// --------------------------------------------------
// FUNÇÕES DE DATA
// --------------------------------------------------
static const char* mesParaNumero(const char* mes) {
    if (!mes) return "01";
    char m[4] = {tolower(mes[0]), tolower(mes[1]), tolower(mes[2]), 0};

    if (!strcmp(m, "jan")) return "01";
    if (!strcmp(m, "feb")) return "02";
    if (!strcmp(m, "mar")) return "03";
    if (!strcmp(m, "apr")) return "04";
    if (!strcmp(m, "may")) return "05";
    if (!strcmp(m, "jun")) return "06";
    if (!strcmp(m, "jul")) return "07";
    if (!strcmp(m, "aug")) return "08";
    if (!strcmp(m, "sep")) return "09";
    if (!strcmp(m, "oct")) return "10";
    if (!strcmp(m, "nov")) return "11";
    if (!strcmp(m, "dec")) return "12";

    return "01";
}

// --------------------------------------------------
// LEITURA CSV
// --------------------------------------------------
void salvarCampo(Game* g, int campo, const char* valor) {
    switch (campo) {
        case 0:
            g->id = valor && *valor ? atoi(valor) : 0;
            break;

        case 1:
            g->name = duplicarString(valor);
            removerAspas(g->name);
            break;

        case 2: {
            char mes[8], dia[8], ano[8];
            if (sscanf(valor, "%3s %7s %7s", mes, dia, ano) == 3) {
                int d = atoi(dia);
                char data[20];
                snprintf(data, sizeof(data), "%02d/%s/%s", d, mesParaNumero(mes), ano);
                g->releaseDate = duplicarString(data);
            } else {
                g->releaseDate = duplicarString("01/01/0001");
            }
            break;
        }

        case 3:
            g->estimatedOwners = valor && *valor ? atoi(valor) : 0;
            break;

        case 4:
            if (valor && strcasecmp(valor, "Free to Play") == 0)
                g->price = 0.0f;
            else
                g->price = valor && *valor ? atof(valor) : 0.0f;
            break;

        case 5:
            g->supportedLanguages = removerColchetes(valor, &g->supportedLanguages_len);
            break;

        case 6:
            g->metacriticScore = valor && *valor ? atoi(valor) : -1;
            break;

        case 7:
            g->userScore = (!valor || !*valor || strcasecmp(valor, "tbd") == 0)
                               ? -1.0f
                               : atof(valor);
            break;

        case 8:
            g->achievements = valor && *valor ? atoi(valor) : 0;
            break;

        case 9:
            g->publishers = dividirPorVirgula(valor, &g->publishers_len);
            break;

        case 10:
            g->developers = dividirPorVirgula(valor, &g->developers_len);
            break;

        case 11:
            g->categories = removerColchetes(valor, &g->categories_len);
            break;

        case 12:
            g->genres = removerColchetes(valor, &g->genres_len);
            break;

        case 13:
            g->tags = removerColchetes(valor, &g->tags_len);
            break;
    }
}

void lerLinhaCsv(char* linha, int idx) {
    int campo = 0;
    char acumulado[20000];
    int tam = 0;
    bool asp = false;
    int col = 0;

    for (int j = 0; linha[j]; j++) {
        char c = linha[j];
        if (c == '"') { asp = !asp; continue; }
        if (c == '[') col++;
        if (c == ']') col--;

        if (c == ',' && !asp && col == 0) {
            acumulado[tam] = '\0';
            salvarCampo(&jogos[idx], campo++, acumulado);
            tam = 0;
        } else {
            acumulado[tam++] = c;
        }
    }

    acumulado[tam] = '\0';
    salvarCampo(&jogos[idx], campo, acumulado);
}

// --------------------------------------------------
// IMPRESSÃO
// --------------------------------------------------
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

// --------------------------------------------------
// PILHA
// --------------------------------------------------
typedef struct Celula {
    Game* game;
    struct Celula* prox;
} Celula;

typedef struct {
    Celula* topo;
} Pilha;

void iniciarPilha(Pilha* p) { p->topo = NULL; }

void empilhar(Pilha* p, Game* g) {
    Celula* nova = malloc(sizeof(Celula));
    nova->game = g;
    nova->prox = p->topo;
    p->topo = nova;
}

Game* desempilhar(Pilha* p) {
    if (!p->topo) return NULL;
    Celula* tmp = p->topo;
    Game* g = tmp->game;
    p->topo = tmp->prox;
    free(tmp);
    return g;
}

static int mostrarRecursivo(Celula* c, int idx) {
    if (!c) return idx;
    idx = mostrarRecursivo(c->prox, idx);
    printf("[%d] ", idx);
    imprimirJogo(c->game);
    return idx + 1;
}

void mostrarPilha(Pilha* p) { mostrarRecursivo(p->topo, 0); }

// --------------------------------------------------
// BUSCA
// --------------------------------------------------
Game* buscarPorId(Game* vetor, int total, int id) {
    for (int i = 0; i < total; i++)
        if (vetor[i].id == id)
            return &vetor[i];
    return NULL;
}

// --------------------------------------------------
// MAIN
// --------------------------------------------------
int main(void) {
    int capacidade = 2000, total = 0;
    jogos = malloc(sizeof(Game) * capacidade);

    FILE* arq = fopen("/tmp/games.csv", "r");
    if (!arq) {
        perror("games.csv");
        return 1;
    }

    char linha[20000];
    fgets(linha, sizeof(linha), arq);
    while (fgets(linha, sizeof(linha), arq) && total < capacidade) {
        linha[strcspn(linha, "\r\n")] = '\0';
        lerLinhaCsv(linha, total++);
    }
    fclose(arq);

    Pilha pilha;
    iniciarPilha(&pilha);

    char entrada[100];
    while (scanf("%s", entrada) != EOF) {
        if (strcmp(entrada, "FIM") == 0) break;
        int id = atoi(entrada);
        Game* g = buscarPorId(jogos, total, id);
        if (g) empilhar(&pilha, g);
    }

    int n;
    scanf("%d", &n);

    for (int i = 0; i < n; i++) {
        char cmd[5];
        scanf("%s", cmd);

        if (strcmp(cmd, "I") == 0) {
            int id;
            scanf("%d", &id);
            Game* g = buscarPorId(jogos, total, id);
            if (g) empilhar(&pilha, g);
        } else if (strcmp(cmd, "R") == 0) {
            Game* r = desempilhar(&pilha);
            if (r) printf("(R) %s\n", r->name);
        }
    }

    mostrarPilha(&pilha);
    free(jogos);
    return 0;
}
