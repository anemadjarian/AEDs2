import java.util.*;

class VanEscolar {
    static class Aluno {
        String nome;
        char regiao;
        int distancia;

        Aluno(String nome, char regiao, int distancia) {
            this.nome = nome;
            this.regiao = regiao;
            this.distancia = distancia;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Aluno> lista = new ArrayList<>();

        while (sc.hasNextLine()) {
            String linha = sc.nextLine().trim();
            if (linha.isEmpty()) continue;

            String[] partes = linha.split(" ");
            if (partes.length < 3) continue;

            String nome = partes[0];
            char regiao = partes[1].charAt(0);
            int distancia = Integer.parseInt(partes[2]);

            lista.add(new Aluno(nome, regiao, distancia));
        }

        lista.sort(Comparator
            .comparingInt((Aluno a) -> a.distancia)
            .thenComparing(a -> a.regiao)
            .thenComparing(a -> a.nome)
        );

        for (Aluno a : lista) {
            System.out.println(a.nome);
        }

        sc.close();
    }
}
