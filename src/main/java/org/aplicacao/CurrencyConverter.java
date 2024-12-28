import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Converte {

    private static final String API_KEY = "a2fbfb264090f718e05884ec";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Solicitar moeda base
            System.out.print("Digite a moeda base (ex: USD): ");
            String baseCurrency = scanner.nextLine().toUpperCase();

            // Obter todas as taxas para a moeda base
            JsonObject rates = fetchRates(baseCurrency);

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Realizar conversão de moeda");
                System.out.println("2. Listar todas as taxas de câmbio");
                System.out.println("3. Sair");
                System.out.print("Escolha uma opção: ");

                int option = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                if (option == 1) {
                    // Realizar conversão de moeda
                    System.out.print("Digite a moeda de destino (ex: BRL): ");
                    String targetCurrency = scanner.nextLine().toUpperCase();

                    System.out.print("Digite o valor que deseja converter: ");
                    while (!scanner.hasNextDouble()) {
                        System.out.println("Por favor, insira um número válido.");
                        scanner.next(); // Limpa entrada inválida
                    }
                    double amount = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer

                    // Calcular conversão
                    if (rates.has(targetCurrency)) {
                        double rate = rates.get(targetCurrency).getAsDouble();
                        double convertedAmount = amount * rate;
                        System.out.printf("O valor de %.2f %s convertido para %s é: %.2f%n", amount, baseCurrency, targetCurrency, convertedAmount);
                    } else {
                        System.out.println("Moeda de destino não encontrada: " + targetCurrency);
                    }

                } else if (option == 2) {
                    // Listar todas as taxas de câmbio
                    System.out.println("Taxas de câmbio disponíveis:");
                    for (String key : rates.keySet()) {
                        System.out.printf("%s: %.4f%n", key, rates.get(key).getAsDouble());
                    }

                } else if (option == 3) {
                    // Sair do programa
                    System.out.println("Saindo do programa. Obrigado por usar o conversor!");
                    break;

                } else {
                    System.out.println("Opção inválida. Tente novamente.");
                }
            }

        } catch (Exception e) {
            System.out.println("Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Busca todas as taxas de câmbio para a moeda base.
     *
     * @param baseCurrency Moeda base (ex: USD)
     * @return Um JsonObject contendo as taxas de câmbio
     * @throws Exception Em caso de erro na API ou no processamento do JSON
     */
    public static JsonObject fetchRates(String baseCurrency) throws Exception {
        String urlStr = API_URL + baseCurrency;

        // Fazer a requisição HTTP
        URL url = new URL(urlStr);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestMethod("GET");
        request.connect();

        // Verificar código de resposta da API
        int responseCode = request.getResponseCode();
        if (responseCode == 200) {
            // Processar a resposta JSON
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonObject = root.getAsJsonObject();

            if (jsonObject.has("conversion_rates")) {
                return jsonObject.getAsJsonObject("conversion_rates");
            } else {
                throw new RuntimeException("Não foi possível obter as taxas de câmbio.");
            }
        } else {
            throw new RuntimeException("Erro ao acessar a API. Código de resposta: " + responseCode);
        }
    }
}

public void main() {
}
