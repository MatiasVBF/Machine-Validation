import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Chatbot {

    private static DoccatModel model;

    public static void main(String[] args) {
        try {
            trainModel();
            String response = getResponse("How are you?");
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void trainModel() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream("TrainingData.txt")))) {
            ObjectStream<String> lineStream = new PlainTextByLineStream(() -> reader, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

            model = DocumentCategorizerME.train("en", sampleStream);

            try (FileOutputStream modelOut = new FileOutputStream("chatbot-model.bin")) {
                model.serialize(modelOut);
            }
        }
    }

    private static String getResponse(String input) throws IOException {
        try (FileInputStream modelIn = new FileInputStream("chatbot-model.bin")) {
            DoccatModel model = new DoccatModel(modelIn);
            DocumentCategorizerME categorizer = new DocumentCategorizerME(model);

            double[] outcomes = categorizer.categorize(input);
            String category = categorizer.getBestCategory(outcomes);

            switch (category) {
                case "How are you?":
                    return "I am fine.";
                case "What is your name?":
                    return "My name is Bot.";
                case "How is the weather?":
                    return "The weather is sunny.";
                default:
                    return "I don't understand.";
            }
        }
    }
}
