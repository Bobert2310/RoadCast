import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RoadCastMain {

    public static final String API_KEY = "b9478773177fc290b1f32f1432103c10";
    public static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static String selectedLocation = "No Location Selected";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Welcome();
        pressEnter();
        changeLocationMenu(scanner);
        boolean Quit = false;

        while (!Quit) {
            MainMenu();
            int mainMenuChoice = UserInput(scanner, 4);

            if (mainMenuChoice == 1) {
                roadConditionsMenu(scanner);
            } else if (mainMenuChoice == 2) {
                changeLocationMenu(scanner);
            } else if (mainMenuChoice == 3) {
                saveReportToFile();
            } else if (mainMenuChoice == 4) {
                Quit = true;
            } else {
                System.out.println("Error: No option was selected. Please try again.");
            }
        }

        System.out.println("Thank you for using RoadCast.");
        System.out.println("Goodbye.");
        scanner.close();
    }

    public static void Welcome() {
        System.out.println("Welcome to RoadCast");
        System.out.println("Press the Enter key to continue");
    }
    public static void pressEnter() {
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int UserInput(Scanner scanner, int maxChoice) {
        int choice;

        while (true) {
            System.out.println("Please enter a number between 1 and " + maxChoice + ":");
            try {
                choice = Integer.parseInt(scanner.nextLine());

                if (choice >= 1 && choice <= maxChoice) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and " + maxChoice + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return choice;
    }
    public static void changeLocationMenu(Scanner scanner) {
        boolean validLocation = false;

        while (!validLocation) {
            System.out.println("\nPlease Enter Your Current City:");
            String userInputLocation = scanner.nextLine().trim();

            if (!userInputLocation.isEmpty() && isValidCity(userInputLocation)) {
                selectedLocation = userInputLocation;
                System.out.println("Location changed to: " + selectedLocation);
                validLocation = true;
            } else {
                System.out.println("Invalid city name. Please enter a valid city.");
            }
        }
    }

    public static boolean isValidCity(String cityName) {
        String urlString = String.format("%s?q=%s&appid=%s", API_URL, cityName, API_KEY);

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }


    public static void MainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. RoadCast Report");
        System.out.println("2. Reset Location");
        System.out.println("3. Save Report");
        System.out.println("4. Exit");
    }


    public static void roadConditionsMenu(Scanner scanner) {
        boolean backToMainMenu = false;

        while (!backToMainMenu) {
            displayReport();
            System.out.println("\n1. Print Report\n2. Back\n3. Exit");
            int roadConditionChoice = UserInput(scanner, 3);

            if (roadConditionChoice == 1) {
                saveReportToFile();
            } else if (roadConditionChoice == 2) {
                backToMainMenu = true;
            } else if (roadConditionChoice == 3) {
                System.out.println("Exiting the program. Goodbye!");
                System.exit(0);
            } else {
                System.out.println("Error: No option was selected. Please try again.");
            }
        }

    }

    public static void saveReportToFile() {
        String weatherData = getWeatherData();

        if (!weatherData.isEmpty()) {
            String filePath = "src/RoadCastReport.txt";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(weatherData);

                String description = jsonNode.get("weather").get(0).get("description").asText();
                double temperature = jsonNode.get("main").get("temp").asDouble();
                int humidity = jsonNode.get("main").get("humidity").asInt();
                double tempCelsius = temperature - 273.15;

                writer.write("Weather Conditions for " + selectedLocation + ":");
                writer.newLine();
                writer.write("Description: " + description);
                writer.newLine();
                writer.write(String.format("Temperature: %.0f°C", tempCelsius));
                writer.newLine();
                writer.write("Humidity: " + humidity + "%");
                writer.newLine();

                writer.write("\nTravel Recommendations:");
                writer.newLine();

                if (description.contains("rain")) {
                    writer.write("It's raining. Drive Carefully.");
                } else if (description.contains("snow")) {
                    writer.write("Snow is expected / present please advise and drive safely.");
                } else if (description.contains("few clouds")) {
                    writer.write("Cloudy weather but dry, roads should be normal.");
                } else if (temperature > 30) {
                    writer.write("It's hot! I hope your air conditioning works.");
                } else if (temperature < 0) {
                    writer.write("It's cold. Your windscreen may need defrosted.");
                } else {
                    writer.write("Weather conditions are moderate. Enjoy your travel!");
                }

                writer.newLine();
                writer.newLine();

                System.out.println("Weather report saved to " + filePath);
            } catch (Exception e) {
                System.out.println("Failed to save weather report. Error: " + e.getMessage());
            }
        } else {
            System.out.println("Weather data is empty. Unable to save report.");
        }
    }



    public static void displayReport() {
        String weatherData = getWeatherData();
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(weatherData);


            String description = jsonNode.get("weather").get(0).get("description").asText();
            double temperature = jsonNode.get("main").get("temp").asDouble();
            int humidity = jsonNode.get("main").get("humidity").asInt();
            double temp1 = (temperature - 273.15);
            System.out.println("Weather Conditions for " + selectedLocation + ":");
            System.out.println("Description: " + description);
            System.out.format("Temperature: %.0f" , temp1);
            System.out.print("°C");
            System.out.println("\nHumidity: " + humidity + "%");

            System.out.println("\nTravel Recommendations:");
            if (description.contains("rain")) {
                System.out.println("It's raining. Drive Carefully.");
            } else if (description.contains("snow")) {
                System.out.println("Snow is expected / present please advise and drive safely.");
            } else if (description.contains("few clouds")) {
                System.out.println("cloudy weather but dry, roads should be normal");
            } else if (temperature > 30) {
                System.out.println("It's hot! I hope your air conditioning works.");
            } else if (temperature < 0) {
                System.out.println("It's cold. your windscreen may need defrosted");
            } else {
                System.out.println("Weather conditions are moderate. Enjoy your travel!");
            }

        } catch (Exception e) {
            System.out.println("Failed to parse weather information. Error: " + e.getMessage());
        }
    }

    public static String getWeatherData() {
        String urlString = String.format("%s?q=%s&appid=%s", API_URL, selectedLocation, API_KEY);

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }

                reader.close();
                return response.toString();
            } else {
                System.out.println("Failed to retrieve weather information for " + selectedLocation +
                        ". Response code: " + responseCode);
                return "";
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve weather information. Error: " + e.getMessage());
            return "";
        }
    }
}
