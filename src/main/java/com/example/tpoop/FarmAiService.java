package com.example.tpoop;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
//import dev.langchain4j.model.chat.ChatLanguageModel;

public class FarmAiService {

    private final FarmAiAssistant assistant;
    private final String initError;

    public FarmAiService(Ferme ferme) {
        FarmAiAssistant built = null;
        String error = null;

        try {
            System.out.println("✅ Connexion à Ollama (modèle local, gratuit)");



// Votre modèle Ollama implémente ChatLanguageModel
            var model = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("qwen2.5:3b")
                    .temperature(0.3)
                    .build();

// Utilisez .chatLanguageModel()
            built = AiServices.builder(FarmAiAssistant.class)
                    .chatLanguageModel(model)           // ← ICI : chatLanguageModel
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(50))
                    .tools(new FarmAiTools(ferme))
                    .build();

            System.out.println("✅ Assistant IA local prêt - 100% gratuit !");

        } catch (Exception e) {
            error = "Ollama n'est pas en cours d'exécution.\n" +
                    "1. Téléchargez Ollama sur https://ollama.com\n" +
                    "2. Lancez 'ollama run qwen2.5:3b' dans un terminal\n" +
                    "3. Redémarrez l'application\n" +
                    "Erreur : " + e.getMessage();
        }

        this.assistant = built;
        this.initError = error;
    }

    public boolean isConfigured() {
        return assistant != null;
    }

    public String getInitError() {
        return initError;
    }

    public String chat(String userMessage) {
        if (!isConfigured()) {
            return initError;
        }
        try {
            return assistant.chat(userMessage);
        } catch (Exception e) {
            return "❌ Erreur : " + e.getMessage();
        }
    }
}