package com.nextfit.nutrition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Point d'entrée de l'application.
 * Étend SpringBootServletInitializer pour le déploiement en WAR sur Tomcat 9.
 */
@SpringBootApplication
public class NutritionApplication extends SpringBootServletInitializer {

    /**
     * Méthode appelée par Tomcat 9 lors du déploiement du WAR.
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NutritionApplication.class);
    }

    /**
     * Point d'entrée standard (pour lancer en standalone si besoin).
     */
    public static void main(String[] args) {
        SpringApplication.run(NutritionApplication.class, args);
    }
}
